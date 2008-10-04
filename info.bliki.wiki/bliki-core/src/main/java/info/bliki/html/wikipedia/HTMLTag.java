package info.bliki.html.wikipedia;

import info.bliki.htmlcleaner.TagNode;

/**
 * Interface for HTML to Wiki Tags
 * 
 */
public interface HTMLTag {

	/**
	 * 
	 * @param node
	 * @param resultBuffer
	 */
	public abstract void open(TagNode node, StringBuilder resultBuffer);

	/**
	 * Convert the curretn Tag into wiki text.
	 * 
	 * @param w
	 * @param node
	 * @param resultBuffer
	 * @param showWithoutTag
	 */
	public abstract void content(AbstractHTMLToWiki w, TagNode node, StringBuilder resultBuffer, boolean showWithoutTag);

	public abstract void close(TagNode node, StringBuilder resultBuffer);

}