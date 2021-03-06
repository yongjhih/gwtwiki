<%--

  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, version 2.1, dated February 1999.

  This program is free software; you can redistribute it and/or modify
  it under the terms of the latest version of the GNU Lesser General
  Public License as published by the Free Software Foundation;

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program (LICENSE.txt); if not, write to the Free Software
  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

--%>
<%@ page errorPage="/WEB-INF/jsp/error.jsp"
    contentType="text/html; charset=utf-8"
    isELIgnored="false"
%>

<%@ include file="page-init.jsp" %>

<c:if test="${!empty results}">
<div class="message"><fmt:message key="linkto.overview"><fmt:param value="${link}" /></fmt:message></div>

<ul>
<c:forEach items="${results}" var="result">
<li><jamwiki:link value="${result.topic}" text="${result.topic}" /></li>
</c:forEach>
</ul>
</c:if>

<c:if test="${empty results}">
<div class="message"><fmt:message key="linkto.none"><fmt:param value="${link}" /></fmt:message></div>
</c:if>