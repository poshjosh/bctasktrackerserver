<%-- 
    Document   : page
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

<!DOCTYPE html>
<html>
  <head>
    <%@include file="/META-INF/jspf/defaultHeadContents.jspf"%> 
    <title>${App.name} - ${pageTitle}</title>
  </head>
    <body>
      <div id="main_container">
          
        <bctt:header logoAlt="${logoAlt}" logoSrc="${logoSrc}" 
                       headerText="${pageHeading}" loginPage="${loginPage}"/>  
        
        <jsp:doBody/>
        
      </div>
    </body>
</html>
