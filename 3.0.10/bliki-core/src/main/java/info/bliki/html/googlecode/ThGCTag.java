package info.bliki.html.googlecode;

import info.bliki.html.wikipedia.AbstractHTMLTag;
import info.bliki.htmlcleaner.TagNode;



public class ThGCTag extends AbstractHTMLTag {

	@Override
	public void open(TagNode node, StringBuilder resultBuffer) {
		resultBuffer.append("| *");
	}
	@Override
	public void close(TagNode node, StringBuilder resultBuffer) {
		resultBuffer.append("* |");
	}
}
