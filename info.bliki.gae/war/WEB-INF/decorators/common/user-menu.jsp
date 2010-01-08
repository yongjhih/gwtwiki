<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<div id="user-menu">
<ul>
	<c:choose>
		<c:when test="${lUser != null}">
			<li>${lUser } | <a href="${logoutUrl}"><fmt:message key="common.logout" /></a></li>
		</c:when>
		<c:otherwise>
			<li><a href="${loginUrl }"><fmt:message key="common.login" /></a></li>
		</c:otherwise>
	</c:choose>
</ul>
</div>
