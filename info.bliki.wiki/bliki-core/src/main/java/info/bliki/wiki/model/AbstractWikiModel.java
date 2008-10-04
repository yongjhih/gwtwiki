package info.bliki.wiki.model;

import info.bliki.Messages;
import info.bliki.htmlcleaner.BaseToken;
import info.bliki.htmlcleaner.ContentToken;
import info.bliki.htmlcleaner.TagNode;
import info.bliki.htmlcleaner.TagToken;
import info.bliki.htmlcleaner.Utils;
import info.bliki.wiki.filter.AbstractParser;
import info.bliki.wiki.filter.Encoder;
import info.bliki.wiki.filter.HTMLConverter;
import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.filter.MagicWord;
import info.bliki.wiki.filter.PDFConverter;
import info.bliki.wiki.filter.TemplateParser;
import info.bliki.wiki.filter.WikipediaParser;
import info.bliki.wiki.tags.TableOfContentTag;
import info.bliki.wiki.tags.WPATag;
import info.bliki.wiki.tags.code.SourceCodeFormatter;
import info.bliki.wiki.tags.util.TagStack;
import info.bliki.wiki.template.ITemplateFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * Standard model implementation for the Wikipedia syntax
 * 
 */
public abstract class AbstractWikiModel implements IWikiModel {
	private static int fNextNumberCounter = 0;

	protected final String[] fCategoryNamespaces = {
			"Category", "Category"
	};

	protected final String[] fTemplateNamespaces = {
			"Template", "Template"
	};

	protected final String[] fImageNamespaces = {
			"Image", "Image"
	};

	protected ArrayList<Reference> fReferences;

	protected Map<String, Integer> fReferenceNames;

	protected int fRecursionLevel;

	protected boolean fReplaceColon;

	protected TagStack fTagStack;

	private boolean fInitialized;

	private IConfiguration fConfiguration;

	private IEventListener fWikiListener = null;

	private ResourceBundle fResourceBundle;

	protected String fRedirectLink = null;

	protected TableOfContentTag fTableOfContentTag = null;

	protected String fPageTitle = "PAGENAME";

	public AbstractWikiModel() {
		this(Configuration.DEFAULT_CONFIGURATION);
	}

	public AbstractWikiModel(Configuration configuration) {
		this(configuration, Locale.ENGLISH);
	}

	public AbstractWikiModel(Configuration configuration, Locale locale) {
		this(configuration, Messages.getResourceBundle(locale));
	}

	public AbstractWikiModel(Configuration configuration, ResourceBundle resourceBundle) {
		fInitialized = false;
		fConfiguration = configuration;
		fResourceBundle = resourceBundle;
		String ns1, ns2;
		ns1 = Messages.getString(resourceBundle, Messages.WIKI_API_CATEGORY1);
		if (ns1 != null) {
			fCategoryNamespaces[0] = ns1;
			ns2 = Messages.getString(resourceBundle, Messages.WIKI_API_CATEGORY2);
			if (ns2 != null) {
				fCategoryNamespaces[1] = ns2;
			}
		}
		ns1 = Messages.getString(resourceBundle, Messages.WIKI_API_TEMPLATE1);
		if (ns1 != null) {
			fTemplateNamespaces[0] = ns1;
			ns2 = Messages.getString(resourceBundle, Messages.WIKI_API_TEMPLATE2);
			if (ns2 != null) {
				fTemplateNamespaces[1] = ns2;
			}
		}
		ns1 = Messages.getString(resourceBundle, Messages.WIKI_API_IMAGE1);
		if (ns1 != null) {
			fImageNamespaces[0] = ns1;
			ns2 = Messages.getString(resourceBundle, Messages.WIKI_API_IMAGE2);
			if (ns2 != null) {
				fImageNamespaces[1] = ns2;
			}
		}
		initialize();
	}

	public void addCategory(String categoryName, String sortKey) {

	}

	public SourceCodeFormatter addCodeFormatter(String key, SourceCodeFormatter value) {
		return fConfiguration.addCodeFormatter(key, value);
	}

	public String addInterwikiLink(String key, String value) {
		return fConfiguration.addInterwikiLink(key, value);
	}

	public void addLink(String topicName) {

	}

	public boolean addSemanticAttribute(String attribute, String attributeValue) {
		return false;
	}

	public boolean addSemanticRelation(String relation, String relationValue) {
		return false;
	}

	public void addTemplate(String template) {

	}

