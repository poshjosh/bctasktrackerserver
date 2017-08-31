<%-- 
    Document   : page_error
    Created on : Aug 24, 2017, 12:55:45 PM
    Author     : Josh
--%>
<%@tag trimDirectiveWhitespaces="true" description="default page" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/META-INF/tlds/bctt" prefix="bctt"%>

<%@attribute name="pageTitle" required="true"%>
<%@attribute name="pageHeading" required="false"%>
<%@attribute name="logoAlt" required="false"%>
<%@attribute name="logoSrc" required="true"%>
<%@attribute name="loginPage" required="false"%>

<bctt:page pageTitle="${pageTitle}" pageHeading="${pageHeading}" 
             logoAlt="${logoAlt}" logoSrc="${logoSrc}" loginPage="${loginPage}">
    <jsp:body>
        <c:out default="An unexpected error occured" 
               value="${userMessage == null ? pageContext.errorData.throwable.localizedMessage : userMessage}"/>

        <p><small><tt>
            Requested resource: ${pageContext.errorData.requestURI}<br/>
            Status Code: ${pageContext.errorData.statusCode}
        </tt></small></p>
        <c:if test="${productionMode != null && !productionMode}">
            <c:forEach var="stackTraceElement" items="${pageContext.exception.stackTrace}">
              ${stackTraceElement}<br/>    
            </c:forEach>
        </c:if>
        <jsp:doBody/>     
    </jsp:body>
</bctt:page>

    
    
