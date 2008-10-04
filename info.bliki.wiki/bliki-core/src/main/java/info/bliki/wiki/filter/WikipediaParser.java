package info.bliki.wiki.filter;

import info.bliki.htmlcleaner.ContentToken;
import info.bliki.htmlcleaner.EndTagToken;
import info.bliki.htmlcleaner.TagNode;
import info.bliki.htmlcleaner.TagToken;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.DefaultEventListener;
import info.bliki.wiki.model.IEventListener;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.DdTag;
import info.bliki.wiki.tags.DlTag;
import info.bliki.wiki.tags.DtTag;
import info.bliki.wiki.tags.HTMLBlockTag;
import info.bliki.wiki.tags.HTMLTag;
import info.bliki.wiki.tags.HrTag;
import info.bliki.wiki.tags.PTag;
import info.bliki.wiki.tags.TableOfContentTag;
import info.bliki.wiki.tags.WPBoldItalicTag;
import info.bliki.wiki.tags.WPPreTag;
import info.bliki.wiki.tags.WPTag;
import info.bliki.wiki.tags.util.Attribute;
import info.bliki.wiki.tags.util.IBodyTag;
import info.bliki.wiki.tags.util.INoBodyParsingTag;
import info.bliki.wiki.tags.util.NodeAttribute;
import info.bliki.wiki.tags.util.TagStack;
import info.bliki.wiki.tags.util.WikiTagNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.validator.EmailValidator;


/**
 * A Wikipedia syntax parser for the second pass in the parsing of a Wikipedia
 * source text.
 * 
 * @see TemplateParser for the first pass
 */
public class WikipediaParser extends AbstractParser implements IParser {
	private static final String[] TOC_IDENTIFIERS = {
			"TOC", "NOTOC", "FORCETOC"
	};

	final static String HEADER_STRINGS[] = {
			"=", "==", "===", "====", "=====", "======"
	};

	final static int TokenNotFound = -2;

	final static int TokenIgnore = -1;

	final static int TokenSTART = 0;

	final static int TokenEOF = 1;

	final static int TokenBOLD = 3;

	final static int TokenITALIC = 4;

	final static int TokenBOLDITALIC = 5;

	final static HTMLTag BOLD = new WPTag("b");

	final static HTMLTag ITALIC = new WPTag("i");

	final static HTMLTag BOLDITALIC = new WPBoldItalicTag();

	final static HTMLTag STRONG = new WPTag("strong");

	final static HTMLTag EM = new WPTag("em");

	/**
	 * &quot;table of content&quot;
	 * 
	 */
	private List<Object> fTableOfContent = null;

	private TableOfContentTag fTableOfContentTag = null;

	private HashSet<String> fToCSet = null;

	private int fHeadCounter = 0;

	private int fSectionCounter = 1;

	/**
	 * Enable HTML tags
	 */
	private boolean fHtmlCodes = true;

	private boolean fNoToC = false;

	private boolean fRenderTemplate = false;

	private boolean fForceToC = false;

	private IEventListener fEventListener = null;

	public WikipediaParser(String stringSource, boolean renderTemplate) {
		this(stringSource, renderTemplate, null);
	}

	public WikipediaParser(String stringSource, boolean renderTemplate, IEventListener wikiListener) {
		super(stringSource);
		fRenderTemplate = renderTemplate;
		if (wikiListener == null) {
			fEventListener = DefaultEventListener.CONST;
		} else {
			fEventListener = wikiListener;
		}
	}

	/**
	 * copy the content in the resulting ContentToken
	 * 
	 */
	private void createContentToken(boolean whiteStart, final int whiteStartPosition, final int diff) {
		if (whiteStart) {
			try {
				final int whiteEndPosition = fCurrentPosition - diff;
				int count = whiteEndPosition - whiteStartPosition;
				if (count > 0) {
					fWikiModel.append(new ContentToken(new String(fSource, whiteStartPosition, count)));
				}
			} finally {
				fWhiteStart = false;
			}
		}
	}

	protected final boolean getNextChar(char testedChar) {
		int temp = fCurrentPosition;
		try {
			fCurrentCharacter = fSource[fCurrentPosition++];
			if (fCurrentCharacter != testedChar) {
				fCurrentPosition = temp;
				return false;
			}
			return true;

		} catch (IndexOutOfBoundsException e) {
			fCurrentPosition = temp;
			return false;
		}
	}

	protected final int getNextChar(char testedChar1, char testedChar2) {
		int temp = fCurrentPosition;
		try {
			int result;
			fCurrentCharacter = fSource[fCurrentPosition++];
			if (fCurrentCharacter == testedChar1)
				result = 0;
			else if (fCurrentCharacter == testedChar2)
				result = 1;
			else {
				fCurrentPosition = temp;
				return -1;
			}
			return result;
		} catch (IndexOutOfBoundsException e) {
			fCurrentPosition = temp;
			return -1;
		}
	}

	protected final boolean getNextCharAsDigit() {
		int temp = fCurrentPosition;
		try {
			fCurrentCharacter = fSource[fCurrentPosition++];
			if (!Character.isDigit(fCurrentCharacter)) {
				fCurrentPosition = temp;
				return false;
			}
			return true;
		} catch (IndexOutOfBoundsException e) {
			fCurrentPosition = temp;
			return false;
		}
	}

	protected final boolean getNextCharAsDigit(int radix) {

		int temp = fCurrentPosition;
		try {
			fCurrentCharacter = fSource[fCurrentPosition++];

			if (Character.digit(fCurrentCharacter, radix) == -1) {
				fCurrentPosition = temp;
				return false;
			}
			return true;
		} catch (IndexOutOfBoundsException e) {
			fCurrentPosition = temp;
			return false;
		}
	}

	protected final int getNumberOfChar(char testedChar) {
		int number = 0;
		try {
			while ((fCurrentCharacter = fSource[fCurrentPosition++]) == testedChar) {
				number++;
			}
		} catch (IndexOutOfBoundsException e) {

		}
		fCurrentPosition--;
		return number;
	}

	protected boolean getNextCharAsWikiPluginIdentifierPart() {
		int temp = fCurrentPosition;
		try {
			fCurrentCharacter = fSource[fCurrentPosition++];

			if (!Encoder.isWikiPluginIdentifierPart(fCurrentCharacter)) {
				fCurrentPosition = temp;
				return false;
			}
			return true;
		} catch (IndexOutOfBoundsException e) {
			fCurrentPosition = temp;
			return false;
		}
	}

