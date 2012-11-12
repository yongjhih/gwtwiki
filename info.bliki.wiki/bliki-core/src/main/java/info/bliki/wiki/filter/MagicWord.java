/**
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, version 2.1, dated February 1999.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the latest version of the GNU Lesser General
 * Public License as published by the Free Software Foundation;
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program (LICENSE.txt); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package info.bliki.wiki.filter;

import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.namespaces.INamespace;
import info.bliki.wiki.namespaces.INamespace.INamespaceValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * See <a href="http://www.mediawiki.org/wiki/Help:Magic_words">Help:Magic
 * words</a> for a list of Mediawiki magic words.
 */
public class MagicWord {

	/**
	 * Type of storing user contributions in the DB.
	 */
	public enum MagicWordE {

		// current date values
		MAGIC_CURRENT_DAY("CURRENTDAY"),

		MAGIC_CURRENT_DAY2("CURRENTDAY2"),

		MAGIC_CURRENT_DAY_NAME("CURRENTDAYNAME"),

		MAGIC_CURRENT_DAY_OF_WEEK("CURRENTDOW"),

		MAGIC_CURRENT_MONTH("CURRENTMONTH"),

		MAGIC_CURRENT_MONTH_ABBR("CURRENTMONTHABBREV"),

		MAGIC_CURRENT_MONTH_NAME("CURRENTMONTHNAME"),

		MAGIC_CURRENT_TIME("CURRENTTIME"),

		MAGIC_CURRENT_HOUR("CURRENTHOUR"),

		MAGIC_CURRENT_WEEK("CURRENTWEEK"),

		MAGIC_CURRENT_YEAR("CURRENTYEAR"),

		MAGIC_CURRENT_TIMESTAMP("CURRENTTIMESTAMP"),

		// local date values
		MAGIC_LOCAL_DAY("LOCALDAY"),

		MAGIC_LOCAL_DAY2("LOCALDAY2"),

		MAGIC_LOCAL_DAY_NAME("LOCALDAYNAME"),

		MAGIC_LOCAL_DAY_OF_WEEK("LOCALDOW"),

		MAGIC_LOCAL_MONTH("LOCALMONTH"),

		MAGIC_LOCAL_MONTH_ABBR("LOCALMONTHABBREV"),

		MAGIC_LOCAL_MONTH_NAME("LOCALMONTHNAME"),

		MAGIC_LOCAL_TIME("LOCALTIME"),

		MAGIC_LOCAL_HOUR("LOCALHOUR"),

		MAGIC_LOCAL_WEEK("LOCALWEEK"),

		MAGIC_LOCAL_YEAR("LOCALYEAR"),

		MAGIC_LOCAL_TIMESTAMP("LOCALTIMESTAMP"),

		// statistics
		MAGIC_CURRENT_VERSION("CURRENTVERSION"),

		MAGIC_NUMBER_ARTICLES("NUMBEROFARTICLES"),

		MAGIC_NUMBER_PAGES("NUMBEROFPAGES"),

		MAGIC_NUMBER_FILES("NUMBEROFFILES"),

		MAGIC_NUMBER_USERS("NUMBEROFUSERS"),

		MAGIC_NUMBER_ADMINS("NUMBEROFADMINS"),

		MAGIC_PAGES_IN_CATEGORY("PAGESINCATEGORY"),

		MAGIC_PAGES_IN_CAT("PAGESINCAT"),

		MAGIC_PAGES_IN_NAMESPACE("PAGESINNAMESPACE"),

		MAGIC_PAGES_IN_NAMESPACE_NS("PAGESINNS"),

		MAGIC_PAGE_SIZE("PAGESIZE"),

		// page values
		MAGIC_PAGE_NAME("PAGENAME"),

		MAGIC_PAGE_NAME_E("PAGENAMEE"),

		MAGIC_SUB_PAGE_NAME("SUBPAGENAME"),

