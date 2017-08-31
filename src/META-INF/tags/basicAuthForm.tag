<%-- 
    Document   : basicAuthForm
    Created on : Aug 18, 2017, 5:21:30 PM
    Author     : Josh
--%>
<%@tag description="Present a simple form with username and password" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/META-INF/tlds/bctt" prefix="bctt"%>

<%@attribute name="formId" required="true"%>
<%@attribute name="targetRelativePath" required="true"%>
<%@attribute name="submitButtonValue" required="false"%>
<c:if test="${submitButtonValue == null}"><c:set var="submitButtonValue" value="Submit"/></c:if>

<form class="form" id="${formId}" method="post" action="${pageContext.servletContext.contextPath}${targetRelativePath}">
    <p>
        <label>Enter username or select from options <br/>
            <input type="text" name="username"/><br/>
            <bctt:entityOptions eoInputClass="noclass" 
                eoTableName="appointment" eoInputName="username_option"/>
        </label>
    </p> 
    <p><label>Password: <br/><input type="password" name="password"/></label></p>
    <p>
        <input style="width:auto;" class="button0" type="submit" value="${submitButtonValue}"/>
    </p>
</form>
