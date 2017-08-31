/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bc.tasktracker.server.servlets;

import com.bc.appcore.AppCore;
import com.bc.appcore.jpa.model.ResultModel;
import com.bc.appcore.table.model.SearchResultsTableModel;
import com.bc.jpa.dao.SelectDao;
import com.bc.jpa.search.SearchResults;
import com.bc.tasktracker.jpa.entities.master.Task;
import com.bc.tasktracker.jpa.entities.master.Task_;
import com.bc.tasktracker.server.AttributeNames;
import com.bc.tasktracker.jpa.TasktrackerSearchContext;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.table.TableModel;

/**
 * @author Josh
 */
@WebServlet(name = "Search", urlPatterns = {"/search"})
public class Search extends BaseServlet {
    
    private transient static final Logger logger = Logger.getLogger(Search.class.getName());
    
    public Search() { }

    @Override
    public void doProcessRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        final SearchResults searchResults = this.execute(request, response);

        final String successPage = this.getSuccessPage(request, searchResults);

        request.getRequestDispatcher(successPage).forward(request, response);
    }
    
    protected SearchResults execute(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("text/html;charset=UTF-8");

        final String pageStr = request.getParameter("page");

        final AppCore app = (AppCore)request.getServletContext().getAttribute(AttributeNames.APP);
        
        final HttpSession session = request.getSession();

        final SearchResults previousSearchResults = (SearchResults)session.getAttribute(AttributeNames.SEARCH_RESULTS);
        logger.fine(() -> "Previous search: " + previousSearchResults);
        final SearchResults searchResults = pageStr != null ? 
                previousSearchResults : this.search(app, request);

        final int pageNumber = pageStr == null ? 0 : Integer.parseInt(pageStr);
        
        final int numberOfPages = searchResults.getSize() == 0 ? 0 : 1;

        this.update(session, searchResults, pageNumber, numberOfPages);
        
        return searchResults;
    }
    
    public SearchResults<Task> search(AppCore app, HttpServletRequest request) {
        
        logger.finer("#search(..) Creating search");
        
//        final SelectDao<Task> dao = this.buildSelector(app, Task.class, request);
//        final BaseSearchResults searchResults = new BaseSearchResults(dao, 20, true);
        final SearchResults<Task> searchResults = this.search(app, Task.class, request);
        
        logger.finer("#search(..) Done creating search");
        
        return searchResults;
    }

    public void update(HttpSession session, SearchResults searchResults) {
        final int numberOfPages = searchResults.getSize() == 0 ? 0 : 1;
        this.update(session, searchResults, 0, numberOfPages);
    }
    
    public void update(HttpSession session, SearchResults searchResults, int pageNumber, int numberOfPages) {
        
        logger.finer("#update(..) Updating searchresults session");
        
        final AppCore app = (AppCore)session.getServletContext().getAttribute(AttributeNames.APP);
        
        final SearchResults previousSearchResults = (SearchResults)session.getAttribute(AttributeNames.SEARCH_RESULTS);

        new Thread("Close_previous_searchresults_Thread") {
            @Override
            public void run() {
                if(!searchResults.equals(previousSearchResults) && previousSearchResults instanceof AutoCloseable) {
                    try{
                        logger.fine("Closing previous search result");
                        ((AutoCloseable)previousSearchResults).close();
                    }catch(Exception e) {
                        logger.log(Level.WARNING, "Exception closing previous search results", e);
                    }
                }
            }
        }.start();

        logger.log(Level.FINE, "Search results size: {0}", searchResults.getSize());

        session.setAttribute(AttributeNames.SEARCH_RESULTS, searchResults);

        logger.log(Level.FINER, "Page offset: {0}", pageNumber);

        final ResultModel resultModel = app.getResultModel(Task.class, null);
        Objects.requireNonNull(resultModel);
        
        TableModel tableModel;
        if(searchResults.getPageCount() > pageNumber) {
            searchResults.setPageNumber(pageNumber);
            tableModel = new SearchResultsTableModel(searchResults, resultModel, pageNumber, numberOfPages);
        }else{
            tableModel = new SearchResultsTableModel(searchResults, resultModel);
        }

        session.setAttribute(AttributeNames.SEARCH_RESULTS_PAGE_TABLE_MODEL, tableModel);

        if(pageNumber < searchResults.getPageCount() - 1) {
            new Thread("Premptively_Load_SearchPage_"+(pageNumber+1)+"_Thread") {
                @Override
                public void run() {
                    try{
                        searchResults.getPage(pageNumber + 1);
                    }catch(RuntimeException e) {
                        logger.log(Level.WARNING, "Unexpected exception", e);
                    }
                }
            }.start();
        }
        
        logger.finer("#update(..) Done updating searchresults session");
    }
    
    public <T> SearchResults<T> search(AppCore app, Class<T> resultType, HttpServletRequest request) {
        
        final String query = request == null ? null : request.getParameter("query");
        final Date deadlineFrom = null;
        final Date deadlineTo = null;
        final String who = request == null ? null : request.getParameter(Task_.reponsibility.getName());
        final Boolean opened = this.getBooleanParameter(request, "opened");
        final Boolean closed = this.getBooleanParameter(request, "closed");
        final Date from = null;
        final Date to = null;
        
        final TasktrackerSearchContext searchContext = (TasktrackerSearchContext)app.getSearchContext(resultType);
        
        final SelectDao selectDao = searchContext.getSelectDaoBuilder()
                .jpaContext(app.getJpaContext())
                .resultType(resultType==null?Task.class:resultType)
                .textToFind(query==null || query.isEmpty() ? null : query)
                .deadlineFrom(deadlineFrom)
                .deadlineTo(deadlineTo)
                .from(from)
                .to(to)
                .opened(opened)
                .closed(closed)
                .who(who)
                .build();
        
        final SearchResults<T> searchResults = searchContext.getSearchResults(selectDao);
        
        logger.fine(() -> "Search size: " + (searchResults==null?null:searchResults.getSize()));
        logger.finer(() -> "Search Results: "+searchResults);
        
        return searchResults;
    }  
    
    private Boolean getBooleanParameter(HttpServletRequest request, String name) {
        final String str = request == null ? null : request.getParameter(name);
        final Boolean bool;
        if(!this.isNullOrEmpty(str)) {
            bool = Boolean.parseBoolean(str);
        }else{
            bool = null;
        }
        return bool;
    }
    
    private boolean isNullOrEmpty(Object text) {
        return text == null || "".equals(text);
    }
}
/**
 * 
    public <T> SelectDao<T> buildSelector(AppCore app, Class<T> resultType, HttpServletRequest request) {
        
        final String query = request == null ? null : request.getParameter("query");
        final Date deadlineFrom = null;
        final Date deadlineTo = null;
        final String who = request == null ? null : request.getParameter(Task_.reponsibility.getName());
        final Appointment appointment;
        if(this.isNullOrEmpty(who)) {
            appointment = null;
        }else{
            appointment = app.getJpaContext().getBuilderForSelect(Appointment.class)
                    .where(Appointment.class, Appointment_.appointment.getName(), who)
                    .getSingleResultAndClose();
        }
        final Boolean opened = null;
        final String sval = request == null ? null : request.getParameter("closed");
        final Boolean closed;
        if(!this.isNullOrEmpty(sval)) {
            closed = Boolean.parseBoolean(sval);
        }else{
            closed = null;
        }
        final Date from = null;
        final Date to = null;
        
        final boolean hasQuery = query != null && !query.isEmpty();
        final boolean joinDoc = hasQuery;
        final boolean joinTr = hasQuery || deadlineFrom != null || deadlineTo != null;
        
        final String q = !hasQuery ? null : '%'+query+'%';
        
        final BuilderForSelect<T> dao = new BuilderForSelectImpl(
                app.getJpaContext().getEntityManager(resultType), resultType);
        
        final CriteriaBuilder cb = dao.getCriteriaBuilder();
        
        final CriteriaQuery cq = dao.getCriteriaQuery();
        
        cq.distinct(true);
        
        final List<Predicate> likes = !hasQuery ? null : new ArrayList();
        
        final Root task = cq.from(Task.class); 
        if(likes != null) {
            likes.add(cb.like(task.get(Task_.description), q));
        }
        
        final Join<Task, Doc> taskDoc = !joinDoc ? null : task.join(Task_.doc); 
        if(likes != null && taskDoc != null) {
            likes.add(cb.like(taskDoc.get(Doc_.subject), q));
            likes.add(cb.like(taskDoc.get(Doc_.referencenumber), q));
        }
        
        // If you don't specify lef join here then some searches will return incorrect results
        final Join<Task, Taskresponse> taskTr = !joinTr ? null : task.join(Task_.taskresponseList, JoinType.LEFT);
        if(likes != null && taskTr != null) {
            likes.add(cb.like(taskTr.get(Taskresponse_.response), q));  
        }

        final List<Predicate> where = new ArrayList<>();
        
        if(likes != null) {
            where.add(cb.or(likes.toArray(new Predicate[0])));
        }
        
        if(appointment != null) {
            where.add(cb.equal(task.get(Task_.reponsibility), appointment));
        }
        if(opened != null) {
            if(opened) {
                where.add(cb.isNotNull(task.get(Task_.timeopened)));
            }else{
                where.add(cb.isNull(task.get(Task_.timeopened)));
            }
        }
        if(closed != null) {
            if(closed) {
                where.add(cb.isNotNull(task.get(Task_.timeclosed)));
            }else{
                where.add(cb.isNull(task.get(Task_.timeclosed)));
            }
        }
        if(from != null) {
            where.add(cb.greaterThanOrEqualTo(task.get(Task_.timeopened), from));
        }        
        if(to != null) {
            where.add(cb.lessThan(task.get(Task_.timeopened), to));
        } 
        
        if(deadlineFrom != null || deadlineTo != null) {
            where.add(cb.isNotNull(taskTr.get(Taskresponse_.deadline)));
        }
        
        if(deadlineFrom != null) {
            final Subquery<Integer> subquery = cq.subquery(Integer.class);
            final Root<Taskresponse> subqueryRoot = subquery.from(Taskresponse.class);
            subquery.select(subqueryRoot.get(Taskresponse_.taskresponseid));
            subquery.where(cb.equal(task, subqueryRoot.get(Taskresponse_.task)));
//            subquery.groupBy(subqueryRoot.get(Taskresponse_.taskresponseid)); 
            subquery.having(cb.greaterThanOrEqualTo(cb.greatest(subqueryRoot.<Date>get(Taskresponse_.deadline)), deadlineFrom));
            where.add(cb.exists(subquery));
        }        
        
        if(deadlineTo != null) {
            final Subquery<Integer> subquery = cq.subquery(Integer.class);
            final Root<Taskresponse> subqueryRoot = subquery.from(Taskresponse.class);
            subquery.select(subqueryRoot.get(Taskresponse_.taskresponseid));
            subquery.where(cb.equal(task, subqueryRoot.get(Taskresponse_.task)));
//            subquery.groupBy(subqueryRoot.get(Taskresponse_.taskresponseid)); 
            subquery.having(cb.lessThan(cb.greatest(subqueryRoot.<Date>get(Taskresponse_.deadline)), deadlineTo));
            where.add(cb.exists(subquery));
        } 

        if(!where.isEmpty()) {
            cq.where( cb.and(where.toArray(new Predicate[0])) );
        }

        cq.orderBy(cb.desc(task.get(Task_.taskid))); 

        return dao;
    }    
 * 
 */