	protected int getNextToken() // throws InvalidInputException
	{
		fWhiteStart = true;
		fWhiteStartPosition = fCurrentPosition;
		try {
			while (true) {
				fCurrentCharacter = fSource[fCurrentPosition++];

				// ---------Identify the next token-------------
				switch (fCurrentCharacter) {
				case '{':
					// dummy parsing of wikipedia templates for event listeners
					if (parseTemplate()) {
					} else {
						// wikipedia table handling
						if (parseTable()) {
							continue;
						}
					}
					break;
				case '_': // TOC identifiers __NOTOC__, __FORCETOC__ ...
					if (parseSpecialIdentifiers()) {
						continue;
					}
					break;
				case '=': // wikipedia header ?
					if (parseSectionHeaders()) {
						continue;
					}
					break;
				case '*': // <ul> list
				case '#': // <ol> list
					if (parseLists()) {
						continue;
					}
					break;
				case ':':
					if (parseSimpleDefinitionLists()) {
						continue;
					}
					break;
				case ';':
					if (parseDefinitionLists()) {
						continue;
					}
					break;
				case '-': // parse ---- as <hr>
					if (parseHorizontalRuler()) {
						continue;
					}
					break;
				case ' ': // pre-formatted text?
				case '\t':
					if (parsePreformattedHorizontalRuler()) {
						if (!fWhiteStart) {
							fWhiteStart = true;
							fWhiteStartPosition = fCurrentPosition;
						} else {
							createContentToken(fWhiteStart, fWhiteStartPosition, 1);
						}
						continue;
					}
					break;
				}

				if (isStartOfLine() && fWikiModel.getRecursionLevel() == 1) {
					if (fWikiModel.stackSize() > 0 && (fWikiModel.peekNode() instanceof PTag) && isEmptyLine(1)) {
						createContentToken(fWhiteStart, fWhiteStartPosition, 2);
						reduceTokenStack(Configuration.HTML_PARAGRAPH_OPEN);
					} else {
						if (!isEmptyLine(1)) {
							if (fWikiModel.stackSize() == 0) {
								addParagraph();
							} else {
								TagToken tag = fWikiModel.peekNode();
								if (tag instanceof WPPreTag) {
									addParagraph();
									// } else if (tag instanceof PTag) {
									// createContentToken(fWhiteStart, fWhiteStartPosition, 2);
									// reduceTokenStack(Configuration.HTML_PARAGRAPH_OPEN);
								} else {
									String allowedParents = Configuration.HTML_PARAGRAPH_OPEN.getParents();
									if (allowedParents != null) {

										int index = -1;
										index = allowedParents.indexOf("|" + tag.getName() + "|");
										if (index >= 0) {
											addParagraph();
										}
									}
								}
							}
						}
					}
				}

				// ---------Identify the next token-------------
				switch (fCurrentCharacter) {
				case '[':
					if (parseWikiLink()) {
						continue;
					}
					break;
				case '\'':
					if (getNextChar('\'')) {
						if (getNextChar('\'')) {
							if (getNextChar('\'')) {
								if (getNextChar('\'')) {
									createContentToken(fWhiteStart, fWhiteStartPosition, 5);
									return TokenBOLDITALIC;
								}
								fCurrentPosition -= 1;
								fWhiteStart = true;
								createContentToken(fWhiteStart, fWhiteStartPosition, 3);
								return TokenBOLD;
							}
							createContentToken(fWhiteStart, fWhiteStartPosition, 3);
							return TokenBOLD;
						}
						createContentToken(fWhiteStart, fWhiteStartPosition, 2);
						return TokenITALIC;
					}
					break;
				case 'f': // ftp://
				case 'F':
					if (parseFTPLinks()) {
						continue;
					}
					break;
				case 'h': // http(s)://
				case 'H':
					if (parseHTTPLinks()) {
						continue;
					}
					break;
				case 'i': // "ISBN ..."
				case 'I':
					if (parseISBNLinks()) {
						continue;
					}
					break;
				case 'm': // mailto:
				case 'M':
					if (parseMailtoLinks()) {
						continue;
					}
					break;
				case '<':
					if (fHtmlCodes) {
						int htmlStartPosition = fCurrentPosition;
						// HTML tags are allowed
						try {
							switch (fStringSource.charAt(fCurrentPosition)) {
							case '!': // <!-- HTML comment -->
								if (parseHTMLCommentTags()) {
									continue;
								}
								break;
							default:

								if (fSource[fCurrentPosition] != '/') {
									// opening HTML tag
									WikiTagNode tagNode = parseTag(fCurrentPosition);
									if (tagNode != null) {
										String tagName = tagNode.getTagName();
										TagToken tag = fWikiModel.getTokenMap().get(tagName);
										if (tag != null) {
											tag = (TagToken) tag.clone();

											if (tag instanceof TagNode) {
												TagNode node = (TagNode) tag;
												List<NodeAttribute> attributes = tagNode.getAttributesEx();
												Attribute attr;
												for (int i = 1; i < attributes.size(); i++) {
													attr = attributes.get(i);
													node.addAttribute(attr.getName(), attr.getValue(), true);
												}
											}
											if (tag instanceof HTMLTag) {
												((HTMLTag) tag).setTemplate(isTemplate());
											}

											createContentToken(fWhiteStart, fWhiteStartPosition, 1);

											fCurrentPosition = fScannerPosition;

											String allowedParents = tag.getParents();
											if (allowedParents != null) {
												reduceTokenStack(tag);
											}
											createTag(tag, tagNode, tagNode.getEndPosition());
											return TokenIgnore;

										}
										break;
									}
								} else {
									// closing HTML tag
									WikiTagNode tagNode = parseTag(++fCurrentPosition);
									if (tagNode != null) {
										String tagName = tagNode.getTagName();
										TagToken tag = fWikiModel.getTokenMap().get(tagName);
										if (tag != null) {
											createContentToken(fWhiteStart, fWhiteStartPosition, 2);
											fCurrentPosition = fScannerPosition;

											if (fWikiModel.stackSize() > 0) {
												TagToken topToken = fWikiModel.peekNode();
												if (topToken.getName().equals(tag.getName())) {
													fWikiModel.popNode();
													return TokenIgnore;
												} else {
													if (tag.isReduceTokenStack()) {
														reduceStackUntilToken(tag);
													}
												}
											} else {
											}
											return TokenIgnore;
										}
										break;
									}
								}
							}
						} catch (IndexOutOfBoundsException e) {
							// do nothing
						}
						fCurrentPosition = htmlStartPosition;
					}
					break;
				}
				if (!fWhiteStart) {
					fWhiteStart = true;
					fWhiteStartPosition = fCurrentPosition - 1;
				}

			}
			// -----------------end switch while try--------------------
		} catch (IndexOutOfBoundsException e) {
			// end of scanner text
		}
		try {
			createContentToken(fWhiteStart, fWhiteStartPosition, 1);
		} catch (IndexOutOfBoundsException e) {
			// end of scanner text
		}
		return TokenEOF;
	}

