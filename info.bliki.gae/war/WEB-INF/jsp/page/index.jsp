<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<head>
<link href="/css/minimalist/displaytag.css" rel="stylesheet"></link>
</head>
<div class="grid_11"><display:table name="pages" id="pages">
	<display:column property="title" />
	<display:column>
		<a href='<c:out value="/wiki/${pages.title}"/>'>view</a>
	</display:column>
	<display:column>
		<a href='<c:out value="/page/delete/${pages.title}"/>'>delete</a>
	</display:column>
	<display:column property="author" />
	<display:column property="date" />
</display:table></div>
<div class="clear"></div>