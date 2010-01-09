package info.bliki.gae.controller;

import info.bliki.gae.db.PageService;

import org.springframework.ui.Model;

public class BlikiController {
  public void setUpModel(Model model) {
    model.addAttribute("leftMenu", PageService.getHTMLContent("LeftMenu"));
    model.addAttribute("bottomArea", PageService.getHTMLContent("BottomArea"));
  }
}