	private void addParagraph() {
		createContentToken(fWhiteStart, fWhiteStartPosition, 2);
		reduceTokenStack(Configuration.HTML_PARAGRAPH_OPEN);
		fWikiModel.pushNode(new PTag());
	}

	private boolean parseHTMLCommentTags() {
		int htmlStartPosition = fCurrentPosition;
		String htmlCommentString = fStringSource.substring(fCurrentPosition - 1, fCurrentPosition + 3);

		if (htmlCommentString.equals("<!--")) {
			fCurrentPosition += 3;
			if (readUntil("-->")) {
				String htmlCommentContent = new String(fSource, htmlStartPosition + 3, fCurrentPosition - htmlStartPosition - 6);
				if (htmlCommentContent != null) {
					createContentToken(fWhiteStart, fWhiteStartPosition, fCurrentPosition - htmlStartPosition + 1);
					return true;
				}
			}
		}
		return false;
	}

	private boolean parseISBNLinks() {
		int urlStartPosition = fCurrentPosition;
		boolean foundISBN = false;
		try {
			String urlString = fStringSource.substring(fCurrentPosition - 1, fCurrentPosition + 4);
			if (urlString.equalsIgnoreCase("isbn ")) {
				fCurrentPosition += 4;
				fCurrentCharacter = fSource[fCurrentPosition++];

				createContentToken(fWhiteStart, fWhiteStartPosition, 6);
				fWhiteStart = false;
				foundISBN = true;
				char ch;
				ch = fSource[fCurrentPosition++];
				while ((ch >= '0' && ch <= '9') || ch == '-') {
					ch = fSource[fCurrentPosition++];
				}
			}
		} catch (IndexOutOfBoundsException e) {
			if (!foundISBN) {
				// rollback work :-)
				fCurrentPosition = urlStartPosition;
			}
		}
		if (foundISBN) {
			String urlString = new String(fSource, urlStartPosition - 1, fCurrentPosition - urlStartPosition);
			fCurrentPosition--;
			fWikiModel.appendISBNLink(urlString);
			return true;
		}
		return false;
	}

	private boolean parseFTPLinks() {
		int urlStartPosition = fCurrentPosition;
		boolean foundUrl = false;
		try {
			String urlString = fStringSource.substring(fCurrentPosition - 1, fCurrentPosition + 2);
			if (urlString.equalsIgnoreCase("ftp")) {
				fCurrentPosition += 2;
				fCurrentCharacter = fSource[fCurrentPosition++];

				if (fCurrentCharacter == ':' && fSource[fCurrentPosition++] == '/' && fSource[fCurrentPosition++] == '/') {
					createContentToken(fWhiteStart, fWhiteStartPosition, 6);
					fWhiteStart = false;
					foundUrl = true;
					while (Encoder.isUrlIdentifierPart(fSource[fCurrentPosition++])) {
					}
				}
			}
		} catch (IndexOutOfBoundsException e) {
			if (!foundUrl) {
				// rollback work :-)
				fCurrentPosition = urlStartPosition;
			}
		}
		if (foundUrl) {
			String urlString = new String(fSource, urlStartPosition - 1, fCurrentPosition - urlStartPosition);
			fCurrentPosition--;
			fWikiModel.appendExternalLink(urlString, urlString, true);
			return true;
		}
		return false;
	}

	private boolean parseHTTPLinks() {
		int urlStartPosition = fCurrentPosition;
		boolean foundUrl = false;
		try {
			int diff = 7;
			String urlString = fStringSource.substring(fCurrentPosition - 1, fCurrentPosition + 3);
			if (urlString.equalsIgnoreCase("http")) {
				fCurrentPosition += 3;
				fCurrentCharacter = fSource[fCurrentPosition++];
				if (fCurrentCharacter == 's') { // optional
					fCurrentCharacter = fSource[fCurrentPosition++];
					diff++;
				}

				if (fCurrentCharacter == ':' && fSource[fCurrentPosition++] == '/' && fSource[fCurrentPosition++] == '/') {
					createContentToken(fWhiteStart, fWhiteStartPosition, diff);
					fWhiteStart = false;
					foundUrl = true;
					while (Encoder.isUrlIdentifierPart(fSource[fCurrentPosition++])) {
					}
				}
			}
		} catch (IndexOutOfBoundsException e) {
			if (!foundUrl) {
				// rollback work :-)
				fCurrentPosition = urlStartPosition;
			}
		}
		if (foundUrl) {
			String urlString = new String(fSource, urlStartPosition - 1, fCurrentPosition - urlStartPosition);
			fCurrentPosition--;
			fWikiModel.appendExternalLink(urlString, urlString, true);
			return true;
		}
		return false;
	}

	private boolean parseMailtoLinks() {
		int urlStartPosition = fCurrentPosition;
		int tempPosition = fCurrentPosition;
		boolean foundUrl = false;
		try {
			String urlString = fStringSource.substring(fCurrentPosition - 1, fCurrentPosition + 6);
			if (urlString.equalsIgnoreCase("mailto:")) {
				tempPosition += 6;
				fCurrentCharacter = fSource[tempPosition++];

				foundUrl = true;
				while (!Character.isWhitespace(fSource[tempPosition++])) {
				}
			}
		} catch (IndexOutOfBoundsException e) {
		}
		if (foundUrl) {
			String urlString = new String(fSource, urlStartPosition - 1, tempPosition - urlStartPosition);
			String email = urlString.substring(7);
			if (EmailValidator.getInstance().isValid(email)) {
				createContentToken(fWhiteStart, fWhiteStartPosition, 1);
				fWhiteStart = false;
				fCurrentPosition = tempPosition;
				fCurrentPosition--;
				fWikiModel.appendMailtoLink(urlString, urlString, true);
				return true;
			}

		}
		// rollback work :-)
		fCurrentPosition = urlStartPosition;
		return false;
	}

	/**
	 * Parse a wiki section starting with a '[' character
	 * 
	 * @return <code>true</code> if a correct link was found
	 */
	private boolean parseWikiLink() {
		int startLinkPosition = fCurrentPosition;
		if (getNextChar('[')) {
			return parseWikiTag();
		} else {
			createContentToken(fWhiteStart, fWhiteStartPosition, 1);
			fWhiteStart = false;

			if (readUntilChar(']')) {
				String name = new String(fSource, startLinkPosition, fCurrentPosition - startLinkPosition - 1);

				// bbcode start
				if (fWikiModel.parseBBCodes() && name.length() > 0) {
					// parse start tokens like phpBB forum syntax style (bbcode)
					StringBuilder bbCode = new StringBuilder(name.length());
					char ch = name.charAt(0);
					if ('a' <= ch && ch <= 'z') {
						// first character must be a letter
						bbCode.append(ch);
						if (parsePHPBBCode(name, bbCode)) {
							return true;
						}
					}
				}
				// bbcode end

				if (handleHTTPLink(name)) {
					return true;
				}
				fCurrentPosition = startLinkPosition;
			}
		}
		return false;
	}

