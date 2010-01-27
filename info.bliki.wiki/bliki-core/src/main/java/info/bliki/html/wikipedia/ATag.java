package info.bliki.html.wikipedia;

import info.bliki.htmlcleaner.BaseToken;
import info.bliki.htmlcleaner.TagNode;

import java.util.List;

public class ATag extends AbstractHTMLTag {
	protected final String openStr;

	protected final String closeStr;

	public ATag() {
		openStr = "[[";
		closeStr = "]]";
	}

	public ATag(String opener, String closer) {
		super();
		openStr = opener;
		closeStr = closer;
	}

	@Override
	public void open(TagNode node, StringBuilder resultBuffer) {
	}

	@Override
	public void content(AbstractHTMLToWiki w, TagNode node, StringBuilder resultBuffer, boolean showWithoutTag) {
		List<Object> children = node.getChildren();
		StringBuilder buf = new StringBuilder();
		BaseToken tok = getFirstContent(children, "img");
		if (tok != null && tok instanceof TagNode) {
			w.nodeToWiki(tok, resultBuffer);
		} else {
			resultBuffer.append(openStr);
			// no wiki tags inside wiki links:
			w.nodesToPlainText(children, buf);
			char ch;
			for (int i = 0; i < buf.length(); i++) {
				ch = buf.charAt(i);
				if (ch == '\n' || ch == '\r' || ch == '\t') {
					buf.setCharAt(i, ' ');
				}
			}
			String str = buf.toString();
			resultBuffer.append(str.trim());
			// w.nodesToText(children, resultBuffer);
			resultBuffer.append(closeStr);
		}
	}

	@Override
	public void close(TagNode node, StringBuilder resultBuffer) {
	}
}