	public ITemplateFunction addTemplateFunction(String key, ITemplateFunction value) {
		return fConfiguration.addTemplateFunction(key, value);
	}

	public TagToken addTokenTag(String key, TagToken value) {
		return fConfiguration.addTokenTag(key, value);
	}

	public String[] addToReferences(String reference, String nameAttribute) {
		String[] result = new String[2];
		result[1] = null;
		if (fReferences == null) {
			fReferences = new ArrayList<Reference>();
			fReferenceNames = new HashMap<String, Integer>();
		}
		if (nameAttribute != null) {
			Integer index = fReferenceNames.get(nameAttribute);
			if (index != null) {
				result[0] = index.toString();
				Reference ref = fReferences.get(index - 1);
				int count = ref.incCounter();
				if (count >= Reference.CHARACTER_REFS.length()) {
					result[1] = nameAttribute + '_' + 'Z';
				} else {
					result[1] = nameAttribute + '_' + Reference.CHARACTER_REFS.charAt(count);
				}
				return result;
			}
		}

		if (nameAttribute != null) {
			fReferences.add(new Reference(reference, nameAttribute));
			Integer index = Integer.valueOf(fReferences.size());
			fReferenceNames.put(nameAttribute, index);
			result[1] = nameAttribute + "_a";
		} else {
			fReferences.add(new Reference(reference));
		}
		result[0] = Integer.toString(fReferences.size());
		return result;
	}

	public void append(BaseToken contentNode) {
		fTagStack.append(contentNode);
	}

	public void appendExternalImageLink(String imageSrc, String imageAltText) {
		TagNode spanTagNode = new TagNode("span");
		append(spanTagNode);
		spanTagNode.addAttribute("class", "image", true);
		TagNode imgTagNode = new TagNode("img");
		spanTagNode.addChild(imgTagNode);
		imgTagNode.addAttribute("src", imageSrc, true);
		imgTagNode.addAttribute("alt", imageAltText, true);
		imgTagNode.addAttribute("rel", "nofollow", true);

		// writer.append("<span class=\"image\">");
		// writer.append("<img src=\"");
		// Encoder.encodeHtml(imageSrc, writer);
		// writer.append("\" alt=\"");
		// Encoder.encodeHtml(imageAltText, writer);
		// writer.append("\" /></span>");
	}

	public void appendExternalLink(String link, String linkName, boolean withoutSquareBrackets) {
		// is it an image?
		link = Utils.escapeXml(link, true, false, false);
		// int indx = link.lastIndexOf(".");
		// if (indx > 0 && indx < (link.length() - 3)) {
		// String ext = link.substring(indx + 1);
		// if (ext.equalsIgnoreCase("gif") || ext.equalsIgnoreCase("png") ||
		// ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")
		// || ext.equalsIgnoreCase("bmp")) {
		// appendExternalImageLink(link, linkName);
		// return;
		// }
		// }
		TagNode aTagNode = new TagNode("a");
		aTagNode.addAttribute("href", link, true);
		aTagNode.addAttribute("class", "externallink", true);
		aTagNode.addAttribute("title", link, true);
		aTagNode.addAttribute("rel", "nofollow", true);
		if (withoutSquareBrackets) {
			append(aTagNode);
			aTagNode.addChild(new ContentToken(linkName));
		} else {
			String trimmedText = linkName.trim();
			if (trimmedText.length() > 0) {
				pushNode(aTagNode);
				WikipediaParser.parseRecursive(trimmedText, this, false, true);
				popNode();
			}
		}
	}

	public void appendInternalImageLink(String hrefImageLink, String srcImageLink, ImageFormat imageFormat) {
		int pxSize = imageFormat.getSize();
		String caption = imageFormat.getCaption();
		TagNode divTagNode = new TagNode("div");
		divTagNode.addAttribute("id", "image", false);
		divTagNode.addAttribute("href", hrefImageLink, false);
		divTagNode.addAttribute("src", srcImageLink, false);
		divTagNode.addObjectAttribute("wikiobject", imageFormat);
		if (pxSize != -1) {
			divTagNode.addAttribute("style", "width:" + pxSize + "px", false);
		}
		pushNode(divTagNode);

		if (caption != null && caption.length() > 0) {

			TagNode captionTagNode = new TagNode("div");
			String clazzValue = "caption";
			String type = imageFormat.getType();
			if (type != null) {
				clazzValue = type + clazzValue;
			}
			captionTagNode.addAttribute("class", clazzValue, false);
			//			
			TagStack localStack = WikipediaParser.parseRecursive(caption, this, true, true);
			captionTagNode.addChildren(localStack.getNodeList());
			String altAttribute = captionTagNode.getBodyString();
			imageFormat.setAlt(altAttribute);
			pushNode(captionTagNode);
			// WikipediaParser.parseRecursive(caption, this);
			popNode();
		}

		popNode(); // div

	}

