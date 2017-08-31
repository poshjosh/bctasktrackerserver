<%-- 
    Document   : page_searchresults
    Created on : Aug 24, 2017, 12:55:45 PM
    Author     : Josh
--%>
<%@tag trimDirectiveWhitespaces="true" description="default page" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="/META-INF/tlds/bctt" prefix="bctt"%>

<%@attribute name="pageTitle" required="true"%>
<%@attribute name="pageHeading" required="false"%>
<%@attribute name="logoAlt" required="false"%>
<%@attribute name="logoSrc" required="true"%>
<%@attribute name="loginPage" required="false"%>

<c:choose>
  <c:when test="${SearchResults == null || empty SearchResults.pages}">
    <c:set var="srSearchSize" value="0"/>      
  </c:when>  
  <c:otherwise>
    <c:set var="srSearchSize" value="${fn:length(SearchResults.pages)}"/>        
  </c:otherwise>
</c:choose>

<c:set var="srDoNoDisplayNumberTypes" value="true" scope="page"/>
<c:set var="srSearchPath" value="${pageContext.servletContext.contextPath}/search" scope="page"/>
<c:set var="srEditable" value="${param.editable == null ? false : param.editable}" scope="page"/>
<c:set var="srCurrentInputPrefix" value="${srEditable ? 'merge' : 'noaction'}" scope="page"/>
<c:set var="srTextareaRowCount" value="3" scope="page"/>
<c:set var="x" value="_" scope="page"/>

<bctt:page pageTitle="${pageTitle}" pageHeading="${pageHeading}" 
             logoAlt="${logoAlt}" logoSrc="${logoSrc}" loginPage="${loginPage}">
    <jsp:body>
        
    <%@include file="/META-INF/jspf/searchform.jspf"%>
        
    <c:choose>
      <c:when test="${srSearchSize != 0}">
          
        <p><tt><bctt:paginationMessage searchResultsBean="${SearchResults}" searchServletPath="${srSearchPath}"/></tt></p> 

        <jsp:useBean id="CurrentPage" class="com.bc.tasktracker.server.beans.TableModelBean" scope="session"/>
        <jsp:setProperty name="CurrentPage" property="request" value="<%=request%>"/>
        
        <c:choose>
            <c:when test="${mobile}"><c:set var="srDatePattern" value="dd MMM"/></c:when>
            <c:otherwise><c:set var="srDatePattern" value="dd MMM yy"/></c:otherwise>
        </c:choose>
        
        <form id="editsearchresultsform" method="post" action="${pageContext.servletContext.contextPath}/editsearchresults">

            <input class="button0" type="submit" value="Submit Update"/>

            <input type="hidden" name="rowFromColumnIndexSeparator" value="${x}"/>
            
            <table id="searchresultstable" class="smaller">
              <thead>
                <tr>  
                  <c:forEach begin="0" end="${CurrentPage.columnCount-1}" varStatus="vsCol">
                    <jsp:setProperty name="CurrentPage" property="columnIndex" value="${vsCol.index}"/>
                    <c:choose>
                      <c:when test="${srDoNoDisplayNumberTypes && CurrentPage.numberType}">
                        <th class="doNotDisplay"></th>
                      </c:when>
                      <c:when test="${CurrentPage.columnName == 'timeopened'}">
                        <th class="doNotDisplay"></th>  
                      </c:when>
                      <c:when test="${CurrentPage.columnName == 'timeclosed'}">
                        <th>Open/Close</th>  
                      </c:when>
                      <c:when test="${CurrentPage.columnLabel == 'Remarks'}">
                        <th class="doNotDisplay"></th>
                      </c:when>
                      <c:otherwise><th>${CurrentPage.columnLabel}</th></c:otherwise>
                    </c:choose>
                  </c:forEach>
                  <th>Add Response</th>
                </tr> 
              </thead>
              <tbody>
                  
                <c:forEach begin="0" end="${CurrentPage.rowCount-1}" varStatus="vsRow">
                    
                <jsp:setProperty name="CurrentPage" property="rowIndex" value="${vsRow.index}"/> 
                
                  <c:forEach begin="0" end="${CurrentPage.columnCount-1}" varStatus="vsCol">
                    <jsp:setProperty name="CurrentPage" property="columnIndex" value="${vsCol.index}"/>  
                    
                    <c:if test="${CurrentPage.idColumn}">
                        <bctt:selectorTag tableName="task" columnName="taskid" 
                                            columnValue="${CurrentPage.valueAt}" singleResult="true"/> 
                        <c:set var="srCurrentDocid" value="${selectorTag_result.doc.docid}"/>
                    </c:if>
                    
                    <c:if test="${CurrentPage.columnName == 'timeclosed'}">
                        <c:set var="srCurrentTaskClosed" value="${CurrentPage.valueAt != null}"/>    
                    </c:if>    
                  </c:forEach>  
                
                <tr class="${srCurrentTaskClosed?'yang':'ying'}">
                    
                  <c:forEach begin="0" end="${CurrentPage.columnCount-1}" varStatus="vsCol">
                      
                    <jsp:setProperty name="CurrentPage" property="columnIndex" value="${vsCol.index}"/>  
                    <c:if test="${CurrentPage.idColumn}">
                      <c:set var="srCurrentTaskid" value="${CurrentPage.valueAt}"/>
                    </c:if>
                    