	/**
	 * Parse a wiki section starting with a '[[' sequence
	 * 
	 * @return <code>true</code> if a correct link was found
	 */
	private boolean parseWikiTag() {
		int startLinkPosition = fCurrentPosition;
		int endLinkPosition;
		// wikipedia link style
		createContentToken(fWhiteStart, fWhiteStartPosition, 2);

		int temp = fCurrentPosition;
		if (findWikiLinkEnd()) {
			endLinkPosition = fCurrentPosition - 2;
			String name = new String(fSource, startLinkPosition, endLinkPosition - startLinkPosition);
			// test for a suffix string behind the Wiki link. Useful for plurals.
			// Example:
			// Dolphins are [[aquatic mammal]]s that are closely related to [[whale]]s
			// and [[porpoise]]s.
			temp = fCurrentPosition;
			StringBuilder suffixBuffer = new StringBuilder();

			try {
				while (true) {
					fCurrentCharacter = fSource[fCurrentPosition++];
					if (!Character.isLowerCase(fCurrentCharacter)) {
						fCurrentPosition--;
						break;
					}
					suffixBuffer.append(fCurrentCharacter);
				}
				String suffix = suffixBuffer.toString();
				fEventListener.onWikiLink(fSource, startLinkPosition, endLinkPosition, suffix);
				fWikiModel.appendRawWikipediaLink(name, suffix);
				return true;
			} catch (IndexOutOfBoundsException e) {
				fCurrentPosition = temp;
			}

			fEventListener.onWikiLink(fSource, startLinkPosition, endLinkPosition, "");
			fWikiModel.appendRawWikipediaLink(name, "");
			return true;
		} else {
			fWhiteStart = true;
			fWhiteStartPosition = startLinkPosition - 2;
			fCurrentPosition = temp + 1;
		}
		return false;
	}

	private boolean parsePreformattedHorizontalRuler() {
		if (isStartOfLine() && !isEmptyLine(1)) {
			// if (fWikiModel.stackSize() == 0 ||
			// !fWikiModel.peekNode().equals("pre")) {
			if (fWikiModel.stackSize() == 0 || !(fWikiModel.peekNode() instanceof HTMLBlockTag)|| (fWikiModel.peekNode() instanceof PTag)) {
//					!(fWikiModel.peekNode() instanceof PreTag || fWikiModel.peekNode() instanceof WPPreTag)) {
				createContentToken(fWhiteStart, fWhiteStartPosition, 2);
				reduceTokenStack(Configuration.HTML_PRE_OPEN);

				// don't use Configuration.HTML_PRE_OPEN here
				// rendering differs between these tags!
				fWikiModel.pushNode(new WPPreTag());
			}
			return true;
		}
		return false;
	}

	/**
	 * Parse <code>----</code> as &lt;hr&gt; tag
	 * 
	 * @return
	 */
	private boolean parseHorizontalRuler() {
		if (isStartOfLine()) {
			int tempCurrPosition = fCurrentPosition;
			try {
				if (fSource[tempCurrPosition++] == '-' && fSource[tempCurrPosition++] == '-' && fSource[tempCurrPosition++] == '-') {
					int pos = isEndOfLine('-', tempCurrPosition);
					if (pos > 0) {
						HrTag hr = new HrTag();
						createContentToken(fWhiteStart, fWhiteStartPosition, 2);
						reduceTokenStack(hr);
						fCurrentPosition = pos;
						fWikiModel.append(hr);
						fWhiteStart = false;
						return true;
					}
				}
			} catch (IndexOutOfBoundsException e) {

			}
			fCurrentPosition = tempCurrPosition;
		}
		return false;
	}

	private boolean parseDefinitionLists() {
		if (isStartOfLine()) {
			createContentToken(fWhiteStart, fWhiteStartPosition, 1);

			int startHeadPosition = fCurrentPosition;
			if (readUntilEOL()) {
				TagToken dl = new DlTag();
				TagToken dt = new DtTag();
				reduceTokenStack(dl);
				String head = new String(fSource, startHeadPosition, fCurrentPosition - startHeadPosition);
				int index = head.indexOf(" : ");
				if (index > 0) {
					fWikiModel.pushNode(dl);
					fWikiModel.pushNode(dt);
					// fResultBuffer.append("<dl><dt>");
					WikipediaParser.parseRecursive(head.substring(0, index).trim(), fWikiModel, false, true);
					// fResultBuffer.append("&nbsp;</dt><dd>");
					fWikiModel.popNode();
					fWikiModel.pushNode(new DdTag());
					WikipediaParser.parseRecursive(head.substring(index + 2).trim(), fWikiModel, false, true);
					// fResultBuffer.append("\n</dd></dl>");
					fWikiModel.popNode();
					fWikiModel.popNode();
				} else {
					index = head.indexOf(":");
					if (index > 0) {
						fWikiModel.pushNode(dl);
						fWikiModel.pushNode(dt);
						// fResultBuffer.append("<dl><dt>");
						WikipediaParser.parseRecursive(head.substring(0, index).trim(), fWikiModel, false, true);
						fWikiModel.popNode();
						fWikiModel.pushNode(new DdTag());
						// fResultBuffer.append("</dt><dd>");
						WikipediaParser.parseRecursive(head.substring(index + 1).trim(), fWikiModel, false, true);
						fWikiModel.popNode();
						fWikiModel.popNode();
						// fResultBuffer.append("\n</dd></dl>");
					} else {
						fWikiModel.pushNode(dl);
						fWikiModel.pushNode(dt);
						// fResultBuffer.append("<dl><dt>");
						WikipediaParser.parseRecursive(head.trim(), fWikiModel, false, true);
						fWikiModel.popNode();
						fWikiModel.popNode();
						// fResultBuffer.append("&nbsp;</dt></dl>");
					}
				}
				return true;
			}
		}
		return false;
	}

