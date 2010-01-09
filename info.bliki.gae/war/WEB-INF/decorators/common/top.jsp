<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<html>
<head>
<title><decorator:title default="${page.name}" /> - 
 <fmt:message key="common.sitename" /></title>
<link href="/bliki.css" rel="stylesheet"></link>
<script type="text/javascript" src="<c:url value="/js/jamwiki.js" />"></script>
<decorator:head />
</head>
<body>