	public void appendInternalLink(String topic, String hashSection, String topicDescription, String cssClass) {
		WPATag aTagNode = new WPATag();
		// append(aTagNode);
		aTagNode.addAttribute("id", "w", true);
		String href = topic;
		if (hashSection != null) {
			href = href + '#' + Encoder.encodeTitleUrl(hashSection);
		}
		aTagNode.addAttribute("href", href, true);
		if (cssClass != null) {
			aTagNode.addAttribute("class", cssClass, true);
		}
		aTagNode.addObjectAttribute("wikilink", topic);
		pushNode(aTagNode);
		WikipediaParser.parseRecursive(topicDescription.trim(), this, false, true);
		popNode();
		// ContentToken text = new ContentToken(topicDescription);
		// aTagNode.addChild(text);
	}

	public void appendInterWikiLink(String namespace, String title, String linkText) {
		String hrefLink = getInterwikiMap().get(namespace.toLowerCase());
		if (hrefLink == null) {
			// shouldn't really happen
			hrefLink = "#";
		}

		String encodedtopic = Encoder.encodeTitleUrl(title);
		if (replaceColon()) {
			encodedtopic = encodedtopic.replaceAll(":", "/");
		}
		hrefLink = hrefLink.replace("${title}", encodedtopic);

		TagNode aTagNode = new TagNode("a");
		// append(aTagNode);
		aTagNode.addAttribute("href", hrefLink, true);
		// aTagNode.addChild(new ContentToken(linkText));
		pushNode(aTagNode);
		WikipediaParser.parseRecursive(linkText.trim(), this, false, true);
		popNode();
	}

	public void appendISBNLink(String isbnPureText) {
		StringBuffer isbnUrl = new StringBuffer(isbnPureText.length() + 100);
		isbnUrl.append("http://www.amazon.com/exec/obidos/ASIN/");

		for (int index = 0; index < isbnPureText.length(); index++) {
			if (isbnPureText.charAt(index) >= '0' && isbnPureText.charAt(index) <= '9') {
				isbnUrl.append(isbnPureText.charAt(index));
			}
		}

		String isbnString = isbnUrl.toString();
		TagNode aTagNode = new TagNode("a");
		append(aTagNode);
		aTagNode.addAttribute("href", isbnString, true);
		aTagNode.addAttribute("class", "external text", true);
		aTagNode.addAttribute("title", isbnString, true);
		aTagNode.addAttribute("rel", "nofollow", true);
		aTagNode.addChild(new ContentToken(isbnPureText));
	}

	public void appendMailtoLink(String link, String linkName, boolean withoutSquareBrackets) {
		// is it an image?
		link = Utils.escapeXml(link, true, false, false);
		int indx = link.lastIndexOf(".");
		if (indx > 0 && indx < (link.length() - 3)) {
			String ext = link.substring(indx + 1);
			if (ext.equalsIgnoreCase("gif") || ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")
					|| ext.equalsIgnoreCase("bmp")) {
				appendExternalImageLink(link, linkName);
				return;
			}
		}
		TagNode aTagNode = new TagNode("a");
		append(aTagNode);
		aTagNode.addAttribute("href", link, true);
		aTagNode.addAttribute("class", "external free", true);
		aTagNode.addAttribute("title", link, true);
		aTagNode.addAttribute("rel", "nofollow", true);
		aTagNode.addChild(new ContentToken(linkName));
	}

