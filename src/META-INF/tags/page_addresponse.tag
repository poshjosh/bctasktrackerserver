<%-- 
    Document   : page_addresponse
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
        <form class="form" id="addresponseform" method="post" action="${pageContext.servletContext.contextPath}/addresponse">

            <input type="hidden" name="taskid" value="${param.taskid}"/>
            <p><label>Response: <br/><textarea name="response"></textarea></label></p> 
            <p><label>Deadline: <small>(e.g 01 Jan 17)</small><br/><input type="text" name="deadline"/></label></p> 

            <input style="width:auto;" class="button0" type="submit" value="Submit"/>

        </form>
        <jsp:doBody/>     
    </jsp:body>
</bctt:page>

    
    
