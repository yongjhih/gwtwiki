package info.bliki.wiki.addon.trac;

import info.bliki.Messages;
import info.bliki.htmlcleaner.BaseToken;
import info.bliki.htmlcleaner.ContentToken;
import info.bliki.htmlcleaner.TagNode;
import info.bliki.htmlcleaner.TagToken;
import info.bliki.htmlcleaner.Utils;
import info.bliki.wiki.addon.filter.LaTeXConverter;
import info.bliki.wiki.filter.AbstractParser;
import info.bliki.wiki.filter.Encoder;
import info.bliki.wiki.filter.HTMLConverter;
import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.filter.MagicWord;
import info.bliki.wiki.filter.PDFConverter;
import info.bliki.wiki.filter.StringPair;
import info.bliki.wiki.filter.TemplateParser;
import info.bliki.wiki.filter.WikipediaParser;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.IConfiguration;
import info.bliki.wiki.model.IEventListener;
import info.bliki.wiki.model.ITableOfContent;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.model.ImageFormat;
import info.bliki.wiki.model.Reference;
import info.bliki.wiki.model.SemanticAttribute;
import info.bliki.wiki.model.SemanticRelation;
import info.bliki.wiki.tags.TableOfContentTag;
import info.bliki.wiki.tags.WPATag;
import info.bliki.wiki.tags.WPTag;
import info.bliki.wiki.tags.code.SourceCodeFormatter;
import info.bliki.wiki.tags.util.TagStack;
import info.bliki.wiki.template.ITemplateFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Standard model implementation for the Wikipedia syntax
 * 
 */
public abstract class AbstractTracModel implements IWikiModel {

	protected ArrayList<String> fCategoryNamespaces;

	protected ArrayList<String> fTemplateNamespaces;

	protected ArrayList<String> fImageNamespaces;

	protected ArrayList<Reference> fReferences;

	protected Map<String, Integer> fReferenceNames;

	protected int fRecursionLevel;

	protected boolean fReplaceColon;

	protected TagStack fTagStack;

	private boolean fInitialized;

	private int fNumber;

	private IConfiguration fConfiguration;

	private IEventListener fWikiListener = null;

	private ResourceBundle fResourceBundle;

	protected String fRedirectLink = null;

	private String fPageTitle;

	protected int fSectionCounter;

	/**
	 * A tag that manages the &quot;table of content&quot;
	 * 
	 */
	private TableOfContentTag fTableOfContentTag = null;
	/**
	 * &quot;table of content&quot;
	 * 
	 */
	private List<Object> fTableOfContent = null;
	/**
	 * Contains all anchor strings to create unique anchors
	 */
	private HashSet<String> fToCSet;

	public AbstractTracModel() {
		this(Configuration.DEFAULT_CONFIGURATION);
	}

	public AbstractTracModel(Configuration configuration) {
		this(configuration, Locale.ENGLISH);
	}

	public AbstractTracModel(Configuration configuration, Locale locale) {
		this(configuration, Messages.getResourceBundle(locale));
	}

	public AbstractTracModel(Configuration configuration, ResourceBundle resourceBundle) {
		fInitialized = false;
		fConfiguration = configuration;
		fResourceBundle = resourceBundle;
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
		aTagNode.addAttribute("class", "externallink", true);
		aTagNode.addAttribute("title", link, true);
		aTagNode.addAttribute("rel", "nofollow", true);
		aTagNode.addChild(new ContentToken(linkName));
	}