	public void appendRawWikipediaLink(String rawLinkText, String suffix) {
		String rawTopicName = rawLinkText;
		if (rawTopicName != null) {
			// trim the name for whitespace characters on the left side
			int trimLeftIndex = 0;
			while ((trimLeftIndex < rawTopicName.length()) && (rawTopicName.charAt(trimLeftIndex) <= ' ')) {
				trimLeftIndex++;
			}
			if (trimLeftIndex > 0) {
				rawTopicName = rawTopicName.substring(trimLeftIndex);
			}
			// Is there an alias like [alias|link] ?
			int pipeIndex = rawTopicName.lastIndexOf('|');
			String alias = "";
			if (-1 != pipeIndex) {
				alias = rawTopicName.substring(pipeIndex + 1);
				rawTopicName = rawTopicName.substring(0, pipeIndex);
				if (alias.length() == 0) {
					// special cases like: [[Test:hello world|]] or [[Test(hello
					// world)|]]
					// or [[Test, hello world|]]
					alias = rawTopicName;
					int index = alias.indexOf(':');
					if (index != -1) {
						alias = alias.substring(index + 1).trim();
					} else {
						index = alias.indexOf('(');
						if (index != -1) {
							alias = alias.substring(0, index).trim();
						} else {
							index = alias.indexOf(',');
							if (index != -1) {
								alias = alias.substring(0, index).trim();
							}
						}
					}
				}
			}

			int hashIndex = rawTopicName.lastIndexOf('#');

			String hash = "";
			if (-1 != hashIndex && hashIndex != rawTopicName.length() - 1) {
				hash = rawTopicName.substring(hashIndex + 1);
				rawTopicName = rawTopicName.substring(0, hashIndex);
			}

			// trim the name for whitespace characters on the right side
			int trimRightIndex = rawTopicName.length() - 1;
			while ((trimRightIndex >= 0) && (rawTopicName.charAt(trimRightIndex) <= ' ')) {
				trimRightIndex--;
			}
			if (trimRightIndex != rawTopicName.length() - 1) {
				rawTopicName = rawTopicName.substring(0, trimRightIndex + 1);
			}

			rawTopicName = Encoder.encodeHtml(rawTopicName);
			String viewableLinkDescription;
			if (-1 != pipeIndex) {
				viewableLinkDescription = alias + suffix;
			} else {
				if (rawTopicName.length() > 0 && rawTopicName.charAt(0) == ':') {
					viewableLinkDescription = rawTopicName.substring(1) + suffix;
				} else {
					viewableLinkDescription = rawTopicName + suffix;
				}
			}

			if (appendRawNamespaceLinks(rawTopicName, viewableLinkDescription, pipeIndex == (-1))) {
				return;
			}

			int indx = rawTopicName.indexOf(':');
			String namespace = null;
			if (indx >= 0) {
				namespace = rawTopicName.substring(0, indx);
			}
			if (namespace != null && isImageNamespace(namespace)) {
				parseInternalImageLink(namespace, rawLinkText);
				return;
			} else {
				if (rawTopicName.length() > 0 && rawTopicName.charAt(0) == ':') {
					rawTopicName = rawTopicName.substring(1);
				}
				if (rawTopicName.length() > 0 && rawTopicName.charAt(0) == ':') {
					rawTopicName = rawTopicName.substring(1);
				}
				addLink(rawTopicName);
				if (-1 != hashIndex) {
					appendInternalLink(rawTopicName, hash, viewableLinkDescription, null);
				} else {
					appendInternalLink(rawTopicName, null, viewableLinkDescription, null);
				}
			}
		}
	}

	public boolean appendRawNamespaceLinks(String rawNamespaceTopic, String viewableLinkDescription, boolean containsNoPipe) {
		int colonIndex = rawNamespaceTopic.indexOf(':');

		if (colonIndex != (-1)) {
			String nameSpace = rawNamespaceTopic.substring(0, colonIndex);

			if (isSemanticWebActive() && (rawNamespaceTopic.length() > colonIndex + 1)) {
				// See <a
				// href="http://en.wikipedia.org/wiki/Semantic_MediaWiki">Semantic
				// MediaWiki</a> for more information.
				if (rawNamespaceTopic.charAt(colonIndex + 1) == ':') {
					// found an SMW relation
					String relationValue = rawNamespaceTopic.substring(colonIndex + 2);

					if (addSemanticRelation(nameSpace, relationValue)) {
						if (containsNoPipe) {
							viewableLinkDescription = relationValue;
						}
						if (viewableLinkDescription.trim().length() > 0) {
							appendInternalLink(relationValue, null, viewableLinkDescription, "interwiki");
						}
						return true;
					}
				} else if (rawNamespaceTopic.charAt(colonIndex + 1) == '=') {
					// found an SMW attribute
					String attributeValue = rawNamespaceTopic.substring(colonIndex + 2);
					if (addSemanticAttribute(nameSpace, attributeValue)) {
						append(new ContentToken(attributeValue));
						return true;
					}
				}

			}
			if (isCategoryNamespace(nameSpace)) {
				// add the category to this texts metadata
				String category = rawNamespaceTopic.substring(colonIndex + 1).trim();
				if (category != null && category.length() > 0) {
					// TODO implement more sort-key behaviour
					// http://en.wikipedia.org/wiki/Wikipedia:Categorization#Category_sorting
					addCategory(category, viewableLinkDescription);
					return true;
				}
			} else if (isInterWiki(nameSpace)) {
				String title = rawNamespaceTopic.substring(colonIndex + 1);
				if (title != null && title.length() > 0) {
					appendInterWikiLink(nameSpace, title, viewableLinkDescription);
					return true;
				}
			}
		}
		return false;
	}

