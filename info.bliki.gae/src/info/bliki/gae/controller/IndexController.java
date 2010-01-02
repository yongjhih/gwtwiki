package info.bliki.gae.controller;

import info.bliki.gae.db.PageService;
import info.bliki.gae.model.Page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller fetches just the index page right now. Some more functionalities
 * can be stuffed into this controller.
 * 
 */
@Controller
public class IndexController {
  protected final Log logger = LogFactory.getLog(getClass());
  @Autowired
  private PageService pageService;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String get(Model model) {
    // Page page = pageService.findByKey(Page.HOME_PAGE_KEY);
    Page page = pageService.findByTitle(Page.MAIN_PAGE);
    model.addAttribute("page", page);
    return "redirect:/wiki/" + Page.MAIN_PAGE;
  }
}