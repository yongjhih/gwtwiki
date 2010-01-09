<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<div id="tab-menu">
<div class="tab-item"><a href="/wiki/${page.name}"><fmt:message key="tab.common.article" /></a></div>
<c:if test="${isAdmin==true}">
<div class="tab-item"><a href="/page/edit/${page.name}"><fmt:message key="tab.common.edit" /></a></div>
</c:if>
</div>
<div class="clear"></div>
 
