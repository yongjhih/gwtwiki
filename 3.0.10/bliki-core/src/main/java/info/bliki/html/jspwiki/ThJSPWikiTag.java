package info.bliki.html.jspwiki;

import info.bliki.html.wikipedia.AbstractHTMLTag;
import info.bliki.htmlcleaner.TagNode;



public class ThJSPWikiTag extends AbstractHTMLTag {

	@Override
	public void open(TagNode node, StringBuilder resultBuffer) {
		resultBuffer.append("||");
	}
	
}
