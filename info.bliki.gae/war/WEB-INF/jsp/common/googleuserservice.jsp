<%-- Need to do this because @include tag is just static include --%>
<%@page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="com.google.appengine.api.users.User"%>
<%
	final UserService userService = UserServiceFactory.getUserService();
	final User user = userService.getCurrentUser();
	request.setAttribute("lUser", user);
	request.setAttribute("logoutUrl", userService.createLogoutURL(request.getRequestURI()));
	request.setAttribute("loginUrl", userService.createLoginURL(request.getRequestURI()));
	if (userService.isUserLoggedIn()) {
		request.setAttribute("isAdmin", userService.isUserAdmin());
	}
%>