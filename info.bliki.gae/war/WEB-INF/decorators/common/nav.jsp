<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/common/googleuserservice.jsp"/>
<div class="container_12">
    <div class="grid_5">
		<c:choose>
			<c:when test="${lUser != null}">
				<p>Logged in: ${lUser } | <a href="${logoutUrl}">sign out</a></p>
			</c:when>
			<c:otherwise>
				<p><a href="${loginUrl }">Sign in</a></p>
			</c:otherwise>
		</c:choose>
	</div>
	<div class="grid_7 underlinemenu">
		<ul>
			<li><a href="/"><fmt:message key="menu.home"/></a></li>
			<li><a href="/page/"><fmt:message key="menu.pages"/></a></li>
		</ul>
	</div>
	<div class="clear"></div>
</div>
