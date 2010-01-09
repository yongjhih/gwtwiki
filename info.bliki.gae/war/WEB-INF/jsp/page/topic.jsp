<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/common/googleuserservice.jsp"/>
 
<div id="content-article">
<c:out value="${topicObject.htmlContent}" escapeXml="false" />
</div>
