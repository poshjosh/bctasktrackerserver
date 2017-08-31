<%@tag trimDirectiveWhitespaces="true" description="Tag for rendering search results message of format: 1 to 10 of 98 results" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="/META-INF/tlds/bctt" prefix="bctt"%>

<%@attribute name="searchResultsBean" required="true" type="com.bc.jpa.search.SearchResults"%>
<%@attribute name="searchServletPath" required="true" description="a full url or path starting with '/' but not ending with '/'. A query of form page=0 etc will be appended to this path for pagination access to further pages in the search results"%>

<c:set var="totalSize" value="${fn:length(searchResultsBean.pages)}"/>

<c:if test="${totalSize > 0}">
  <c:set var="pageSize" value="${fn:length(searchResultsBean.currentPage)}"/>
  <c:set var="pageStart" value="${searchResultsBean.pageNumber * searchResultsBean.pageSize}"/>
  <c:set var="pageEnd" value="${pageStart + pageSize}"/>
</c:if>

  <c:if test="${pageStart > 0}">
    <a class="larger" href="${searchServletPath}?page=${searchResultsBean.pageNumber - 1}">&lt;</a>&nbsp;&nbsp;        
  </c:if>
  <c:choose>
    <c:when test="${totalSize == 0 || totalSize == 1}">
      ${totalSize} search result
    </c:when>    
    <c:otherwise>
      ${pageStart + 1} - ${pageEnd} of ${totalSize} results  
    </c:otherwise>
  </c:choose>
  <c:if test="${searchText != null && 
                searchText != defaultSearchText &&
                searchText != defaultSearchTextMobile}">
    for <tt><c:out value="${searchText}"/></tt>    
  </c:if>
  <c:if test="${pageEnd < totalSize}">
    &nbsp;&nbsp;<a class="larger" href="${searchServletPath}?page=${searchResultsBean.pageNumber + 1}">&gt;</a>        
  </c:if>
