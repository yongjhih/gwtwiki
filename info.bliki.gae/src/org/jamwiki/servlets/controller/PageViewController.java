package org.jamwiki.servlets.controller;

import info.bliki.gae.db.PageService;
import info.bliki.gae.utils.BlikiBase;
import info.bliki.gae.utils.BlikiUtil;

import java.io.PrintWriter;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jamwiki.WikiException;
import org.jamwiki.WikiMessage;
import org.jamwiki.model.Topic;
import org.jamwiki.model.WikiUser;
import org.jamwiki.parser.ParserException;
import org.jamwiki.parser.ParserInput;
import org.jamwiki.parser.ParserOutput;
import org.jamwiki.parser.ParserUtil;
import org.jamwiki.servlets.CategoryServlet;
import org.jamwiki.servlets.ServletUtil;
import org.jamwiki.servlets.WikiPageInfo;
import org.jamwiki.utils.NamespaceHandler;
import org.jamwiki.utils.PseudoTopicHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PageViewController extends BlikiController {
  protected final Log logger = LogFactory.getLog(getClass());

  // @Autowired
  // private PageService pageService;

  // @RequestMapping(value = "/p/{key}", method = RequestMethod.GET)
  // public String indexP(@PathVariable String key, Model model) {
  // Page page = pageService.findByKey(Long.valueOf(key));
  // if (page == null) {
  // return "common/404";
  // }
  // model.addAttribute("page", page);
  // return "page/view";
  // }

  /**
   * Read the StyleSheet wiki text and use it as &quot;bliki.css&quot; cascading
   * stylesheet.
   */
  @RequestMapping(value = "/bliki.css", method = RequestMethod.GET)
  public String indexStylesheet(HttpServletRequest request,
      HttpServletResponse response, Model model) {
    try {
      Topic page = PageService.findByTitle(BlikiBase.SPECIAL_PAGE_STYLESHEET);
      if (page != null) {
        // model.addAttribute("page", page);
        String stylesheet = page.getTopicContent();
        response.setContentType("text/css");
        response.setCharacterEncoding("UTF-8");
        // cache for 30 minutes (60 * 30 = 1800)
        // FIXME - make configurable
        response.setHeader("Cache-Control", "max-age=1800");
        PrintWriter out = response.getWriter();
        out.print(stylesheet);
        out.close();

      }
    } catch (Exception e) {
      // logger.severe("Failure while loading stylesheet for virtualWiki " +
      // virtualWiki, e);
    }
    // do not load defaults or redirect - return as raw CSS
    return null;
  } 
//
//  @RequestMapping(value = "/wiki/{key}", method = RequestMethod.GET)
//  public String indexW(@PathVariable String key, HttpServletRequest request,HttpServletResponse response,
//      Model model) {
//    setUpModel(model);
//    if (StringUtils.isBlank(key)) {
//      return PageController.ERROR_PAGE_URI;
//    }
//    if (PseudoTopicHandler.isPseudoTopic(key)) {
//      //
//      if (key.equals("Special:Categories")) {
//        categories(request, response, model);
//      }
//    }
//    String topicName = BlikiUtil.decodeTitle(key);
//    Topic topic = PageService.findByTitle(topicName);
//    if (topic == null) {
//      model.addAttribute("page", new Topic(topicName));
//      return PageController.EDIT_PAGE_URI;
//      // return "/page/new.jsp?title=" + key;
//    }
//    model.addAttribute("page", topic);
//    viewTopic(request, model, topic, false);
//    return PageController.TOPIC_PAGE_URI;// "page/view";
//  }

  public static String categories(HttpServletRequest request, HttpServletResponse response, Model model) {
    CategoryServlet cs = new CategoryServlet();
    try {
      ModelAndView mav = cs.handleRequest(request, response);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return "page/categories"; 
  }

  /**
   * Utility method used when viewing a topic.
   * 
   * @param request
   *          The current servlet request object.
   * @param next
   *          The current Spring ModelAndView object.
   * @param pageInfo
   *          The current WikiPageInfo object, which contains information needed
   *          for rendering the final JSP page.
   * @param pageTitle
   *          The title of the page being rendered.
   * @param topic
   *          The Topic object for the topic being displayed.
   * @param sectionEdit
   *          Set to <code>true</code> if edit links should be displayed for
   *          each section of the topic.
   * @throws WikiException
   *           Thrown if any error occurs while retrieving or parsing the topic.
   */
  protected static void viewTopic(HttpServletRequest request, Model model,
      Topic topic, boolean sectionEdit) throws WikiException {
    // FIXME - what should the default be for topics that don't exist?
    if (topic == null) {
      throw new WikiException(new WikiMessage("common.exception.notopic"));
    }
    // WikiUtil.validateTopicName(topic.getName());
    // if (topic.getTopicType() == Topic.TYPE_REDIRECT
    // && (request.getParameter("redirect") == null ||
    // !request.getParameter("redirect").equalsIgnoreCase("no"))) {
    // Topic child = null;
    // try {
    // child = WikiUtil.findRedirectedTopic(topic, 0);
    // } catch (DataAccessException e) {
    // throw new WikiException(new WikiMessage("error.unknown", e.getMessage()),
    // e);
    // }
    // if (!child.getName().equals(topic.getName())) {
    // String redirectUrl = null;
    // try {
    // redirectUrl = LinkUtil.buildTopicUrl(request.getContextPath(),
    // topic.getVirtualWiki(), topic.getName(), true);
    // } catch (DataAccessException e) {
    // throw new WikiException(new WikiMessage("error.unknown", e.getMessage()),
    // e);
    // }
    // // FIXME - hard coding
    // redirectUrl += LinkUtil.appendQueryParam("", "redirect", "no");
    // String redirectName = topic.getName();
    // pageInfo.setRedirectInfo(redirectUrl, redirectName);
    // pageTitle = new WikiMessage("topic.title", child.getName());
    // topic = child;
    // // update the page info's virtual wiki in case this redirect is to
    // // another virtual wiki
    // pageInfo.setVirtualWikiName(topic.getVirtualWiki());
    // }
    // }
    String virtualWiki = topic.getVirtualWiki();
    String topicName = topic.getName();
    // WikiUserDetails userDetails = ServletUtil.currentUserDetails();
    // if (sectionEdit && !ServletUtil.isEditable(virtualWiki, topicName,
    // userDetails)) {
    sectionEdit = false;
    // }
    WikiUser user = ServletUtil.currentWikiUser();
    ParserInput parserInput = new ParserInput();
    parserInput.setContext(request.getContextPath());
    parserInput.setLocale(request.getLocale());
    parserInput.setWikiUser(user);
    parserInput.setTopicName(topicName);
    // parserInput.setUserIpAddress(ServletUtil.getIpAddress(request));
    parserInput.setVirtualWiki(virtualWiki);
    parserInput.setAllowSectionEdit(sectionEdit);
    ParserOutput parserOutput = new ParserOutput();
    String content = null;
    try {
      content = ParserUtil.parse(parserInput, parserOutput, topic
          .getTopicContent());
    } catch (ParserException e) {
      throw new WikiException(new WikiMessage("error.unknown", e.getMessage()),
          e);
    }
    if (parserOutput.getCategories().size() > 0) {
      LinkedHashMap<String, String> categories = new LinkedHashMap<String, String>();
      for (String key : parserOutput.getCategories().keySet()) {
        String value = key.substring(NamespaceHandler.NAMESPACE_CATEGORY
            .length()
            + NamespaceHandler.NAMESPACE_SEPARATOR.length());
        categories.put(key, value);
      }
      model.addAttribute("categories", categories);
    }
    topic.setHtmlContent(content);
    // if (topic.getTopicType() == Topic.TYPE_CATEGORY) {
    // loadCategoryContent(next, virtualWiki, topic.getName());
    // }
    // if (topic.getTopicType() == Topic.TYPE_IMAGE || topic.getTopicType() ==
    // Topic.TYPE_FILE) {
    // List<WikiFileVersion> fileVersions = null;
    // try {
    // fileVersions =
    // WikiBase.getDataHandler().getAllWikiFileVersions(virtualWiki, topicName,
    // true);
    // } catch (DataAccessException e) {
    // throw new WikiException(new WikiMessage("error.unknown", e.getMessage()),
    // e);
    // }
    // for (WikiFileVersion fileVersion : fileVersions) {
    // // update version urls to include web root path
    // String url = FilenameUtils.normalize(Environment.getValue(Environment.
    // PROP_FILE_DIR_RELATIVE_PATH) + "/" + fileVersion.getUrl());
    // url = FilenameUtils.separatorsToUnix(url);
    // fileVersion.setUrl(url);
    // }
    // next.addObject("fileVersions", fileVersions);
    // if (topic.getTopicType() == Topic.TYPE_IMAGE) {
    // next.addObject("topicImage", true);
    // } else {
    // next.addObject("topicFile", true);
    // }
    // }
    WikiPageInfo pageInfo = new WikiPageInfo(request);
    // WikiMessage pageTitle,
    pageInfo.setSpecial(false);
    pageInfo.setTopicName(topicName);
    model.addAttribute(ServletUtil.PARAMETER_PAGE_INFO, pageInfo);
    model.addAttribute(ServletUtil.PARAMETER_TOPIC_OBJECT, topic);
    // if (pageTitle != null) {
    // pageInfo.setPageTitle(pageTitle);
    // }
  }
}
