<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<html>
    <head>
        <title><decorator:title default="${page.title}" /> - <fmt:message key="common.sitename" /></title>
        <link href="/css/960/reset.css" rel="stylesheet"></link>
        <link href="/css/960/text.css" rel="stylesheet"></link>
        <link href="/css/960/960.css" rel="stylesheet"></link>
        <link href="bliki.css" rel="stylesheet"></link>
        <decorator:head />
    </head>
    <body>
    	<div id="header">
    		<%@ include file="common/header.jsp" %>
    	</div>
    	<div id="nav"><jsp:include page="common/nav.jsp" /></div>
    	<div id="content">
    		<div  class="container_12">
        		<decorator:body />
        	</div>
        </div>
        <div id="footer">
        	<%@ include file="common/footer.jsp" %>
        </div>
    </body>
</html>