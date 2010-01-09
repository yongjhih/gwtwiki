package info.bliki.gae.servlets;

import info.bliki.gae.utils.BlikiBase;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jamwiki.model.Topic;
import org.springframework.web.servlet.ModelAndView;

public class StylesheetServlet extends BlikiServlet {

  protected ModelAndView handleBlikiRequest(HttpServletRequest request,
      HttpServletResponse response, ModelAndView next) throws Exception {
    // String virtualWiki = null;
    try {
      // virtualWiki = pageInfo.getVirtualWikiName();
      // String stylesheet = ServletUtil.cachedContent(request.getContextPath(),
      // request.getLocale(), virtualWiki, WikiBase.SPECIAL_PAGE_STYLESHEET,
      // false);
      Topic page = pageService.findByTitle(BlikiBase.SPECIAL_PAGE_STYLESHEET);
      if (page != null) {
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

}