	public void appendInternalImageLink(String hrefImageLink, String srcImageLink, ImageFormat imageFormat) {
		int pxSize = imageFormat.getSize();
		String caption = imageFormat.getCaption();
		TagNode divTagNode = new TagNode("div");
		divTagNode.addAttribute("id", "image", true);
		divTagNode.addAttribute("href", hrefImageLink, true);
		divTagNode.addAttribute("src", srcImageLink, true);
		divTagNode.addObjectAttribute("wikiobject", imageFormat);
		if (pxSize != -1) {
			divTagNode.addAttribute("style", "width:" + pxSize + "px", true);
		}
		pushNode(divTagNode);

		if (caption != null && caption.length() > 0) {
			TagNode captionTagNode = new TagNode("div");
			String clazzValue = "caption";
			String type = imageFormat.getType();
			if (type != null) {
				clazzValue = type + clazzValue;
			}
			captionTagNode.addAttribute("class", clazzValue, true);
			pushNode(captionTagNode);
			TracParser.parseRecursive(caption, this);
			popNode();
		}

		popNode(); // div

	}

	public void appendInternalLink(String topic, String hashSection, String topicDescription, String cssClass, boolean parseRecursive) {
		WPATag aTagNode = new WPATag();
		append(aTagNode);
		aTagNode.addAttribute("id", "w", true);
		String href = topic;
		if (hashSection != null) {
			href = href + '#' + hashSection;
		}
		aTagNode.addAttribute("href", href, true);
		aTagNode.addObjectAttribute("wikilink", topic);

		ContentToken text = new ContentToken(topicDescription);
		aTagNode.addChild(text);
	}

	public String encodeTitleToUrl(String wikiTitle, boolean firstCharacterAsUpperCase) {
		return Encoder.encodeTitleToUrl(wikiTitle, firstCharacterAsUpperCase);
	}

	public String encodeTitleDotUrl(String wikiTitle, boolean firstCharacterAsUpperCase) {
		return Encoder.encodeTitleDotUrl(wikiTitle, firstCharacterAsUpperCase);
	}
	
