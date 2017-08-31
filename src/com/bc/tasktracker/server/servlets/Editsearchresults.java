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

import com.bc.appcore.exceptions.TaskExecutionException;
import com.bc.appcore.parameter.InvalidParameterException;
import com.bc.appcore.parameter.ParameterException;
import com.bc.tasktracker.server.AttributeNames;
import com.bc.tasktracker.server.ParameterManager;
import com.bc.tasktracker.server.WebApp;
import com.bc.tasktracker.jpa.entities.master.Task_;
import com.bc.tasktracker.jpa.entities.master.Taskresponse_;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 15, 2017 12:23:47 AM
 */
@WebServlet(name = "Editsearchresults", urlPatterns = {"/editsearchresults"})
public class Editsearchresults extends BaseServlet {
    
    private static interface RowUpdateConfig {
        String NOACTION = "noaction";
        String PERSIST = "persist";
        String MERGE = "merge";
        String REMOVE = "remove";
        String getAction();
        int getRow();
        int getColumn();
        Map<String, String> getParameters();
        
//<%-- @related searchresultsForm_inputNameFormat --%>  
//<%-- Format e.g for action = add, separator = '_' --%>
//<%-- add_[rowIndex]_[columnIndex|columnName]_key0_val0 ... keyN_valN --%>    
//<%-- Actions supported = persist,merge,remove,noaction,[specific class name] --%>
        public static RowUpdateConfig from(String parameterName, String separator) {
            parameterName = parameterName.trim();
            final RowUpdateConfigImpl internal = new RowUpdateConfigImpl();
            if(parameterName.startsWith(NOACTION)) {
                internal.rawValue = parameterName;
                internal.action = NOACTION;
                internal.row = -1;
                internal.column = -1;
                internal.parameters = Collections.EMPTY_MAP;
            }else{
                final String [] parts = parameterName.split(Pattern.quote(separator.trim()));
                internal.rawValue = parameterName;
                internal.action = parts[0];
                internal.row = Integer.parseInt(parts[1]);
                internal.column = Integer.parseInt(parts[2]);
                internal.parameters = new HashMap(parts.length, 1.0f);
                String key = null;
                for(int i=3; i<parts.length; i++) {
                    if(key == null) {
                        internal.parameters.put(parts[i], null);
                        key = parts[i];
                    }else{
                        internal.parameters.put(key, parts[i]);
                        key = null;
                    }
                }
            }
            return internal;
        }
        
    }
    private static class RowUpdateConfigImpl implements RowUpdateConfig{
        private String rawValue;
        private String action;
        private int row;
        private int column;
        private Map<String, String> parameters;
        @Override
        public String getAction() {
            return action;
        }
        @Override
        public int getRow() {
            return row;
        }
        @Override
        public int getColumn() {
            return column;
        }
        @Override
        public Map<String, String> getParameters() {
            return parameters;
        }
        @Override
        public String toString() {
            return Objects.requireNonNull(rawValue);
        }
    }
    
    private transient final Logger logger = Logger.getLogger(Editsearchresults.class.getName());
    
    public Editsearchresults() { }

