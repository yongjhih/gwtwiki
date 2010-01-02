<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<head>
	<script src="http://www.google.com/jsapi"></script>
</head>
<body>
	<h3>Add new page</h3>
	<form action="/page/new" method="post" name="frmNewPage" class="medium">
		<div> 
			<label for="title"><fmt:message key="frmNewPage.title"/></label>
			<input type="text" name="title" value="${page.title}"/>
		</div>
		<div>
			<label for="content"><fmt:message key="frmNewPage.content"/></label>
			<textarea rows="15" cols="70" name="content">${page.content}</textarea>
		</div>
		<input type="hidden" name="key" value="${page.key}"/>
		<div><input type="submit" name="Save" class="submit" value="Save"/></div>
	</form>
</body>