		MAGIC_SUB_PAGE_NAME_E("SUBPAGENAMEE"),

		MAGIC_BASE_PAGE_NAME("BASEPAGENAME"),

		MAGIC_BASE_PAGE_NAME_E("BASEPAGENAMEE"),

		MAGIC_NAMESPACE("NAMESPACE"),

		MAGIC_NAMESPACE_E("NAMESPACEE"),

		MAGIC_FULL_PAGE_NAME("FULLPAGENAME"),

		MAGIC_FULL_PAGE_NAME_E("FULLPAGENAMEE"),

		MAGIC_TALK_SPACE("TALKSPACE"),

		MAGIC_TALK_SPACE_E("TALKSPACEE"),

		MAGIC_SUBJECT_SPACE("SUBJECTSPACE"),

		MAGIC_SUBJECT_SPACE_E("SUBJECTSPACEE"),

		MAGIC_ARTICLE_SPACE("ARTICLESPACE"),

		MAGIC_ARTICLE_SPACE_E("ARTICLESPACEE"),

		MAGIC_TALK_PAGE_NAME("TALKPAGENAME"),

		MAGIC_TALK_PAGE_NAME_E("TALKPAGENAMEE"),

		MAGIC_SUBJECT_PAGE_NAME("SUBJECTPAGENAME"),

		MAGIC_SUBJECT_PAGE_NAME_E("SUBJECTPAGENAMEE"),

		MAGIC_ARTICLE_PAGE_NAME("ARTICLEPAGENAME"),

		MAGIC_ARTICLE_PAGE_NAME_E("ARTICLEPAGENAMEE"),

		MAGIC_REVISION_ID("REVISIONID"),

		MAGIC_REVISION_DAY("REVISIONDAY"),

		MAGIC_REVISION_DAY2("REVISIONDAY2"),

		MAGIC_REVISION_MONTH("REVISIONMONTH"),

		MAGIC_REVISION_MONTH1("REVISIONMONTH1"),

		MAGIC_REVISION_YEAR("REVISIONYEAR"),

		MAGIC_REVISION_TIMESTAMP("REVISIONTIMESTAMP"),

		MAGIC_REVISION_USER("REVISIONUSER"),

		MAGIC_PROTECTION_LEVEL("PROTECTIONLEVEL"),

		MAGIC_DISPLAY_TITLE("DISPLAYTITLE"),

		MAGIC_DEFAULT_SORT("DEFAULTSORT"),

		MAGIC_DEFAULT_SORT_KEY("DEFAULTSORTKEY"),

		MAGIC_DEFAULT_CATEGORY_SORT("DEFAULTCATEGORYSORT"),

		MAGIC_SITE_NAME("SITENAME"),

		MAGIC_SERVER("SERVER"),

		MAGIC_SCRIPT_PATH("SCRIPTPATH"),

		MAGIC_SERVER_NAME("SERVERNAME"),

		MAGIC_STYLE_PATH("STYLEPATH"),

		MAGIC_CONTENT_LANGUAGE("CONTENTLANGUAGE"),

		MAGIC_CONTENT_LANG("CONTENTLANG");

		private final String text;

		MagicWordE(String text) {
			this.text = text;
			MAGIC_WORDS.put(text.toLowerCase(), this);
		}

		/**
		 * Converts the enum to text.
		 */
		@Override
		public String toString() {
			return this.text;
		}

