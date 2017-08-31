<%-- 
    Document   : entityOptions
    Created on : Aug 16, 2017, 3:41:59 AM
    Author     : Josh
--%>
<%@tag description="put the tag description here" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@attribute name="eoTableName" required="true"%>
<%@attribute name="eoInputName" required="false"%>
<%@attribute name="eoInputClass" required="true"%>
<%@attribute name="eoDefaultOptionName" required="false"%>
<%@attribute name="eoLimit" required="false" type="java.lang.Integer"%>

<c:if test="${eoInputName == null}"><c:set var="eoInputName" value="${eoTableName}"/></c:if>
<c:if test="${eoDefaultOptionName == null}"><c:set var="eoDefaultOptionName" value="Select ${eoTableName}"/></c:if>
<c:if test="${eoLimit == null}"><c:set var="eoLimit" value="100"/></c:if>
    
<jsp:useBean id="eoSelectorBean" class="com.bc.tasktracker.server.beans.SelectorBean" scope="request"/>
<jsp:setProperty name="eoSelectorBean" property="tableName" value="${eoTableName}"/>
<jsp:setProperty name="eoSelectorBean" property="limit" value="${eoLimit}"/>
<jsp:setProperty name="eoSelectorBean" property="request" value="<%=request%>"/>

<select class="${eoInputClass}" name="${eoInputName}" size="1">
<%-- We use empty string here as when we left out the value altogether it used the contents of the option tag --%>        
    <option value="" disabled selected>${eoDefaultOptionName}</option>  
    <c:forEach var="Entity" items="${eoSelectorBean.resultList}">
      <option value="${Entity.abbreviation}">${Entity.abbreviation}</option>
    </c:forEach>
</select>