	public void appendInterWikiLink(String namespace, String title, String linkText) {
		String hrefLink = getInterwikiMap().get(namespace.toLowerCase());
		if (hrefLink == null) {
			// shouldn't really happen
			hrefLink = "#";
		}

		String encodedtopic = encodeTitleToUrl(title, true);
		if (replaceColon()) {
			encodedtopic = encodedtopic.replaceAll(":", "/");
		}
		hrefLink = hrefLink.replace("${title}", encodedtopic);

		TagNode aTagNode = new TagNode("a");
		append(aTagNode);
		aTagNode.addAttribute("href", hrefLink, true);
		aTagNode.addChild(new ContentToken(linkText));
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

			if (appendRawNamespaceLinks(rawTopicName, viewableLinkDescription, (-1) == pipeIndex)) {
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
					appendInternalLink(rawTopicName, hash, viewableLinkDescription, null, true);
				} else {
					appendInternalLink(rawTopicName, null, viewableLinkDescription, null, true);
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
							appendInternalLink(relationValue, null, viewableLinkDescription, null, true);
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
				String category = rawNamespaceTopic.substring(colonIndex + 1);
				if (category != null && category.length() > 0) {
					addCategory(category, "");
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
		return getCategoryNamespace();
	}

	public String get2ndImageNamespace() {
		return getImageNamespace();
	}

	public String get2ndTemplateNamespace() {
		return getTemplateNamespace();
	}

	public String getCategoryNamespace() {
		return "Category";
	}

	public Map<String, SourceCodeFormatter> getCodeFormatterMap() {
		return fConfiguration.getCodeFormatterMap();
	}

	public String getImageNamespace() {
		return "Image";
	}

	public Map<String, String> getInterwikiMap() {
		return fConfiguration.getInterwikiMap();
	}

	public int getNextNumber() {
		return fNumber++;
	}

	public TagToken getNode(int offset) {
		return fTagStack.get(offset);
	}

	/**
	 * Get the raw wiki text for the given namespace and article name. Handles
	 * some MagicWord templates by default.
	 * 
	 * @param namespace
	 *          the namespace of this article
	 * @param templateName
	 * 
	 * @return <code>null</code> if no content was found
	 */
	public String getRawWikiContent(String namespace, String templateName, Map<String, String> templateParameters) {
		// String name = Encoder.encodeTitleUrl(articleName);
		if (isTemplateNamespace(namespace)) {
			if (MagicWord.isMagicWord(templateName)) {
				return MagicWord.processMagicWord(templateName, this);
			}
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

	// public TableOfContentTag getTableOfContentTag(boolean isTOCIdentifier) {
	// if (fTableOfContentTag == null) {
	// TableOfContentTag tableOfContentTag = new TableOfContentTag("div");
	// tableOfContentTag.addAttribute("id", "tableofcontent", true);
	// tableOfContentTag.setShowToC(false);
	// tableOfContentTag.setTOCIdentifier(isTOCIdentifier);
	// fTableOfContentTag = tableOfContentTag;
	// } else {
	// if (isTOCIdentifier) {
	// // try {
	// TableOfContentTag tableOfContentTag = (TableOfContentTag)
	// fTableOfContentTag.clone();
	// fTableOfContentTag.setShowToC(false);
	// tableOfContentTag.setShowToC(true);
	// tableOfContentTag.setTOCIdentifier(isTOCIdentifier);
	// fTableOfContentTag = tableOfContentTag;
	// // } catch (CloneNotSupportedException e) {
	// // e.printStackTrace();
	// // }
	// } else {
	// return fTableOfContentTag;
	// }
	// }
	// this.append(fTableOfContentTag);
	// return fTableOfContentTag;
	// }

	public ITemplateFunction getTemplateFunction(String name) {
		return getTemplateMap().get(name);
	}

	public Map<String, ITemplateFunction> getTemplateMap() {
		return fConfiguration.getTemplateMap();
	}

	public String getTemplateNamespace() {
		return "Template";
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

	private void initialize() {
		fNumber = 0;
		if (!fInitialized) {
			fWikiListener = null;
			fToCSet = null;
			fTableOfContent = null;
			fCategoryNamespaces = new ArrayList<String>();
			fTemplateNamespaces = new ArrayList<String>();
			fImageNamespaces = new ArrayList<String>();
			fTagStack = new TagStack();
			fReferences = null;
			fReferenceNames = null;
			fRecursionLevel = 0;
			fSectionCounter = 0;
			fReplaceColon = false;
			fCategoryNamespaces.add("Category");
			fTemplateNamespaces.add("Template");
			fImageNamespaces.add("Image");
			fInitialized = true;

		}
	}

	public boolean isCamelCaseEnabled() {
		return false;
	}

	public boolean isCategoryNamespace(String namespace) {
		for (int i = 0; i < fCategoryNamespaces.size(); i++) {
			if (namespace.equalsIgnoreCase(fCategoryNamespaces.get(i))) {
				return true;
			}
		}
		return false;
	}

	public boolean isEditorMode() {
		return false;
	}

	public boolean isImageNamespace(String namespace) {
		for (int i = 0; i < fImageNamespaces.size(); i++) {
			if (namespace.equalsIgnoreCase(fImageNamespaces.get(i))) {
				return true;
			}
		}
		return false;
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
		for (int i = 0; i < fTemplateNamespaces.size(); i++) {
			if (namespace.equalsIgnoreCase(fTemplateNamespaces.get(i))) {
				return true;
			}
		}
		return false;
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
		TracParser.parse(rawWikiText, this);
		fInitialized = false;
	}

	public String parseTemplates(String rawWikiText) {
		return parseTemplates(rawWikiText, false);
	}

	public String parseTemplates(String rawWikiText, boolean parseOnlySignature) {
		if (rawWikiText == null) {
			return "";
		}
		initialize();
		StringBuilder buf = new StringBuilder(rawWikiText.length() + rawWikiText.length() / 10);
		try {
			TemplateParser.parse(rawWikiText, this, buf, parseOnlySignature, false);
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

	/**
	 * 
	 */
	public String render(ITextConverter converter, String rawWikiText) {
		initialize();
		if (rawWikiText == null) {
			return "";
		}
		TracParser.parse(rawWikiText, this);
		StringBuilder buf = new StringBuilder(rawWikiText.length() + rawWikiText.length() / 10);
		List<BaseToken> list = fTagStack.getNodeList();

		try {
			converter.nodesToText(list, buf, this);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		fInitialized = false;
		return buf.toString();
	}

	public String render(String rawWikiText) {
		return render(new HTMLConverter(), rawWikiText);
	}

	public String renderLaTeX(String rawWikiText) {
		return render(new LaTeXConverter(), rawWikiText);
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
		TracParser.parse(rawWikiText, this);
		fInitialized = false;
		return fTagStack.getNodeList();
	}

	public ResourceBundle getResourceBundle() {
		return fResourceBundle;
	}

	public AbstractParser createNewInstance(String rawWikitext) {
		return new TracParser(rawWikitext, getWikiListener());
	}

	public void setPageName(String pageTitle) {
		fPageTitle = pageTitle;
	}

	public String getPageName() {
		return fPageTitle;
	}

	/**
	 * handle head for table of content
	 * 
	 * @param rawHead
	 * @param headLevel
	 */
	public ITableOfContent appendHead(String rawHead, int headLevel, boolean noToC, int headCounter) {
		TagStack localStack = WikipediaParser.parseRecursive(rawHead.trim(), this, true, true);

		WPTag headTagNode = new WPTag("h" + headLevel);
		headTagNode.addChildren(localStack.getNodeList());
		String tocHead = headTagNode.getBodyString();
		String anchor = Encoder.encodeUrl(tocHead);
		createTableOfContent(false);
		if (!noToC && (headCounter > 3)) {
			fTableOfContentTag.setShowToC(true);
		}
		if (fToCSet.contains(anchor)) {
			String newAnchor = anchor;
			for (int i = 2; i < Integer.MAX_VALUE; i++) {
				newAnchor = anchor + '_' + Integer.toString(i);
				if (!fToCSet.contains(newAnchor)) {
					break;
				}
			}
			anchor = newAnchor;
		}
		addToTableOfContent(fTableOfContent, tocHead, anchor, headLevel);
		if (getRecursionLevel() == 1) {
			buildEditLinkUrl(fSectionCounter++);
		}
		TagNode aTagNode = new TagNode("a");
		aTagNode.addAttribute("name", anchor, true);
		aTagNode.addAttribute("id", anchor, true);
		append(aTagNode);

		append(headTagNode);
		return fTableOfContentTag;
	}

	private void addToTableOfContent(List<Object> toc, String head, String anchor, int headLevel) {
		if (headLevel == 1) {
			toc.add(new StringPair(head, anchor));
		} else {
			if (toc.size() > 0) {
				if (toc.get(toc.size() - 1) instanceof List) {
					addToTableOfContent((List<Object>) toc.get(toc.size() - 1), head, anchor, --headLevel);
					return;
				}
			}
			ArrayList<Object> list = new ArrayList<Object>();
			toc.add(list);
			addToTableOfContent(list, head, anchor, --headLevel);
		}
	}

	/**
	 * 
	 * @param isTOCIdentifier
	 *          <code>true</code> if the __TOC__ keyword was parsed
	 */
	public ITableOfContent createTableOfContent(boolean isTOCIdentifier) {
		if (fTableOfContentTag == null) {
			TableOfContentTag tableOfContentTag = new TableOfContentTag("div");
			tableOfContentTag.addAttribute("id", "tableofcontent", true);
			tableOfContentTag.setShowToC(false);
			tableOfContentTag.setTOCIdentifier(isTOCIdentifier);
			fTableOfContentTag = tableOfContentTag;
			this.append(fTableOfContentTag);
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
				this.append(fTableOfContentTag);
			} else {
			}
		}

		// fTableOfContentTag = getTableOfContentTag(isTOCIdentifier);
		if (fTableOfContentTag != null) {
			if (fTableOfContent == null) {
				fTableOfContent = fTableOfContentTag.getTableOfContent();
			}
		}
		if (fToCSet == null) {
			fToCSet = new HashSet<String>();
		}
		return fTableOfContentTag;
	}
}