		/**
		 * Tries to convert a text to the according enum value.
		 * 
		 * @param text the text to convert
		 * 
		 * @return the according enum value
		 */
		public static MagicWordE fromString(String text) {
			if (text == null) {
				return null;
			}
			return MAGIC_WORDS.get(text.toLowerCase());
		}
	}
	/**
	 * Magic words in lower case.
	 * 
	 * Note: MediaWiki tolerates some variations in the case but they do not
	 * seem consistent, e.g.
	 * <code>{{CURRENTYEAR}} {{currentyear}} {{Currentyear}}</code>
	 * return the current year,
	 * <code>{{CURRENTYeAR}} {{cURRENTYEAR}}</code> don't.
	 * The following variations of {{SERVERNAME}} however all return the server name:
	 * <code>{{SERVERNAME}} {{SERVeRNAMe}} {{sERVERNAME}} {{servername}} {{Servername}}</code>
	 * 
	 * Therefore, tolerate any case here and use lower-case in this hashmap.
	 */
	protected final static HashMap<String, MagicWordE> MAGIC_WORDS = new HashMap<String, MagicWord.MagicWordE>(100);

	protected static final String TEMPLATE_INCLUSION = "template-inclusion";

	/**
	 * Determine if a template name corresponds to a magic word requiring special
	 * handling. See <a
	 * href="http://www.mediawiki.org/wiki/Help:Magic_words">Help:Magic words</a>
	 * for a list of Mediawiki magic words.
	 * 
	 * @param name
	 *            the potential magic word
	 * @return <tt>true</tt> if <tt>name</tt> was a magic word,
	 *         <tt>false</tt> otherwise
	 */
	public static boolean isMagicWord(String name) {
		return MAGIC_WORDS.containsKey(name.toLowerCase());
	}

	/**
	 * Determine if a template name corresponds to a magic word requiring
	 * special handling. See <a
	 * href="http://www.mediawiki.org/wiki/Help:Magic_words">Help:Magic words</a>
	 * for a list of Mediawiki magic words.
	 * 
	 * @param name
	 *            the potential magic word
	 * @return if <tt>name</tt> was a magic word: the corresponding
	 *         {@link MagicWordE}, otherwise <tt>null</tt>
	 */
	public static MagicWordE getMagicWord(String name) {
		return MagicWordE.fromString(name);
	}

