package info.bliki.gae.controller;

import info.bliki.gae.db.PageService;

import org.springframework.ui.Model;

public class BlikiController {
  public void setUpModel(PageService pageService, Model model) {
    model.addAttribute("leftMenu", pageService.getHTMLContent("LeftMenu"));
    model.addAttribute("bottomArea", pageService.getHTMLContent("BottomArea"));
  }
}
