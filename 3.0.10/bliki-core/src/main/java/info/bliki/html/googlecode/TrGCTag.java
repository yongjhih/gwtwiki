package info.bliki.html.googlecode;

import info.bliki.html.wikipedia.AbstractHTMLTag;
import info.bliki.htmlcleaner.TagNode;



public class TrGCTag extends AbstractHTMLTag {

	@Override
	public void open(TagNode node, StringBuilder resultBuffer) {
		resultBuffer.append("\n|");
	}

	@Override
	public void close(TagNode node, StringBuilder resultBuffer) {
		resultBuffer.append("|");
	}

}
