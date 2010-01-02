<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<html>
	<head>
		<title>Bliki</title>
	</head>
	<body>
		<div class="grid_8">
		<c:choose>
			<c:when test="${!empty page.title}">
				<h2>${page.title}</h2>
				<p>${page.htmlContent}</p>
			</c:when>
			<c:otherwise>
				Create your page!
			</c:otherwise>
		</c:choose>
		</div>
	</body>
</html>