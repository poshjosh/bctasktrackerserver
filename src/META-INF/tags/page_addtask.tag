<%-- 
    Document   : page_addtask
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
        <form class="form" id="addtaskform" method="post" action="${pageContext.servletContext.contextPath}/addtask">

            <c:choose>
                <c:when test="${param.docid != null}">
                    <input type="hidden" name="docid" value="${param.docid}"/>    
                </c:when>
                <c:otherwise>
            <b>Document Details</b>
            <p><label>Subject: <br/><input type="text" name="subject"/></label></p> 
            <p><label>Reference No: <br/><input type="text" name="referencenumber"/></label></p> 
            <p><label>Date Signed:&emsp;<small>(e.g 01 Jan 17)</small><br/>
                    <input type="text" name="datesigned"/></label></p> 
                </c:otherwise>
            </c:choose>

            <b>Task Details</b>
            <p><label>Description:&emsp;<small>What is the task?</small><br/>
                    <textarea name="description"></textarea></label></p> 
            <p>
                <label>Responsibility:&emsp;<small>Who is tasked?</small><br/> 
                    <bctt:entityOptions eoInputClass="noclass" 
                        eoTableName="appointment" eoInputName="reponsibility"/>
                </label>
            </p> 

            <input style="width:auto;" class="button0" type="submit" value="Submit"/>
        </form>
        <jsp:doBody/>     
    </jsp:body>
</bctt:page>

    
    