	public boolean appendRedirectLink(String redirectLink) {
		fRedirectLink = redirectLink;
		return true;
	}

	public void appendSignature(Appendable writer, int numberOfTildes) throws IOException {
		switch (numberOfTildes) {
		case 3:
			writer.append("~~~");
			break;
		case 4:
			writer.append("~~~~");
			break;
		case 5:
			writer.append("~~~~~");
			break;
		}
	}

	public void appendStack(TagStack stack) {
		if (stack != null) {
			fTagStack.append(stack);
		}
	}

	public void buildEditLinkUrl(int section) {
	}

	public int decrementRecursionLevel() {
		return --fRecursionLevel;
	}

	public String get2ndCategoryNamespace() {
		return fCategoryNamespaces[1];
	}

	public String get2ndImageNamespace() {
		return fImageNamespaces[1];
	}

	public String get2ndTemplateNamespace() {
		return fTemplateNamespaces[1];
	}

	public String getCategoryNamespace() {
		return fCategoryNamespaces[0];
	}

	public Map<String, SourceCodeFormatter> getCodeFormatterMap() {
		return fConfiguration.getCodeFormatterMap();
	}

	public String getImageNamespace() {
		return fImageNamespaces[0];
	}

	public Map<String, String> getInterwikiMap() {
		return fConfiguration.getInterwikiMap();
	}

	public synchronized int getNextNumber() {
		return fNextNumberCounter++;
	}

	public TagToken getNode(int offset) {
		return fTagStack.get(offset);
	}

	public String getRawWikiContent(String namespace, String templateName, Map<String, String> templateParameters) {
		// String name = Encoder.encodeTitleUrl(articleName);
		if (MagicWord.isMagicWord(templateName)) {
			return MagicWord.processMagicWord(templateName, this);
		}
		return null;
	}

	public int getRecursionLevel() {
		return fRecursionLevel;
	}

	public String getRedirectLink() {
		return fRedirectLink;
	}

	public List<Reference> getReferences() {
		return fReferences;
	}

	public List<SemanticAttribute> getSemanticAttributes() {
		return null;
	}

	public List<SemanticRelation> getSemanticRelations() {
		return null;
	}

	public TableOfContentTag getTableOfContentTag(boolean isTOCIdentifier) {
		if (fTableOfContentTag == null) {
			TableOfContentTag tableOfContentTag = new TableOfContentTag("div");
			tableOfContentTag.addAttribute("id", "tableofcontent", true);
			tableOfContentTag.setShowToC(false);
			tableOfContentTag.setTOCIdentifier(isTOCIdentifier);
			fTableOfContentTag = tableOfContentTag;
		} else {
			if (isTOCIdentifier) {
				// try {
				TableOfContentTag tableOfContentTag = (TableOfContentTag) fTableOfContentTag.clone();
				fTableOfContentTag.setShowToC(false);
				tableOfContentTag.setShowToC(true);
				tableOfContentTag.setTOCIdentifier(isTOCIdentifier);
				fTableOfContentTag = tableOfContentTag;
				// } catch (CloneNotSupportedException e) {
				// e.printStackTrace();
				// }
			} else {
				return fTableOfContentTag;
			}
		}
		this.append(fTableOfContentTag);
		return fTableOfContentTag;
	}

	public ITemplateFunction getTemplateFunction(String name) {
		return getTemplateMap().get(name);
	}

	public Map<String, ITemplateFunction> getTemplateMap() {
		return fConfiguration.getTemplateMap();
	}

	public String getTemplateNamespace() {
		return fTemplateNamespaces[0];
	}

	public Map<String, TagToken> getTokenMap() {
		return fConfiguration.getTokenMap();
	}

	public IEventListener getWikiListener() {
		return fWikiListener;
	}

	public int incrementRecursionLevel() {
		return ++fRecursionLevel;
	}

	protected void initialize() {
		if (!fInitialized) {
			fWikiListener = null;
			fTagStack = new TagStack();
			fReferences = null;
			fReferenceNames = null;
			fRecursionLevel = 0;
			fReplaceColon = false;
			fInitialized = true;
		}
	}

