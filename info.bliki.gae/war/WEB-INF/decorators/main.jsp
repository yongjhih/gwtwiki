<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator"
	prefix="decorator"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<%@ include file="common/top.jsp"%>

<div id="wiki-page">
<div id="wiki-navigation">
<div id="logo"></div>
<br />
<c:if test="${!empty leftMenu && leftMenu != '<br/><br/>'}">
	<div id="nav-menu"><c:out value="${leftMenu}" escapeXml="false" />
	</div>
</c:if></div>
</div>
<div id="wiki-content">
<jsp:include page="common/user-menu.jsp" />
<jsp:include page="common/top-menu.jsp" />
  <div id="contents">
  <h1 id="contents-header">${page.name}</h1>
    <decorator:body />
  </div>
</div>

<div id="footer"></div>
<div id="wiki-footer">
<hr width="99%" />
<c:out value="${bottomArea}" escapeXml="false" /> <br />
<%@ include file="common/footer.jsp"%></div>
</div>
</body>
</html>