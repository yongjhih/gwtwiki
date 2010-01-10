<%@ page errorPage="/WEB-INF/jsp/error.jsp"
    contentType="text/html; charset=utf-8"
%>

<%@ include file="page-init.jsp" %>
 
<fieldset>
<legend><fmt:message key="topic.caption.editlegend" /></legend>

<form action="/page/new" method="post" name="form" class="medium"> 

<input type="hidden" name="title" value="<c:out value="${page.name}" />" />
<%@ include file="/WEB-INF/jsp/page/editor-toolbar-include.jsp" %>
<p>
<textarea id="topicContents" name="contents" rows="25" cols="80" accesskey=","><c:out value="${page.topicContent}" escapeXml="true" /></textarea>
</p>
<input type="submit" name="save" value="<fmt:message key="common.save" />"  accesskey="s" />
		
</form>
	
</fieldset>