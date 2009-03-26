package info.bliki.html.wikipedia;

import info.bliki.htmlcleaner.TagNode;


public class TdTag extends AbstractHTMLTag {

	@Override
	public void open(TagNode node, StringBuilder resultBuffer) {
		resultBuffer.append("\n|");
	}

}
