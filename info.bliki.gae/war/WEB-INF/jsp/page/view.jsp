<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/common/googleuserservice.jsp"/>
<h2>${page.title }</h2>
<p>${page.htmlContent }</p>
<c:if test="${isAdmin==true}">
	<a href="/page/edit/${page.title}">Edit this content</a>
</c:if>