	private boolean parseSimpleDefinitionLists() {
		if (isStartOfLine()) {
			createContentToken(fWhiteStart, fWhiteStartPosition, 1);

			int levelHeader = getNumberOfChar(':') + 1;
			int startHeadPosition = fCurrentPosition;
			if (readUntilEOL()) {
				reduceTokenStack(Configuration.HTML_DL_OPEN);
				String head = new String(fSource, startHeadPosition, fCurrentPosition - startHeadPosition);
				for (int i = 0; i < levelHeader; i++) {
					fWikiModel.pushNode(new DlTag());
					fWikiModel.pushNode(new DdTag());
				}

				WikipediaParser.parseRecursive(head.trim(), fWikiModel, false, true);

				for (int i = 0; i < levelHeader; i++) {
					// fResultBuffer.append("\n</dd></dl>");
					fWikiModel.popNode();
					fWikiModel.popNode();
				}
				return true;
			}
		}
		return false;
	}

	private boolean parseLists() {
		// set scanner pointer to '\n' character:
		if (isStartOfLine()) {
			setPosition(fCurrentPosition - 2);
			WPList list = wpList();
			if (list != null && !list.isEmpty()) {
				createContentToken(fWhiteStart, fWhiteStartPosition, 1);
				reduceTokenStack(list);
				fCurrentPosition = getPosition() - 1;
				fWikiModel.append(list);
				return true;
			}
		}
		return false;
	}

	private boolean parseSectionHeaders() {
		if (isStartOfLine()) {
			int headerStartPosition = fCurrentPosition - 1;
			int endIndex = fStringSource.indexOf("\n", fCurrentPosition);
			if (endIndex < 0) {
				endIndex = fStringSource.length();
			}
			int headerEndPosition = endIndex;
			char ch;
			while (headerEndPosition > 0) {
				ch = fSource[--headerEndPosition];
				if (!Character.isWhitespace(ch)) {
					break;
				}
			}
			if (headerEndPosition < 0 || headerEndPosition <= headerStartPosition) {
				return false;
			}
			int level = 0;
			while (headerStartPosition < headerEndPosition) {
				if (fSource[headerStartPosition] == '=' && fSource[headerEndPosition] == '=') {
					level++;
					headerStartPosition++;
					headerEndPosition--;
					// if (level == 6) {
					// headerEndPosition++;
					// break;
					// }
				} else {
					headerEndPosition++;
					break;
				}
			}
			if (level == 0) {
				return false;
			}
			if (level > 6) {
				level = 6;
			}
			createContentToken(fWhiteStart, fWhiteStartPosition, 1);
			reduceTokenStack();
			String head = "";
			if (headerEndPosition > headerStartPosition) {
				head = new String(fSource, headerStartPosition, headerEndPosition - headerStartPosition);
			}
			fEventListener.onHeader(fSource, headerStartPosition, headerEndPosition, level);
			fCurrentPosition = endIndex;

			handleHead(head, level);

			return true;
		}
		return false;
	}

	private boolean parseTable() {
		if (isStartOfLine()) {
			// wiki table ?
			setPosition(fCurrentPosition - 1);
			WPTable table = wpTable(fTableOfContentTag);
			if (table != null) {
				createContentToken(fWhiteStart, fWhiteStartPosition, 1);
				reduceTokenStack(table);
				// set pointer behind: "\n|}"
				fCurrentPosition = getPosition();
				fWikiModel.append(table);
				// table.filter(fSource, fWikiModel);
				return true;
			}
		}
		return false;
	}

