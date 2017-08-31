<%-- 
    Document   : header
    Created on : Mar 9, 2017, 11:29:28 AM
    Author     : Josh
--%>
<%@tag description="put the tag description here" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@attribute name="headerText" required="false" description="The text to be displayed in the header"%>
<%@attribute name="logoAlt" required="false"%>
<%@attribute name="logoSrc" required="true"%>
<%@attribute name="loginPage" required="false"%>

<c:if test="${loginPage == null || loginPage == ''}">
    <c:set var="loginPage" value="${pageContext.servletContext.contextPath}/login.jsp"/>
</c:if>

<c:if test="${userMessage != null && userMessage != ''}"><div class="userMessage">${userMessage}</div></c:if>
<c:if test="${logoAlt == null}"><c:set var="logoAlt" value="logo"/></c:if>    

<table id="headertable">
    <tr>
<%-- (58,65), (40,45) --%>        
        <td style="width:50px; height:53px;">
            <a href="${pageContext.servletContext.contextPath}/tracker">
                <img id="logo" alt="${logoAlt==null?'logo':logoAlt}" width="50" height="53" src="${logoSrc}"/>
            </a>
        </td>
        <td class="widthFull,centered" style="height:53px; padding:0.2em; background-color:#234567; color:white;">
            <div class="widthFull,centered,larger" style="font-size:larger;">&emsp;${App.name}&emsp;</div>
            <div class="widthFull,centered">&emsp;${headerText}&emsp;</div>
        </td>
        <td style="height:53px; padding:0.2em; background-color:#234567; color:white;">
            <c:choose>
                <c:when test="${App.user.loggedIn}">
                    ${App.user.name} |
                    <a style="color:white;" href="${pageContext.servletContext.contextPath}/logout">Logout</a>    
                </c:when>
                <c:otherwise>
                    <a style="color:white;" href="${loginPage}">Login</a>    
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
</table>
