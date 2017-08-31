<%-- 
    Document   : page_login
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
<%@attribute name="joinPage" required="false"%>

<bctt:page pageTitle="${pageTitle}" pageHeading="${pageHeading}" 
             logoAlt="${logoAlt}" logoSrc="${logoSrc}" loginPage="${loginPage}">
    <jsp:body>
        <bctt:basicAuthForm formId="loginform" 
                              targetRelativePath="/login" 
                              submitButtonValue="Login"/>

        <c:if test="${joinPage != null}"> 
            <p><a href="${joinPage}">New User</a></p>
        </c:if>
        <jsp:doBody/>     
    </jsp:body>
</bctt:page>

    
    
