package info.bliki.gae.model;

import info.bliki.gae.db.PageService;
import info.bliki.gae.db.PageServiceImpl;
import info.bliki.gae.utils.BlikiUtil;
import info.bliki.htmlcleaner.ContentToken;
import info.bliki.wiki.filter.WikipediaParser;
import info.bliki.wiki.model.WikiModel;
import info.bliki.wiki.tags.WPATag;

import java.util.Map;



public class BlikiModel extends WikiModel {
  private final PageService fPageService;

  private String fEditWikiBaseURL;

  public BlikiModel() {
    this("/image/${image}", "/wiki/${title}", "/page/edit/${title}");

  }

  public BlikiModel(String imageBaseURL, String linkBaseURL, String editBaseURL) {
    super(imageBaseURL, linkBaseURL);
    fEditWikiBaseURL = editBaseURL;
    fPageService = new PageServiceImpl();
  }

  public static BlikiModel get() {
    return new BlikiModel();
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.bliki.wiki.model.AbstractWikiModel#isMathtranRenderer()
   */
  @Override
  public boolean isMathtranRenderer() {
    return true;
  }

  @Override
  public void appendInternalLink(String topic, String hashSection,
      String topicDescription, String cssClass, boolean parseRecursive) {
    String hrefLink;
    String baseURL = fExternalWikiBaseURL;
    String style = "";
    if (!isExistingArticle(topic)) {
      if (BlikiUtil.isUserEditor()) {
        baseURL = fEditWikiBaseURL;
        style = "edit";
      }
    }
    if (topic.length() > 0) {
      String encodedtopic = encodeTitleToUrl(topic, true);
      hrefLink = baseURL.replace("${title}", encodedtopic);
    } else {
      if (hashSection != null) {
        hrefLink = "";
      } else {
        hrefLink = baseURL.replace("${title}", "");
      }
    }

    WPATag aTagNode = new WPATag();
    // append(aTagNode);
    aTagNode.addAttribute("title", topic, true);
    aTagNode.addAttribute("class", style, true);
    String href = hrefLink;
    if (hashSection != null) {
      href = href + '#' + encodeTitleDotUrl(hashSection, true);
    }
    aTagNode.addAttribute("href", href, true);
    if (cssClass != null) {
      aTagNode.addAttribute("class", cssClass, true);
    }
    aTagNode.addObjectAttribute("wikilink", topic);

    pushNode(aTagNode);
    if (parseRecursive) {
      WikipediaParser
          .parseRecursive(topicDescription.trim(), this, false, true);
    } else {
      aTagNode.addChild(new ContentToken(topicDescription));
    }
    popNode();
  }

  public boolean isExistingArticle(String topicName) {
    try {
      String title = BlikiUtil.decodeTitle(topicName);
      Page page = fPageService.findByTitle(title);
      if (page != null) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public String getRawWikiContent(String namespace, String topicName,
      Map<String, String> templateParameters) {
    String result = super.getRawWikiContent(namespace, topicName,
        templateParameters);
    if (result != null) {
      return result;
    }
    try {
      String title = BlikiUtil.decodeTitle(topicName);
      Page page = fPageService.findByTitle(title);
      if (page != null) {
        return page.getContent();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }
}