<%-- @related searchresultsForm_inputNameFormat see notes on this page --%>                      
                    <c:set var="srCurrentInputName" value="${srCurrentInputPrefix}${x}${vsRow.index}${x}${vsCol.index}"/>
                    <c:choose>
                      <c:when test="${srDoNoDisplayNumberTypes && CurrentPage.numberType}">
                        <td class="doNotDisplay"></td>  
                      </c:when>
                      <c:when test="${CurrentPage.columnName == 'timeopened'}">
                        <td class="doNotDisplay"></td>  
                      </c:when>
                      <c:when test="${CurrentPage.columnName == 'timeclosed'}">
                        <td>
                        <c:choose>
                          <c:when test="${CurrentPage.valueAt != null}">
                            <input class="button0" type="submit" value=" Open >"
                                   name="com.bc.tasktracker.server.actions.OpenTask${x}${vsRow.index}${x}-1${x}taskid${x}${srCurrentTaskid}"/>  
                          </c:when>
                          <c:otherwise>
                            <input class="button0" type="submit" value="Close >" 
                                   name="com.bc.tasktracker.server.actions.CloseTask${x}${vsRow.index}${x}-1${x}taskid${x}${srCurrentTaskid}"/>    
                          </c:otherwise>
                        </c:choose> 
                        </td>
                      </c:when>
                      <c:when test="${CurrentPage.columnLabel == 'Remarks'}">
                        <td class="doNotDisplay"></td>  
                      </c:when>
                      <c:when test="${CurrentPage.dateType}">
                        <td>

                          <fmt:formatDate value="${CurrentPage.valueAt}" pattern="${srDatePattern}" var="srFormattedDate"/>
                          <c:choose>
                            <c:when test="${!srEditable}">${srFormattedDate}</c:when>   
                            <c:otherwise><input type="text" name="${srCurrentInputName}" value="${srFormattedDate}"/></c:otherwise>
                          </c:choose>
                            
                        </td>  
                      </c:when>
                      <c:when test="${CurrentPage.columnLabel == 'Response 1' || CurrentPage.columnName == 'Response 2'}">
                        <td>
                            
                          <c:choose>
                            <c:when test="${!srEditable}">${CurrentPage.valueAt}</c:when>   
                            <c:otherwise>
                              <textarea rows="${srTextareaRowCount}" name="${srCurrentInputName}">${CurrentPage.valueAt}</textarea>
                            </c:otherwise>
                          </c:choose>
                            
                        </td>
                      </c:when>
                      <c:when test="${CurrentPage.selectionType}">
                        <td>
                            
                          <c:choose>
                            <c:when test="${!srEditable}">
                                <c:forEach var="Selection" items="${CurrentPage.selectionValues}">
                                  <c:if test="${CurrentPage.valueAt == Selection.value}">
                                    ${Selection.displayValue}
                                  </c:if>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <select name="${srCurrentInputName}" size="1">
<%-- We use empty string here as when we left out the value altogether it used the contents of the option tag --%>        
                                  <c:forEach var="Selection" items="${CurrentPage.selectionValues}">
                                    <c:choose>
                                      <c:when test="${CurrentPage.valueAt == Selection.value}">
                                        <option selected value="${Selection.displayValue}">${Selection.displayValue}</option>
                                      </c:when>
                                      <c:otherwise>
                                        <option value="${Selection.displayValue}">${Selection.displayValue}</option>
                                      </c:otherwise>
                                    </c:choose>
                                  </c:forEach>
                                </select>
                            </c:otherwise>
                          </c:choose>
                            
                        </td>
                      </c:when>
                      <c:otherwise>
                        <td>
                            
                          <c:choose>
                            <c:when test="${!srEditable}">${CurrentPage.valueAt}</c:when>   
                            <c:otherwise>
                              <textarea rows="${srTextareaRowCount}" name="${srCurrentInputName}">${CurrentPage.valueAt}</textarea>
                            </c:otherwise>
                          </c:choose>
                            
                        </td>
                      </c:otherwise>
                    </c:choose>
                  </c:forEach>
                    
                  <td>
<%-- @related searchresultsForm_inputNameFormat --%>  
<%-- Format e.g for action = add, separator = '_' --%>
<%-- add_[rowIndex]_[columnIndex|columnName]_key0_val0 ... keyN_valN --%>    
<%-- Actions supported = persist,merge,remove,noaction,[specific class name] --%>
                    <textarea name="com.bc.tasktracker.actions.AddTaskresponse${x}${vsRow.index}${x}-1${x}taskid${x}${srCurrentTaskid}" rows="${srTextareaRowCount}">
                    </textarea>    
                    <input style="border:0; float:right;" type="submit" value=">"/>
                    
                  </td>
                    
                </tr>
                </c:forEach>
              </tbody>
            </table>
            
            <input class="button0" type="submit" value="Submit Update"/>              
            
        </form>
        
        <b><bctt:paginationLinks pagePerBatch="10" searchResultsBean="${SearchResults}" searchServletPath="${srSearchPath}"/></b> 

      </c:when>    
      <c:otherwise>
          
        <p>${srSearchSize} search results</p>
        
        <a href="${pageContext.servletContext.contextPath}/addtask.jsp">Add Task</a>
        
      </c:otherwise>
    </c:choose>          
        <jsp:doBody/>  
        
    </jsp:body>
</bctt:page>

    
    