	private boolean parseTemplate() {
		// dummy parsing of Wikipedia templates for event listeners
		// doesn't change fCurrentPosition
		if (fSource[fCurrentPosition] == '{') {
			int templateStartPosition = fCurrentPosition + 1;
			if (fSource[templateStartPosition] != '{') {
				int templateEndPosition = findNestedTemplateEnd(fSource, templateStartPosition);
				if (templateEndPosition > 0) {
					fEventListener.onTemplate(fSource, templateStartPosition, templateEndPosition - 2);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Parse special identifiers like __TOC__, __NOTOC__, __FORCETOC__
	 * 
	 * @return
	 */
	private boolean parseSpecialIdentifiers() {
		if (fSource[fCurrentPosition] == '_') {
			fCurrentPosition++;
			int tocEndPosition = fCurrentPosition;
			char ch;
			while (true) {
				ch = fSource[tocEndPosition++];
				if (ch >= 'A' && ch <= 'Z') {
					continue;
				}
				break;
			}
			if (ch == '_' && fSource[tocEndPosition] == '_') {
				String tocIdent = fStringSource.substring(fCurrentPosition, tocEndPosition - 1);
				boolean tocRecognized = false;
				for (int i = 0; i < TOC_IDENTIFIERS.length; i++) {
					if (TOC_IDENTIFIERS[i].equals(tocIdent)) {
						createContentToken(fWhiteStart, fWhiteStartPosition, 2);
						tocRecognized = true;
						fCurrentPosition = tocEndPosition + 1;
						switch (i) {
						case 0: // TOC
							createTableOfContent(true);
							fForceToC = true;
							break;
						case 1: // NOTOC
							setNoToC(true);
							break;
						case 2: // FORCETOC
							fForceToC = true;
							break;
						}
						break;
					}
				}
				if (tocRecognized) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if the scanners cursor position is at the start of a line
	 * 
	 * @return
	 */
	private boolean isStartOfLine() {
		if (fCurrentPosition >= 2) {
			char beforeChar = fSource[fCurrentPosition - 2];
			// if (beforeChar == '\n' || beforeChar == '\r') {
			if (beforeChar == '\n') {
				return true;
			}
			if (beforeChar == '*' && fCurrentPosition == 2) {
				return true;
			}
		} else {
			if (fCurrentPosition == 1) {
				return true;
			}
		}
		return false;
	}

	private int isEndOfLine(char testChar, int currentPosition) {
		int tempPosition = currentPosition;
		try {
			char ch;
			while (true) {
				ch = fSource[tempPosition];
				if (ch != testChar) {
					break;
				}
				tempPosition++;
			}
			while (true) {
				ch = fSource[tempPosition++];
				if (ch == '\n') {
					return tempPosition;
				} else if (!Character.isWhitespace(ch)) {
					return -1;
				}
			}
		} catch (IndexOutOfBoundsException e) {

		}
		return -1;
	}

	private void createTag(TagToken tag, WikiTagNode tagNode, int startMacroPosition) {
		String endTag;
		String macroBodyString = "";
		int index0;
		String command = tagNode.getTagName();
		if ((tag != null) && (tag instanceof IBodyTag) && (!tagNode.isEmptyXmlTag())) {
			endTag = command + '>';
			index0 = Util.indexOfIgnoreCase(fStringSource, "</", endTag, startMacroPosition);

			if (index0 >= 0) {
				macroBodyString = fStringSource.substring(startMacroPosition, index0);
				fCurrentPosition = index0 + endTag.length() + 2;
			} else {
				macroBodyString = new String(fSource, startMacroPosition, fSource.length - startMacroPosition);
				fCurrentPosition = fSource.length;
			}
		} else {
			macroBodyString = null;
			fCurrentPosition = startMacroPosition;
		}

		handleTag(tag, tagNode, macroBodyString);
	}

	private boolean handleHTTPLink(String name) {
		String urlString;
		if (name != null) {
			boolean isEmail = false;
			urlString = name.trim();
			String email;
			String lowerCaseName = urlString.toLowerCase();
			boolean prefixCheck = lowerCaseName.startsWith("http://");
			if (!prefixCheck) {
				prefixCheck = lowerCaseName.startsWith("https://");
			}
			if (!prefixCheck) {
				prefixCheck = lowerCaseName.startsWith("ftp://");
			}
			if (!prefixCheck) {
				prefixCheck = lowerCaseName.startsWith("mailto:");
				isEmail = true;
			}
			if (prefixCheck) {
				// Wikipedia link style: name separated by space?
				int pipeIndex = urlString.indexOf(' ');
				String alias = "";
				if (pipeIndex != (-1)) {
					alias = urlString.substring(pipeIndex + 1);
					urlString = urlString.substring(0, pipeIndex);
				} else {
					alias = urlString;
				}

				if (isEmail) {
					if (pipeIndex > 7) {
						email = urlString.substring(7, pipeIndex);
					} else {
						email = urlString.substring(7);
					}
					if (EmailValidator.getInstance().isValid(email)) {
						fWikiModel.appendMailtoLink(urlString, alias, false);
						return true;
					}
				} else {
					fWikiModel.appendExternalLink(urlString, alias, false);
					return true;
				}

			}
		}
		return false;
	}

	// private void handleWikipediaLink(String linkText, String suffix) {
	// String name = linkText;
	// if (name != null) {
	// // trim the name for whitespace characters on the left side
	// int trimLeftIndex = 0;
	// while ((trimLeftIndex < name.length()) && (name.charAt(trimLeftIndex) <= '
	// ')) {
	// trimLeftIndex++;
	// }
	// if (trimLeftIndex > 0) {
	// name = name.substring(trimLeftIndex);
	// }
	// // Is there an alias like [alias|link] ?
	// int pipeIndex = name.lastIndexOf('|');
	// String alias = "";
	// if (-1 != pipeIndex) {
	// alias = name.substring(pipeIndex + 1);
	// name = name.substring(0, pipeIndex);
	// if (alias.length() == 0) {
	// // special cases like: [[Test:hello world|]] or [[Test(hello
	// // world)|]]
	// // or [[Test, hello world|]]
	// alias = name;
	// int index = alias.indexOf(':');
	// if (index != -1) {
	// alias = alias.substring(index + 1).trim();
	// } else {
	// index = alias.indexOf('(');
	// if (index != -1) {
	// alias = alias.substring(0, index).trim();
	// } else {
	// index = alias.indexOf(',');
	// if (index != -1) {
	// alias = alias.substring(0, index).trim();
	// }
	// }
	// }
	// }
	// }
	//
	// int hashIndex = name.lastIndexOf('#');
	//
	// String hash = "";
	// if (-1 != hashIndex && hashIndex != name.length() - 1) {
	// hash = name.substring(hashIndex + 1);
	// name = name.substring(0, hashIndex);
	// }
	//
	// // trim the name for whitespace characters on the right side
	// int trimRightIndex = name.length() - 1;
	// while ((trimRightIndex >= 0) && (name.charAt(trimRightIndex) <= ' ')) {
	// trimRightIndex--;
	// }
	// if (trimRightIndex != name.length() - 1) {
	// name = name.substring(0, trimRightIndex + 1);
	// }
	//
	// name = Encoder.encodeHtml(name);
	// String view;
	// if (-1 != pipeIndex) {
	// view = alias + suffix;
	// } else {
	// if (name.length() > 0 && name.charAt(0) == ':') {
	// view = name.substring(1) + suffix;
	// } else {
	// view = name + suffix;
	// }
	// }
	//
	// if (handleNamespaceLinks(name, view, pipeIndex)) {
	// return;
	// }
	//
	// int indx = name.indexOf(':');
	// String namespace = null;
	// if (indx >= 0) {
	// namespace = name.substring(0, indx);
	// }
	// if (namespace != null && fWikiModel.isImageNamespace(namespace)) {
	// fWikiModel.parseInternalImageLink(namespace, linkText);
	// return;
	// } else {
	// if (name.length() > 0 && name.charAt(0) == ':') {
	// name = name.substring(1);
	// }
	// if (name.length() > 0 && name.charAt(0) == ':') {
	// name = name.substring(1);
	// }
	// fWikiModel.addLink(name);
	// if (-1 != hashIndex) {
	// fWikiModel.appendInternalLink(name, hash, view);
	// } else {
	// fWikiModel.appendInternalLink(name, null, view);
	// }
	// }
	// }
	// }

	/**
	 * @param name
	 * @param view
	 */
	// private boolean handleNamespaceLinks(String name, String view, int
	// pipeIndex) {
	// int colonIndex = name.indexOf(':');
	//
	// if (colonIndex != (-1)) {
	// String nameSpace = name.substring(0, colonIndex);
	//
	// if (fWikiModel.isSemanticWebActive() && (name.length() > colonIndex + 1)) {
	// // See <a
	// // href="http://en.wikipedia.org/wiki/Semantic_MediaWiki">Semantic
	// // MediaWiki</a> for more information.
	// if (name.charAt(colonIndex + 1) == ':') {
	// // found an SMW relation
	// String relationValue = name.substring(colonIndex + 2);
	//
	// if (fWikiModel.addSemanticRelation(nameSpace, relationValue)) {
	// if ((-1) == pipeIndex) {
	// view = relationValue;
	// }
	// if (view.trim().length() > 0) {
	// fWikiModel.appendInternalLink(relationValue, null, view);
	// }
	// return true;
	// }
	// } else if (name.charAt(colonIndex + 1) == '=') {
	// // found an SMW attribute
	// String attributeValue = name.substring(colonIndex + 2);
	// if (fWikiModel.addSemanticAttribute(nameSpace, attributeValue)) {
	// fWikiModel.append(new ContentToken(attributeValue));
	// return true;
	// }
	// }
	//
	// }
	// if (fWikiModel.isCategoryNamespace(nameSpace)) {
	// // add the category to this texts metadata
	// String category = name.substring(colonIndex + 1);
	// if (category != null && category.length() > 0) {
	// fWikiModel.addCategory(category, "");
	// return true;
	// }
	// } else if (fWikiModel.isInterWiki(nameSpace)) {
	// String title = name.substring(colonIndex + 1);
	// if (title != null && title.length() > 0) {
	// fWikiModel.appendInterWikiLink(nameSpace, title, view);
	// return true;
	// }
	// }
	// }
	// return false;
	// }
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
	 * handle head for table of content
	 * 
	 * @param rawHead
	 * @param headLevel
	 */
	private void handleHead(String rawHead, int headLevel) {
		if (rawHead != null) {
			TagStack localStack = parseRecursive(rawHead.trim(), fWikiModel, true, true);

			WPTag headTagNode = new WPTag("h" + headLevel);
			headTagNode.addChildren(localStack.getNodeList());
			String tocHead = headTagNode.getBodyString();
			String anchor = Encoder.encodeUrl(tocHead);
			createTableOfContent(false);
			if (!fNoToC && (++fHeadCounter) > 3) {
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
			if (fWikiModel.getRecursionLevel() == 1) {
				fWikiModel.buildEditLinkUrl(fSectionCounter++);
			}
			TagNode aTagNode = new TagNode("a");
			aTagNode.addAttribute("name", anchor, true);
			aTagNode.addAttribute("id", anchor, true);
			fWikiModel.append(aTagNode);

			fWikiModel.append(headTagNode);

		}
	}

	/**
	 * 
	 * @param isTOCIdentifier
	 *          <code>true</code> if the __TOC__ keyword was parsed
	 */
	private void createTableOfContent(boolean isTOCIdentifier) {
		fTableOfContentTag = fWikiModel.getTableOfContentTag(isTOCIdentifier);
		if (fTableOfContentTag != null) {
			if (fTableOfContent == null) {
				fTableOfContent = fTableOfContentTag.getTableOfContent();
			}
		}
		fToCSet = new HashSet<String>();
	}

	private void handleTag(TagToken tag, WikiTagNode tagNode, String bodyString) {
		String command = tagNode.getTagName();
		try {
			if (tag instanceof EndTagToken) {
				fWikiModel.append(tag);
			} else {
				fWikiModel.pushNode(tag);
				if (null != bodyString) {
					if (tag instanceof INoBodyParsingTag) {
						((TagNode) tag).addChild(new ContentToken(bodyString));
					} else {
						// recursively filter tags within the tags body string
						WikipediaParser.parseRecursive(bodyString.trim(), fWikiModel, false, true);
					}
				}
				if (tag instanceof IBodyTag) {
					fWikiModel.popNode();
				}
			}
		} catch (IllegalArgumentException e) {
			TagNode divTagNode = new TagNode("div");
			divTagNode.addAttribute("class", "error", true);
			divTagNode.addChild(new ContentToken("IllegalArgumentException: " + command + " - " + e.getMessage()));
			fWikiModel.append(divTagNode);
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
			TagNode divTagNode = new TagNode("div");
			divTagNode.addAttribute("class", "error", true);
			divTagNode.addChild(new ContentToken(command + ": " + e.getMessage()));
			fWikiModel.append(divTagNode);
			e.printStackTrace();
			return;
		}
	}

	public void runParser() {
		int token = TokenSTART;
		while ((token = getNextToken()) != TokenEOF) {
			switch (token) {
			case TokenBOLDITALIC:
				if (fWikiModel.stackSize() > 0 && fWikiModel.peekNode().equals(BOLDITALIC)) {
					fWikiModel.popNode();
					// fResultBuffer.append("</i></b>");
				} else if (fWikiModel.stackSize() > 1 && fWikiModel.peekNode().equals(BOLD)
						&& fWikiModel.getNode(fWikiModel.stackSize() - 2).equals(ITALIC)) {
					fWikiModel.popNode();
					fWikiModel.popNode();
					// fResultBuffer.append("</b></i>");
				} else if (fWikiModel.stackSize() > 1 && fWikiModel.peekNode().equals(ITALIC)
						&& fWikiModel.getNode(fWikiModel.stackSize() - 2).equals(BOLD)) {
					fWikiModel.popNode();
					fWikiModel.popNode();
					// fResultBuffer.append("</i></b>");
				} else if (fWikiModel.stackSize() > 0 && fWikiModel.peekNode().equals(BOLD)) {
					fWikiModel.popNode();
					fWikiModel.pushNode(new WPTag("i"));
				} else if (fWikiModel.stackSize() > 0 && fWikiModel.peekNode().equals(ITALIC)) {
					fWikiModel.popNode();
					fWikiModel.pushNode(new WPTag("b"));
				} else {
					fWikiModel.pushNode(new WPBoldItalicTag());
					// fResultBuffer.append("<b><i>");
				}
				break;
			case TokenBOLD:
				if (fWikiModel.stackSize() > 0 && fWikiModel.peekNode().equals(BOLDITALIC)) {
					fWikiModel.popNode();
					fWikiModel.pushNode(new WPTag("i"));
					// fResultBuffer.append("</b>");
				} else if (fWikiModel.stackSize() > 0 && fWikiModel.peekNode().equals(BOLD)) {
					fWikiModel.popNode();
					// fResultBuffer.append("</b>");
				} else {
					fWikiModel.pushNode(new WPTag("b"));
					// fResultBuffer.append("<b>");
				}
				break;
			case TokenITALIC:
				if (fWikiModel.stackSize() > 0 && fWikiModel.peekNode().equals(BOLDITALIC)) {
					fWikiModel.popNode();
					fWikiModel.pushNode(new WPTag("b"));
					// fResultBuffer.append("</i>");
				} else if (fWikiModel.stackSize() > 0 && fWikiModel.peekNode().equals(ITALIC)) {
					fWikiModel.popNode();
					// fResultBuffer.append("</i>");
				} else {
					fWikiModel.pushNode(new WPTag("i"));
					// fResultBuffer.append("<i>");
				}
				break;
			}
		}
		reduceTokenStack();

		if (!fNoToC && fTableOfContentTag != null) {
			if (fHeadCounter > 3 || fForceToC) {
				fTableOfContentTag.setShowToC(true);
			}
		}

	}

	/**
	 * Reduce the current token stack completely
	 */
	private void reduceTokenStack() {
		if (fWikiModel.stackSize() == 0) {
			return;
		}
		while (fWikiModel.stackSize() > 0) {
			fWikiModel.popNode();
		}
	}

	/**
	 * Reduce the current token stack until an allowed parent is at the top of the
	 * stack
	 */
	private void reduceTokenStack(TagToken node) {
		String allowedParents = node.getParents();
		if (allowedParents != null) {
			TagToken tag;
			int index = -1;

			while (fWikiModel.stackSize() > 0) {
				tag = fWikiModel.peekNode();
				index = allowedParents.indexOf("|" + tag.getName() + "|");
				if (index < 0) {
					fWikiModel.popNode();
					if (tag.getName().equals(node.getName())) {
						// for wrong nested HTML tags like <table> <tr><td>number
						// 1<tr><td>number 2</table>
						break;
					}
				} else {
					break;
				}
			}
		} else {
			while (fWikiModel.stackSize() > 0) {
				fWikiModel.popNode();
			}
		}
	}

	/**
	 * Reduce the current token stack until the given nodes name is at the top of
	 * the stack. Useful for closing HTML tags.
	 */
	private void reduceStackUntilToken(TagToken node) {
		TagToken tag;
		int index = -1;
		String allowedParents = node.getParents();
		while (fWikiModel.stackSize() > 0) {
			tag = fWikiModel.peekNode();
			if (node.getName().equals(tag.getName())) {
				fWikiModel.popNode();
				break;
			}
			if (allowedParents == null) {
				fWikiModel.popNode();
			} else {
				index = allowedParents.indexOf("|" + tag.getName() + "|");
				if (index < 0) {
					fWikiModel.popNode();
				} else {
					break;
				}
			}
		}
	}

	/**
	 * count the number of wiki headers in this document
	 * 
	 * @param toc
	 * @return
	 */
	// private int isToC(List<Object> toc) {
	// if (toc == null) {
	// return 0;
	// }
	// if (toc.size() == 1 && (toc.get(0) instanceof List)) {
	// return isToC((List<Object>) toc.get(0));
	// }
	// int result = 0;
	// for (int i = 0; i < toc.size(); i++) {
	// if (toc.get(i) instanceof List) {
	// result += isToC((List<Object>) toc.get(i));
	// } else {
	// result++;
	// }
	// }
	// return result;
	// }
	public boolean isNoToC() {
		return fNoToC;
	}

	public void setNoToC(boolean noToC) {
		fNoToC = noToC;
	}

	/**
	 * Call the parser on the first recursion level, where the text can contain a
	 * table of contents (TOC).
	 * 
	 * <br/><br/><b>Note:</b> in this level the wiki model will call the
	 * <code>setUp()</code> method before parsing and the
	 * <code>tearDown()</code> method after the parser has finished.
	 * 
	 * @param rawWikitext
	 *          the raw text of the article
	 * @param wikiModel
	 *          a suitable wiki model for the given wiki article text
	 * @param parseTemplates
	 *          parse the template expansion step
	 * @param templateParserBuffer
	 *          if the <code>templateParserBuffer != null</code> the
	 *          <code>templateParserBuffer</code> will be used to append the
	 *          result of the template expansion step
	 * 
	 */
	public static void parse(String rawWikiText, IWikiModel wikiModel, boolean parseTemplates, Appendable templateParserBuffer) {
		try {
			// initialize the wiki model
			wikiModel.setUp();

			Appendable buf;
			if (templateParserBuffer != null) {
				buf = templateParserBuffer;
			} else {
				buf = new StringBuilder(rawWikiText.length() + rawWikiText.length() / 10);
			}
			if (parseTemplates) {
				String pass1Text = null;
				try {
					TemplateParser.parse(rawWikiText, wikiModel, buf, wikiModel.isTemplateTopic());
					pass1Text = buf.toString();
				} catch (Exception ioe) {
					ioe.printStackTrace();
					pass1Text = "<span class=\"error\">TemplateParser exception: " + ioe.getClass().getSimpleName() + "</span>";
				}
				if (parseRedirect(pass1Text, wikiModel) == null) {
					parseRecursive(pass1Text, wikiModel, false, false);
				}
			} else {
				if (parseRedirect(rawWikiText, wikiModel) == null) {
					parseRecursive(rawWikiText, wikiModel, false, false);
				}
			}
		} finally {
			// clean up wiki model if necessary
			wikiModel.tearDown();
		}
	}

	/**
	 * Check the text for a <code>#REDIRECT [[...]]</code> or
	 * <code>#redirect [[...]]</code> link
	 * 
	 * @param rawWikiText
	 *          the wiki text
	 * @param wikiModel
	 * @return <code>null</code> if a redirect was found and further parsing
	 *         should be canceled according to the model.
	 */
	public static String parseRedirect(String rawWikiText, IWikiModel wikiModel) {
		int redirectStart = -1;
		int redirectEnd = -1;
		for (int i = 0; i < rawWikiText.length(); i++) {
			if (rawWikiText.charAt(i) == '#') {
				boolean isRedirect = rawWikiText.startsWith("redirect", i + 1);
				if (!isRedirect) {
					isRedirect = rawWikiText.startsWith("REDIRECT", i + 1);
				}
				if (isRedirect) {
					redirectStart = rawWikiText.indexOf("[[", i + 8);
					if (redirectStart > i + 8) {
						redirectStart += 2;
						redirectEnd = rawWikiText.indexOf("]]", redirectStart);
					}
				}
				break;
			}
			if (Character.isWhitespace(rawWikiText.charAt(i))) {
				continue;
			}
			break;
		}

		if (redirectEnd >= 0) {
			String redirectedLink = rawWikiText.substring(redirectStart, redirectEnd);
			if (wikiModel.appendRedirectLink(redirectedLink)) {
				return redirectedLink;
			}
		}
		return null;
	}

	/**
	 * Call the parser on the subsequent recursion levels, where the subtexts (of
	 * templates, table cells, list items or image captions) don't contain a table
	 * of contents (TOC)
	 * 
	 * <b>Note:</b> the wiki model doesn't call the <code>setUp()</code> or
	 * <code>tearDown()</code> methods for the subsequent recursive parser
	 * steps.
	 * 
	 * @param rawWikitext
	 * @param wikiModel
	 * @return
	 */
	public static void parseRecursive(String rawWikitext, IWikiModel wikiModel) {
		parseRecursive(rawWikitext, wikiModel, false, true);
	}

	/**
	 * Call the parser on the subsequent recursion levels, where the subtexts (of
	 * templates, table cells, list items or image captions) don't contain a table
	 * of contents (TOC)
	 * 
	 * <b>Note:</b> the wiki model doesn't call the <code>setUp()</code> or
	 * <code>tearDown()</code> methods for the subsequent recursive parser
	 * steps.
	 * 
	 * @param rawWikitext
	 * @param wikiModel
	 * @param noTOC
	 * @param appendStack
	 * @return
	 * @return
	 */
	public static TagStack parseRecursive(String rawWikitext, IWikiModel wikiModel, boolean createOnlyLocalStack, boolean noTOC) {
		AbstractParser parser = wikiModel.createNewInstance(rawWikitext);
		return parser.parseRecursiveInternal(wikiModel, createOnlyLocalStack, noTOC);
	}

	public boolean isTemplate() {
		return fRenderTemplate;
	}

}