	/**
	 * Process a magic word, returning the value corresponding to the magic word
	 * value. See <a
	 * href="http://www.mediawiki.org/wiki/Help:Magic_words">Help:Magic words</a>
	 * for a list of Mediawiki magic words.
	 * 
	 * @param magicWord
	 *            the magic word to process
	 * @param parameter
	 *            the parameters supplied to the magic word
	 * @param model
	 *            the wiki model to use while rendering
	 * 
	 * @return the processed magic word content or its name if unprocessed
	 */
	public static String processMagicWord(MagicWordE magicWord, String parameter, IWikiModel model) {
		assert(magicWord != null);
		SimpleDateFormat formatter = model.getSimpleDateFormat();
		// TODO: assume this is non-null!
		Date current = model.getCurrentTimeStamp();
		if (current == null) {
			// set a default value
			current = new Date(System.currentTimeMillis());
		}
		// local date values
		switch (magicWord) {
			case MAGIC_LOCAL_DAY:
				formatter.applyPattern("d");
				return formatter.format(current);
			case MAGIC_LOCAL_DAY2:
				formatter.applyPattern("dd");
				return formatter.format(current);
			case MAGIC_LOCAL_DAY_NAME:
				formatter.applyPattern("EEEE");
				return formatter.format(current);
			case MAGIC_LOCAL_DAY_OF_WEEK:
				formatter.applyPattern("F");
				return formatter.format(current);
			case MAGIC_LOCAL_MONTH:
				formatter.applyPattern("MM");
				return formatter.format(current);
			case MAGIC_LOCAL_MONTH_ABBR:
				formatter.applyPattern("MMM");
				return formatter.format(current);
			case MAGIC_LOCAL_MONTH_NAME:
				formatter.applyPattern("MMMM");
				return formatter.format(current);
			case MAGIC_LOCAL_TIME:
				formatter.applyPattern("HH:mm");
				return formatter.format(current);
			case MAGIC_LOCAL_HOUR:
				formatter.applyPattern("HH");
				return formatter.format(current);
			case MAGIC_LOCAL_WEEK:
				formatter.applyPattern("w");
				return formatter.format(current);
			case MAGIC_LOCAL_YEAR:
				formatter.applyPattern("yyyy");
				return formatter.format(current);
			case MAGIC_LOCAL_TIMESTAMP:
				formatter.applyPattern("yyyyMMddHHmmss");
				return formatter.format(current);
				// current date values

			case MAGIC_CURRENT_DAY:
				formatter.applyPattern("d");
				return formatter.format(current);
			case MAGIC_CURRENT_DAY2:
				formatter.applyPattern("dd");
				return formatter.format(current);
			case MAGIC_CURRENT_DAY_NAME:
				formatter.applyPattern("EEEE");
				return formatter.format(current);
			case MAGIC_CURRENT_DAY_OF_WEEK:
				formatter.applyPattern("F");
				return formatter.format(current);
			case MAGIC_CURRENT_MONTH:
				formatter.applyPattern("MM");
				return formatter.format(current);
			case MAGIC_CURRENT_MONTH_ABBR:
				formatter.applyPattern("MMM");
				return formatter.format(current);
			case MAGIC_CURRENT_MONTH_NAME:
				formatter.applyPattern("MMMM");
				return formatter.format(current);
			case MAGIC_CURRENT_TIME:
				formatter.applyPattern("HH:mm");
				return formatter.format(current);
			case MAGIC_CURRENT_HOUR:
				formatter.applyPattern("HH");
				return formatter.format(current);
			case MAGIC_CURRENT_WEEK:
				formatter.applyPattern("w");
				return formatter.format(current);
			case MAGIC_CURRENT_YEAR:
				formatter.applyPattern("yyyy");
				return formatter.format(current);
			case MAGIC_CURRENT_TIMESTAMP:
				formatter.applyPattern("yyyyMMddHHmmss");
				return formatter.format(current);

			case MAGIC_PAGE_NAME:
				if (parameter.length() > 0) {
					return parameter;
				} else {
					String temp = model.getPageName();
					if (temp != null) {
						return temp;
					}
				}
				break;
			case MAGIC_NAMESPACE:
				if (parameter.length() > 0) {
					int indx = parameter.indexOf(':');
					if (indx >= 0) {
						String subStr = parameter.substring(0, indx);
						INamespaceValue namespace = model.getNamespace().getNamespace(subStr);
						if (namespace != null) {
							return namespace.getPrimaryText();
						}
					}
					return "";
				} else {
					String temp = model.getNamespaceName();
					if (temp != null) {
						return temp;
					}
				}
				break;
			case MAGIC_FULL_PAGE_NAME:
				if (parameter.length() > 0) {
					return parameter;
				} else {
					String temp = model.getPageName();
					if (temp != null) {
						return temp;
					}
				}
				break;
			case MAGIC_TALK_PAGE_NAME:
				if (true) { // block to hide local variables from other cases
					String pageName;
					INamespaceValue talkspace = null;
					INamespace ns = model.getNamespace();
					if (parameter.length() > 0) {
						pageName = parameter;
						int index = pageName.indexOf(':');
						// assume main namespace for now:
						talkspace = ns.getMain().getTalkspace();
						if (index > 0) {
							// {{TALKPAGENAME:Template:Sandbox}}
							INamespaceValue namespace = ns.getNamespace(pageName.substring(0, index));
							if (namespace != null) {
								pageName = pageName.substring(index + 1);
								talkspace = namespace.getTalkspace();
							}
						}
					} else {
						pageName = model.getPageName();
						talkspace = ns.getTalkspace(model.getNamespaceName());
					}
					if (pageName != null) {
						if (talkspace != null) {
							return talkspace.getPrimaryText() + ":" + pageName;
						} else {
							// if there is no talkspace, MediaWiki returns an empty string
							return "";
						}
					}
				}
				break;
			default:
				break;
		}

		return magicWord.toString();
	}
}