	public boolean isCategoryNamespace(String namespace) {
		return namespace.equalsIgnoreCase(fCategoryNamespaces[0]) || namespace.equalsIgnoreCase(fCategoryNamespaces[1]);
	}

	public boolean isEditorMode() {
		return false;
	}

	public boolean isImageNamespace(String namespace) {
		return namespace.equalsIgnoreCase(fImageNamespaces[0]) || namespace.equalsIgnoreCase(fImageNamespaces[1]);
	}

	public boolean isInterWiki(String namespace) {
		return getInterwikiMap().containsKey(namespace.toLowerCase());
	}

	public boolean isMathtranRenderer() {
		return false;
	}

	public boolean isNamespace(String namespace) {
		return isImageNamespace(namespace) || isTemplateNamespace(namespace) || isCategoryNamespace(namespace);
	}

	public boolean isPreviewMode() {
		return false;
	}

	public boolean isSemanticWebActive() {
		return false;
	}

	public boolean isTemplateNamespace(String namespace) {
		return namespace.equalsIgnoreCase(fTemplateNamespaces[0]) || namespace.equalsIgnoreCase(fTemplateNamespaces[1]);
	}

	public boolean isTemplateTopic() {
		return false;
	}

	public boolean parseBBCodes() {
		return false;
	}

	public void parseEvents(IEventListener listener, String rawWikiText) {
		initialize();
		if (rawWikiText == null) {
			return;
		}
		fWikiListener = listener;
		WikipediaParser.parse(rawWikiText, this, false, null);
		fInitialized = false;
	}

	public String parseTemplates(String rawWikiText) {
		return parseTemplates(rawWikiText, false);
	}

	public String parseTemplates(String rawWikiText, boolean parseOnlySignature) {
		if (rawWikiText == null) {
			return "";
		}
		if (!parseOnlySignature) {
			initialize();
		}
		StringBuilder buf = new StringBuilder(rawWikiText.length() + rawWikiText.length() / 10);
		try {
			TemplateParser.parse(rawWikiText, this, buf, parseOnlySignature, true);
		} catch (Exception ioe) {
			ioe.printStackTrace();
			buf.append("<span class=\"error\">TemplateParser exception: " + ioe.getClass().getSimpleName() + "</span>");
		}
		return buf.toString();
	}

	public TagToken peekNode() {
		return fTagStack.peek();
	}

	public TagToken popNode() {
		return fTagStack.pop();
	}

	public boolean pushNode(TagToken node) {
		return fTagStack.push(node);
	}

	public String render(ITextConverter converter, String rawWikiText) {
		initialize();
		if (rawWikiText == null) {
			return "";
		}
		WikipediaParser.parse(rawWikiText, this, true, null);
		if (converter != null) {
			StringBuilder buf = new StringBuilder(rawWikiText.length() + rawWikiText.length() / 10);
			List<BaseToken> list = fTagStack.getNodeList();

			try {
				converter.nodesToText(list, buf, this);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				fInitialized = false;
			}
			return buf.toString();
		}
		fInitialized = false;
		return null;
	}

	public String render(String rawWikiText) {
		return render(new HTMLConverter(), rawWikiText);
	}

	public String renderPDF(String rawWikiText) {
		return render(new PDFConverter(), rawWikiText);
	}

	public boolean replaceColon() {
		return true;
	}

	public void setSemanticWebActive(boolean semanticWeb) {

	}

	public void setUp() {
		fRecursionLevel = 0;
	}

	public boolean showSyntax(String tagName) {
		return true;
	}

	public int stackSize() {
		return fTagStack.size();
	}

	public TagStack swapStack(TagStack stack) {
		TagStack temp = fTagStack;
		fTagStack = stack;
		return temp;
	}

	public void tearDown() {

	}

	public List<BaseToken> toNodeList(String rawWikiText) {
		initialize();
		if (rawWikiText == null) {
			return new ArrayList<BaseToken>();
		}
		WikipediaParser.parse(rawWikiText, this, true, null);
		fInitialized = false;
		return fTagStack.getNodeList();
	}

	public ResourceBundle getResourceBundle() {
		return fResourceBundle;
	}

	public AbstractParser createNewInstance(String rawWikitext) {
		return new WikipediaParser(rawWikitext, isTemplateTopic(), getWikiListener());
	}

	public void setPageName(String pageTitle) {
		fPageTitle = pageTitle;
	}

	public String getPageName() {
		return fPageTitle;
	}
}
