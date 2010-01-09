package info.bliki.gae.servlets;

import info.bliki.gae.db.PageService;
import info.bliki.gae.db.PageServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jamwiki.model.Topic;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public abstract class BlikiServlet extends AbstractController {
  protected PageService pageService = new PageServiceImpl();
  // private static final WikiLogger logger =
  // WikiLogger.getLogger(JAMWikiServlet.class.getName());

  /**
   * The name of the output parameter used to indicate that Spring should
   * redirect to another servlet.
   */
  protected static final String SPRING_REDIRECT_PREFIX = "redirect:";

  /**
   * Flag to indicate whether or not the servlet should load the nav bar and
   * other layout elements.
   */
  protected boolean layout = true;
  /** The prefix of the JSP file used to display the servlet output. */
  protected String displayJSP = "wiki";
  /**
   * The name of the JSP file used to render the servlet output in case of
   * errors.
   */
  private static final String JSP_ERROR = "error-display.jsp";
  /**
   * Any page that take longer than this value (specified in milliseconds) will
   * print a warning to the log.
   */
  protected static final int SLOW_PAGE_LIMIT = 1000;
  /**
   * Parameter used to indicate that a topic should be the target of a
   * successful login.
   */
  protected static final String PARAM_LOGIN_SUCCESS_TARGET = "returnto";

  /**
   * This method ensures that the left menu, logo, and other required values
   * have been loaded into the session object.
   * 
   * @param request
   *          The servlet request object.
   * @param next
   *          A ModelAndView object corresponding to the page being constructed.
   */
  // private void buildLayout(HttpServletRequest request, ModelAndView next,
  // WikiPageInfo pageInfo) {
  // String virtualWikiName = pageInfo.getVirtualWikiName();
  // if (virtualWikiName == null) {
  // logger.severe("No virtual wiki available for page request " +
  // request.getRequestURI());
  // virtualWikiName = WikiBase.DEFAULT_VWIKI;
  // }
  // VirtualWiki virtualWiki = ServletUtil.retrieveVirtualWiki(virtualWikiName);
  // // build the layout contents
  // String leftMenu = ServletUtil.cachedContent(request.getContextPath(),
  // request.getLocale(), virtualWikiName, WikiBase.SPECIAL_PAGE_LEFT_MENU,
  // true);
  // next.addObject("leftMenu", leftMenu);
  // next.addObject("defaultTopic", virtualWiki.getDefaultTopicName());
  // next.addObject("virtualWiki", virtualWiki.getName());
  // next.addObject("logo",
  // Environment.getValue(Environment.PROP_BASE_LOGO_IMAGE));
  // String bottomArea = ServletUtil.cachedContent(request.getContextPath(),
  // request.getLocale(), virtualWiki.getName(),
  // WikiBase.SPECIAL_PAGE_BOTTOM_AREA, true);
  // next.addObject("bottomArea", bottomArea);
  // next.addObject(WikiUtil.PARAMETER_VIRTUAL_WIKI, virtualWiki.getName());
  // long cssRevision = 0L;
  // try {
  // cssRevision = WikiBase.getDataHandler().lookupTopic(virtualWiki.getName(),
  // WikiBase.SPECIAL_PAGE_STYLESHEET, false, null).getCurrentVersionId();
  // } catch (Exception e) {}
  // next.addObject("cssRevision", cssRevision);
  // }

  /**
   * Build a map of links and the corresponding link text to be used as the tab
   * menu links for the WikiPageInfo object.
   */
  // private LinkedHashMap buildTabMenu(HttpServletRequest request, WikiPageInfo
  // pageInfo) {
  // LinkedHashMap<String, WikiMessage> links = new LinkedHashMap<String,
  // WikiMessage>();
  // // WikiUserDetails userDetails = ServletUtil.currentUserDetails();
  // String pageName = pageInfo.getTopicName();
  // String virtualWiki = pageInfo.getVirtualWikiName();
  // try {
  // if (pageInfo.getAdmin()) {
  // // if (userDetails.hasRole(Role.ROLE_SYSADMIN)) {
  // // links.put("Special:Admin", new WikiMessage("tab.admin.configuration"));
  // // links.put("Special:Maintenance", new
  // WikiMessage("tab.admin.maintenance"));
  // // links.put("Special:Roles", new WikiMessage("tab.admin.roles"));
  // // }
  // // if (userDetails.hasRole(Role.ROLE_TRANSLATE)) {
  // // links.put("Special:Translation", new
  // WikiMessage("tab.admin.translations"));
  // // }
  // } else if (pageInfo.getSpecial()) {
  // // append query params for pages such as Special:Contributions that need it
  // String specialUrl = pageName;
  // if (!StringUtils.isBlank(request.getQueryString())) {
  // specialUrl = pageName + "?" + request.getQueryString();
  // }
  // links.put(specialUrl, new WikiMessage("tab.common.special"));
  // } else {
  // String article = WikiUtil.extractTopicLink(pageName);
  // String comments = WikiUtil.extractCommentsLink(pageName);
  // links.put(article, new WikiMessage("tab.common.article"));
  // links.put(comments, new WikiMessage("tab.common.comments"));
  // // if (ServletUtil.isEditable(virtualWiki, pageName, userDetails)) {
  // String editLink = "Special:Edit?topic=" +
  // Utilities.encodeAndEscapeTopicName(pageName);
  // if (!StringUtils.isBlank(request.getParameter("topicVersionId"))) {
  // editLink += "&topicVersionId=" + request.getParameter("topicVersionId");
  // }
  // links.put(editLink, new WikiMessage("tab.common.edit"));
  // // }
  // String historyLink = "Special:History?topic=" +
  // Utilities.encodeAndEscapeTopicName(pageName);
  // links.put(historyLink, new WikiMessage("tab.common.history"));
  // // if (ServletUtil.isMoveable(virtualWiki, pageName, userDetails)) {
  // // String moveLink = "Special:Move?topic=" +
  // Utilities.encodeAndEscapeTopicName(pageName);
  // // links.put(moveLink, new WikiMessage("tab.common.move"));
  // // }
  // // if (!userDetails.hasRole(Role.ROLE_ANONYMOUS)) {
  // // Watchlist watchlist = ServletUtil.currentWatchlist(request,
  // virtualWiki);
  // // boolean watched = (watchlist.containsTopic(pageName));
  // // String watchlistLabel = (watched) ? "tab.common.unwatch" :
  // "tab.common.watch";
  // // String watchlistLink = "Special:Watchlist?topic=" +
  // Utilities.encodeAndEscapeTopicName(pageName);
  // // links.put(watchlistLink, new WikiMessage(watchlistLabel));
  // // }
  // if (pageInfo.isUserPage()) {
  // WikiLink wikiLink = LinkUtil.parseWikiLink(pageName);
  // String contributionsLink = "Special:Contributions?contributor=" +
  // Utilities.encodeAndEscapeTopicName(wikiLink.getArticle());
  // links.put(contributionsLink, new WikiMessage("tab.common.contributions"));
  // }
  // String linkToLink = "Special:LinkTo?topic=" +
  // Utilities.encodeAndEscapeTopicName(pageName);
  // links.put(linkToLink, new WikiMessage("tab.common.links"));
  // // if (userDetails.hasRole(Role.ROLE_ADMIN)) {
  // // String manageLink = "Special:Manage?topic=" +
  // Utilities.encodeAndEscapeTopicName(pageName);
  // // links.put(manageLink, new WikiMessage("tab.common.manage"));
  // // }
  // String printLink = "Special:Print?topic=" +
  // Utilities.encodeAndEscapeTopicName(pageName);
  // links.put(printLink, new WikiMessage("tab.common.print"));
  // }
  // } catch (Exception e) {
  // logger.severe("Unable to build tabbed menu links", e);
  // }
  // return links;
  // }

  /**
   * Build a map of links and the corresponding link text to be used as the user
   * menu links for the WikiPageInfo object.
   */
  // private LinkedHashMap buildUserMenu(WikiPageInfo pageInfo) {
  // LinkedHashMap<String, WikiMessage> links = new LinkedHashMap<String,
  // WikiMessage>();
  // // WikiUserDetails userDetails = ServletUtil.currentUserDetails();
  // // if (userDetails.hasRole(Role.ROLE_ANONYMOUS) &&
  // !userDetails.hasRole(Role.ROLE_EMBEDDED)) {
  // // include the current page in the login link
  // String loginLink = "Special:Login";
  // if (!StringUtils.startsWith(pageInfo.getTopicName(), "Special:Login")) {
  // loginLink += LinkUtil.appendQueryParam("", PARAM_LOGIN_SUCCESS_TARGET,
  // pageInfo.getTopicName());
  // }
  // links.put(loginLink, new WikiMessage("common.login"));
  // links.put("Special:Account", new WikiMessage("usermenu.register"));
  // // }
  // // if (!userDetails.hasRole(Role.ROLE_ANONYMOUS)) {
  // // WikiUser user = ServletUtil.currentWikiUser();
  // // String userPage = NamespaceHandler.NAMESPACE_USER +
  // NamespaceHandler.NAMESPACE_SEPARATOR + user.getUsername();
  // // String userCommentsPage = NamespaceHandler.NAMESPACE_USER_COMMENTS +
  // NamespaceHandler.NAMESPACE_SEPARATOR + user.getUsername();
  // // String username = user.getUsername();
  // // if (!StringUtils.isBlank(user.getDisplayName())) {
  // // username = user.getDisplayName();
  // // }
  // // // user name will be escaped by the jamwiki:link tag
  // // WikiMessage userMenuMessage = new WikiMessage("usermenu.user");
  // // userMenuMessage.setParamsWithoutEscaping(new String[]{username});
  // // links.put(userPage, userMenuMessage);
  // // links.put(userCommentsPage, new WikiMessage("usermenu.usercomments"));
  // // links.put("Special:Watchlist", new WikiMessage("usermenu.watchlist"));
  // // }
  // // if (!userDetails.hasRole(Role.ROLE_ANONYMOUS) &&
  // !userDetails.hasRole(Role.ROLE_NO_ACCOUNT)) {
  // // links.put("Special:Account", new WikiMessage("usermenu.account"));
  // // }
  // // if (!userDetails.hasRole(Role.ROLE_ANONYMOUS) &&
  // !userDetails.hasRole(Role.ROLE_EMBEDDED)) {
  // // links.put("Special:Logout", new WikiMessage("common.logout"));
  // // }
  // // if (userDetails.hasRole(Role.ROLE_SYSADMIN)) {
  // // links.put("Special:Admin", new WikiMessage("usermenu.admin"));
  // // } else if (userDetails.hasRole(Role.ROLE_TRANSLATE)) {
  // // links.put("Special:Translation", new
  // WikiMessage("tab.admin.translations"));
  // // }
  // return links;
  // }
  /**
   * Abstract method that must be implemented by all sub-classes to handle
   * servlet requests.
   * 
   * @param request
   *          The servlet request object.
   * @param response
   *          The servlet response object.
   * @param next
   *          A ModelAndView object that has been initialized to the view
   *          specified by the <code>displayJSP</code> member variable.
   * @param pageInfo
   *          A WikiPageInfo object that will hold output parameters to be
   *          passed to the output JSP.
   * @return A ModelAndView object corresponding to the information to be
   *         rendered, or <code>null</code> if the method directly handles its
   *         own output, for example by writing directly to the output response.
   */
  protected abstract ModelAndView handleBlikiRequest(
      HttpServletRequest request, HttpServletResponse response,
      ModelAndView next) throws Exception;

  /**
   * Implement the handleRequestInternal method specified by the Spring
   * AbstractController class.
   * 
   * @param request
   *          The servlet request object.
   * @param response
   *          The servlet response object.
   * @return A ModelAndView object corresponding to the information to be
   *         rendered, or <code>null</code> if the method directly handles its
   *         own output, for example by writing directly to the output response.
   * @throws Exception
   *           Thrown if any error occurs during method execution.
   */
  public ModelAndView handleRequestInternal(HttpServletRequest request,
      HttpServletResponse response) {
    long start = System.currentTimeMillis();
    initParams();
    ModelAndView next = new ModelAndView(this.displayJSP);
    // WikiPageInfo pageInfo = new WikiPageInfo(request);
    try {
      next = this.handleBlikiRequest(request, response, next);
      if (next != null && this.layout) {
//        this.loadLayout(request, next);
      }
      if (next != null) {
//        next.addObject(ServletUtil.PARAMETER_PAGE_INFO, pageInfo);
      }
    } catch (Throwable t) {
      return this.viewError(request, t);
    }
    long execution = System.currentTimeMillis() - start;
    // if (execution > JAMWikiServlet.SLOW_PAGE_LIMIT) {
    // // logger.warning("Slow page loading time: " + request.getRequestURI() +
    // " (" + (execution / 1000.000) + " s.)");
    // }
    if (logger.isInfoEnabled()) {
      String url = request.getRequestURI()
          + (!StringUtils.isEmpty(request.getQueryString()) ? "?"
              + request.getQueryString() : "");
      // logger.info("Loaded page " + url + " (" + (execution / 1000.000) +
      // " s.)");
    }
    return next;
  }

  /**
   * If any special servlet initialization needs to be performed it can be done
   * by overriding this method. In particular, this method can be used to
   * override the defaults for the <code>layout</code> member variable, which
   * determines whether or not the output JSP should include the left navigation
   * and other layout values, and the <code>displayJSP</code> member variable,
   * which determine the JSP file used to render output.
   */
  protected void initParams() {
  }

  /**
   * This method ensures that values required for rendering a JSP page have been
   * loaded into the ModelAndView object. Examples of values that may be handled
   * by this method include topic name, username, etc.
   * 
   * @param request
   *          The current servlet request object.
   * @param next
   *          The current ModelAndView object.
   * @param pageInfo
   *          The current WikiPageInfo object, containing basic page rendering
   *          information.
   */
  private void loadLayout(HttpServletRequest request, ModelAndView next, Topic pageInfo) throws Exception {
    if (next.getViewName() != null
        && next.getViewName().startsWith(SPRING_REDIRECT_PREFIX)) {
      // if this is a redirect, no need to load anything
      return;
    }
    // load cached top area, nav bar, etc.
//    this.buildLayout(request, next, pageInfo);
    if (StringUtils.isBlank(pageInfo.getName())) {
//      pageInfo.setTopicName(WikiUtil.getTopicFromURI(request));
    }
//    pageInfo.setUserMenu(this.buildUserMenu(pageInfo));
//    pageInfo.setTabMenu(this.buildTabMenu(request, pageInfo));
  }

  /**
   * Method used when redirecting to an error page.
   * 
   * @param request
   *          The servlet request object.
   * @param t
   *          The exception that is the source of the error.
   * @return Returns a ModelAndView object corresponding to the error page
   *         display.
   */
  private ModelAndView viewError(HttpServletRequest request, Throwable t) {
    ModelAndView next = new ModelAndView("wiki");

    return next;
  }

}
