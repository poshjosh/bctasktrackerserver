<%@tag description="Tag that simplifies access of SelectorBean" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@attribute name="tableName" required="true"%>
<%@attribute name="columnName" required="true"%>
<%@attribute name="columnValue" required="true"%>
<%@attribute name="singleResult" required="true" type="java.lang.Boolean"%>

<jsp:useBean id="stSelectorBean" class="com.bc.tasktracker.server.beans.SelectorBean" scope="page"/>
<jsp:setProperty name="stSelectorBean" property="tableName" value="${tableName}"/>
<jsp:setProperty name="stSelectorBean" property="columnName" value="${columnName}"/>
<jsp:setProperty name="stSelectorBean" property="columnValue" value="${columnValue}"/>
<jsp:setProperty name="stSelectorBean" property="request" value="<%=request%>"/>

<c:choose>
    <c:when test="${singleResult}">
        <c:set var="selectorTag_result" value="${stSelectorBean.singleResult}" scope="request"/>
    </c:when>
    <c:otherwise>
        <c:set var="selectorTag_result" value="${stSelectorBean.resultList}" scope="request"/>
    </c:otherwise>
</c:choose>