    @Override
    public void doProcessRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try{
            
            final Integer updateCount = this.execute(request, response);
            
            request.setAttribute(AttributeNames.USER_MESSAGE, "Success");

            final String successPage = this.getSuccessPage(request, updateCount);

            request.getRequestDispatcher(successPage).forward(request, response);
            
        }catch(TaskExecutionException | ParameterException e) {
            
            request.setAttribute(AttributeNames.USER_MESSAGE, e.getUserMessage());
            
            final String errorPage = this.getErrorPage(request, e);

            request.getRequestDispatcher(errorPage).forward(request, response);
        }
    }

    protected Integer execute(HttpServletRequest request, HttpServletResponse response) 
            throws TaskExecutionException, ParameterException {

        response.setContentType("text/html;charset=UTF-8");

        final String separatorName = "rowFromColumnIndexSeparator";
        
        final String separator = Objects.requireNonNull(request.getParameter(separatorName));
        
        final Enumeration<String> paramNames = request.getParameterNames();
        
        int output = 0;
        
        while(paramNames.hasMoreElements()) {
            
            final String paramName = paramNames.nextElement();
            
            final String paramValue = request.getParameter(paramName);
            
            logger.fine(() -> paramName + '=' + paramValue);
            
            if(separatorName.equals(paramName)) {
                continue;
            }
            
            RowUpdateConfig updateConfig;
            try{
                
                updateConfig = RowUpdateConfig.from(paramName, separator);
            }catch(RuntimeException e) {
                updateConfig = null;
                logger.log(Level.WARNING, "While creating "+RowUpdateConfig.class.getName()+
                        " for: "+paramName+", encountered exception: {0}", e.toString());
            }
            if(updateConfig != null) {
                output += this.update(request, updateConfig, paramName, paramValue);
            }
        }
        
        return output;
    } 
    
    private int update(HttpServletRequest request, RowUpdateConfig updateConfig, 
            String paramName, String paramValue) 
            throws ParameterException, TaskExecutionException {
        
        logger.fine(() -> paramName + '=' + paramValue);
        
        final String ACTION = updateConfig.getAction();
        
        Class actionClass = null;
        switch(ACTION) {
            case RowUpdateConfig.NOACTION: 
                return 0;
            case RowUpdateConfig.PERSIST:    
            case RowUpdateConfig.MERGE:
            case RowUpdateConfig.REMOVE:    
                break;
            default:
                try{
                    actionClass = Class.forName(ACTION);
                }catch(ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
        }
        
        final int output;
        
        final WebApp app = this.getApp(request);
        
        if(actionClass != null) {
            
            final Map params;
            if(WebApp.ADD_TASKRESPONSE.equals(ACTION)) {
                
                params = this.getAddTaskresponseParams(request, updateConfig, paramValue);
                
            }else{
                
                params = updateConfig.getParameters();
            }
            
            final Object result = app.getAction(ACTION).execute(app, params);

            output = result == null ? 0 : 1;
            
        }else{
            
            final ParameterManager pm = app.getOrException(ParameterManager.class);

            final TableModel tableModel = (TableModel)request.getSession().getAttribute(AttributeNames.SEARCH_RESULTS_PAGE_TABLE_MODEL);

            logger.fine(() -> "Current search result page has "+tableModel.getRowCount()+" rows and  "+tableModel.getColumnCount()+" columns.");
            
            final Class columnClass = tableModel.getColumnClass(updateConfig.getColumn());
        
            final Object result = pm.parse(app, columnClass, paramName, paramValue);
        
            tableModel.setValueAt(result, updateConfig.getRow(), updateConfig.getColumn());
            
            output = 1;
        }

        app.getAction(WebApp.REFRESH_SEARCHRESULTS).execute(
                app, 
                Collections.singletonMap(
                        AttributeNames.SEARCH_RESULTS, 
                        request.getSession().getAttribute(AttributeNames.SEARCH_RESULTS)
                )
        );
        
        return output;
    }
    
    
    private Map<String, Object> getAddTaskresponseParams(
            HttpServletRequest request, RowUpdateConfig updateConfig, String paramValue) 
            throws InvalidParameterException {
        
        final WebApp app = this.getApp(request);
        
        final Map<String, Object> params = new HashMap<>(4, 0.75f);

        final String taskidName = Task_.taskid.getName();
        final String taskidSval = updateConfig.getParameters().get(taskidName);
        final ParameterManager pm = app.getOrException(ParameterManager.class);
        params.put(taskidName, pm.parse(app, Integer.class, taskidName, taskidSval));
        params.put(Taskresponse_.response.getName(), paramValue);
        
        return params;
    }

    private int update_old(HttpServletRequest request, RowUpdateConfig updateConfig, 
            String paramName, String paramValue) 
            throws ParameterException, TaskExecutionException {
        
        if(RowUpdateConfig.NOACTION.equals(updateConfig.getAction())) {
            return 0;
        }
        
        final WebApp app = this.getApp(request);
        
        final ParameterManager pm = app.getOrException(ParameterManager.class);
        
        final TableModel tableModel = (TableModel)request.getSession().getAttribute(AttributeNames.SEARCH_RESULTS_PAGE_TABLE_MODEL);
        
        logger.fine(() -> "Current search result page has "+tableModel.getRowCount()+" rows and  "+tableModel.getColumnCount()+" columns.");
        
        int output;
        
        paramValue = paramValue == null ? null : paramValue.trim();
        
        if(RowUpdateConfig.PERSIST.equals(updateConfig.getAction()) && updateConfig.getColumn() == -1) {
            
            if(paramValue == null || paramValue.isEmpty()) {
                output = 0;
            }else{
                
                final Map<String, Object> params = new HashMap<>(4, 0.75f);

                final String taskidName = Task_.taskid.getName();
                final String taskidSval = updateConfig.getParameters().get(taskidName);
                params.put(taskidName, pm.parse(app, Integer.class, taskidName, taskidSval));
                params.put(Taskresponse_.response.getName(), paramValue);

                final Object result = app.getAction(WebApp.ADD_TASKRESPONSE).execute(app, params);

                output = result == null ? 0 : 1;
            }
        }else {
            
            final Class columnClass = tableModel.getColumnClass(updateConfig.getColumn());
        
            final Object result = pm.parse(app, columnClass, paramName, paramValue);
        
            tableModel.setValueAt(result, updateConfig.getRow(), updateConfig.getColumn());
            
            output = 1;
        }
        
        app.getAction(WebApp.REFRESH_SEARCHRESULTS).execute(
                app, 
                Collections.singletonMap(
                        AttributeNames.SEARCH_RESULTS, 
                        request.getSession().getAttribute(AttributeNames.SEARCH_RESULTS)
                )
        );
        
        return output;
    }
}
