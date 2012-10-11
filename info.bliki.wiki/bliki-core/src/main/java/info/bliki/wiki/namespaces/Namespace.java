package info.bliki.wiki.namespaces;

import info.bliki.Messages;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Mediawiki Namespaces. See <a
 * href="http://www.mediawiki.org/wiki/Manual:Namespace#Built-in_namespaces"
 * >Mediawiki - Manual:Namespace</a>
 * 
 */
public class Namespace implements INamespace {

	protected final String[] fNamespaces1 = { "Media", "Special", "", "Talk", "User", "User_talk", "Meta", "Meta_talk", "Image",
			"Image_talk", "MediaWiki", "MediaWiki_talk", "Template", "Template_talk", "Help", "Help_talk", "Category", "Category_talk" };

	protected final String[] fNamespaces2 = { "Media", "Special", "", "Talk", "User", "User_talk", "Meta", "Meta_talk", "File",
			"File_talk", "MediaWiki", "MediaWiki_talk", "Template", "Template_talk", "Help", "Help_talk", "Category", "Category_talk" };

	/**
	 * 
	 * Maps lower-case namespace names to the original names.
	 */
	public final Map<String, String> NAMESPACE_MAP = new HashMap<String, String>();

	/**
	 * Maps namespaces case-insensitively to their according talkspaces.
	 */
	public final Map<String, String> TALKSPACE_MAP = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

	protected ResourceBundle fResourceBundle = null;

	public Namespace() {
		this((ResourceBundle) null);
	}

	public Namespace(Locale locale) {
		this(Messages.getResourceBundle(locale));
	}

	public Namespace(ResourceBundle resourceBundle) {
		fResourceBundle = resourceBundle;
		initializeNamespaces();

		for (String[] namespaces : new String[][] { fNamespaces1, fNamespaces2 }) {
			for (String namespace : namespaces) {
				String namespaceLower;
				if (fResourceBundle == null || fResourceBundle.getLocale() == null) {
					namespaceLower = namespace.toLowerCase();
				} else {
					namespaceLower = namespace.toLowerCase(fResourceBundle.getLocale());
				}
				NAMESPACE_MAP.put(namespaceLower, namespace);
			}

			TALKSPACE_MAP.put(namespaces[convertNumberCode(MEDIA_NAMESPACE_KEY)], null); // media
			TALKSPACE_MAP.put(namespaces[convertNumberCode(SPECIAL_NAMESPACE_KEY)], null); // special
			TALKSPACE_MAP.put(namespaces[convertNumberCode(MAIN_NAMESPACE_KEY)], getTalk()); // ""
			TALKSPACE_MAP.put(namespaces[convertNumberCode(TALK_NAMESPACE_KEY)], getTalk()); // talk
			TALKSPACE_MAP.put(namespaces[convertNumberCode(USER_NAMESPACE_KEY)], getUser_talk()); // user
			TALKSPACE_MAP.put(namespaces[convertNumberCode(USER_TALK_NAMESPACE_KEY)], getUser_talk()); // user_talk
			TALKSPACE_MAP.put(namespaces[convertNumberCode(PROJECT_NAMESPACE_KEY)], getMeta_talk()); // project
			TALKSPACE_MAP.put(namespaces[convertNumberCode(PROJECT_TALK_NAMESPACE_KEY)], getMeta_talk()); // project_talk
			TALKSPACE_MAP.put(namespaces[convertNumberCode(FILE_NAMESPACE_KEY)], getImage_talk()); // image
			TALKSPACE_MAP.put(namespaces[convertNumberCode(FILE_TALK_NAMESPACE_KEY)], getImage_talk()); // image_talk
			TALKSPACE_MAP.put(namespaces[convertNumberCode(MEDIAWIKI_NAMESPACE_KEY)], getMediaWiki_talk()); // mediawiki
			TALKSPACE_MAP.put(namespaces[convertNumberCode(MEDIAWIKI_TALK_NAMESPACE_KEY)], getMediaWiki_talk()); // mediawiki_talk
			TALKSPACE_MAP.put(namespaces[convertNumberCode(TEMPLATE_NAMESPACE_KEY)], getTemplate_talk()); // template
			TALKSPACE_MAP.put(namespaces[convertNumberCode(TEMPLATE_TALK_NAMESPACE_KEY)], getTemplate_talk()); // template_talk
			TALKSPACE_MAP.put(namespaces[convertNumberCode(HELP_NAMESPACE_KEY)], getHelp_talk()); // help
			TALKSPACE_MAP.put(namespaces[convertNumberCode(HELP_TALK_NAMESPACE_KEY)], getHelp_talk()); // help_talk
			TALKSPACE_MAP.put(namespaces[convertNumberCode(CATEGORY_NAMESPACE_KEY)], getCategory_talk()); // category
			TALKSPACE_MAP.put(namespaces[convertNumberCode(CATEGORY_TALK_NAMESPACE_KEY)], getCategory_talk()); // category_talk
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getCategory()
	 */
	public String getCategory() {
		return fNamespaces1[16];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getCategory_talk()
	 */
	public String getCategory_talk() {
		return fNamespaces1[17];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getCategory_talk2()
	 */
	public String getCategory_talk2() {
		return fNamespaces2[17];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getCategory2()
	 */
	public String getCategory2() {
		return fNamespaces2[16];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getHelp()
	 */
	public String getHelp() {
		return fNamespaces1[14];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getHelp_talk()
	 */
	public String getHelp_talk() {
		return fNamespaces1[15];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getHelp_talk2()
	 */
	public String getHelp_talk2() {
		return fNamespaces2[15];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getHelp2()
	 */
	public String getHelp2() {
		return fNamespaces2[14];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getImage()
	 */
	public String getImage() {
		return fNamespaces1[8];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getImage_talk()
	 */
	public String getImage_talk() {
		return fNamespaces1[9];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getImage_talk2()
	 */
	public String getImage_talk2() {
		return fNamespaces2[9];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getImage2()
	 */
	public String getImage2() {
		return fNamespaces2[8];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getMedia()
	 */
	public String getMedia() {
		return fNamespaces1[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getMedia2()
	 */
	public String getMedia2() {
		return fNamespaces2[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getMediaWiki()
	 */
	public String getMediaWiki() {
		return fNamespaces1[10];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getMediaWiki_talk()
	 */
	public String getMediaWiki_talk() {
		return fNamespaces1[11];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getMediaWiki_talk2()
	 */
	public String getMediaWiki_talk2() {
		return fNamespaces2[11];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getMediaWiki2()
	 */
	public String getMediaWiki2() {
		return fNamespaces2[10];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getMeta()
	 */
	public String getMeta() {
		return fNamespaces1[6];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getMeta_talk()
	 */
	public String getMeta_talk() {
		return fNamespaces1[7];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getMeta_talk2()
	 */
	public String getMeta_talk2() {
		return fNamespaces2[7];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getMeta2()
	 */
	public String getMeta2() {
		return fNamespaces2[6];
	}

	public String getNamespace(String namespace) {
		for (int i = 0; i < fNamespaces1.length; i++) {
			if (fNamespaces1[i].equals(namespace)) {
				return namespace;
			}
		}
		for (int i = 0; i < fNamespaces2.length; i++) {
			if (fNamespaces2[i].equals(namespace)) {
				return namespace;
			}
		}
		return "";
	}

	public String getNamespaceByLowercase(String lowercaseNamespace) {
		return NAMESPACE_MAP.get(lowercaseNamespace);
	}

	public String getNamespaceByNumber(int numberCode) {
		return fNamespaces1[convertNumberCode(numberCode)];
	}

	/**
	 * Converts an (external) namespace number code to the position in the
	 * {@link #fNamespaces1} and {@link #fNamespaces2} arrays.
	 * 
	 * @param numberCode
	 *          a code like {@link INamespace#MEDIA_NAMESPACE_KEY}
	 * 
	 * @return an array index
	 */
	protected final int convertNumberCode(int numberCode) {
		return numberCode + 2;
	}

	public ResourceBundle getResourceBundle() {
		return fResourceBundle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getSpecial()
	 */
	public String getSpecial() {
		return fNamespaces1[1];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getSpecial2()
	 */
	public String getSpecial2() {
		return fNamespaces2[1];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getTalk()
	 */
	public String getTalk() {
		return fNamespaces1[3];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getTalk2()
	 */
	public String getTalk2() {
		return fNamespaces2[3];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getTemplate()
	 */
	public String getTemplate() {
		return fNamespaces1[12];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getTemplate_talk()
	 */
	public String getTemplate_talk() {
		return fNamespaces1[13];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getTemplate_talk2()
	 */
	public String getTemplate_talk2() {
		return fNamespaces2[13];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getTemplate2()
	 */
	public String getTemplate2() {
		return fNamespaces2[12];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getUser()
	 */
	public String getUser() {
		return fNamespaces1[4];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getUser_talk()
	 */
	public String getUser_talk() {
		return fNamespaces1[5];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getUser_talk2()
	 */
	public String getUser_talk2() {
		return fNamespaces2[5];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see info.bliki.wiki.namespaces.INamespcae#getUser2()
	 */
	public String getUser2() {
		return fNamespaces2[4];
	}

	/**
	 * Extracts the two namespace strings from the resource bundle into the
	 * {@link #fNamespaces1} and {@link #fNamespaces2} arrays.
	 * 
	 * @param ns1Id
	 *          the first id in the bundle, e.g. {@link Messages#WIKI_API_MEDIA1}
	 * @param ns2Id
	 *          the first id in the bundle, e.g. {@link Messages#WIKI_API_MEDIA2}
	 * @param arrayPos
	 *          the position in the arrays
	 */
	private void extractFromResource(String ns1Id, String ns2Id, int arrayPos) {
		String ns1 = Messages.getString(fResourceBundle, ns1Id);
		if (ns1 != null) {
			fNamespaces1[arrayPos] = ns1;
			String ns2 = Messages.getString(fResourceBundle, ns2Id);
			if (ns2 != null) {
				fNamespaces2[arrayPos] = ns2;
			}
		}
	}

	private void initializeNamespaces() {
		if (fResourceBundle == null) {
			return;
		}

		extractFromResource(Messages.WIKI_API_MEDIA1, Messages.WIKI_API_MEDIA2, 0);
		extractFromResource(Messages.WIKI_API_SPECIAL1, Messages.WIKI_API_SPECIAL2, 1);
		extractFromResource(Messages.WIKI_API_TALK1, Messages.WIKI_API_TALK2, 3);
		extractFromResource(Messages.WIKI_API_USER1, Messages.WIKI_API_USER2, 4);
		extractFromResource(Messages.WIKI_API_USERTALK1, Messages.WIKI_API_USERTALK2, 5);
		extractFromResource(Messages.WIKI_API_META1, Messages.WIKI_API_META2, 6);
		extractFromResource(Messages.WIKI_API_METATALK1, Messages.WIKI_API_METATALK2, 7);
		extractFromResource(Messages.WIKI_API_IMAGE1, Messages.WIKI_API_IMAGE2, 8);
		extractFromResource(Messages.WIKI_API_IMAGETALK1, Messages.WIKI_API_IMAGETALK2, 9);
		extractFromResource(Messages.WIKI_API_MEDIAWIKI1, Messages.WIKI_API_MEDIAWIKI2, 10);
		extractFromResource(Messages.WIKI_API_MEDIAWIKITALK1, Messages.WIKI_API_MEDIAWIKITALK2, 11);
		extractFromResource(Messages.WIKI_API_TEMPLATE1, Messages.WIKI_API_TEMPLATE2, 12);
		extractFromResource(Messages.WIKI_API_TEMPLATETALK1, Messages.WIKI_API_TEMPLATETALK2, 13);
		extractFromResource(Messages.WIKI_API_HELP1, Messages.WIKI_API_HELP2, 14);
		extractFromResource(Messages.WIKI_API_HELPTALK1, Messages.WIKI_API_HELPTALK2, 15);
		extractFromResource(Messages.WIKI_API_CATEGORY1, Messages.WIKI_API_CATEGORY2, 16);
		extractFromResource(Messages.WIKI_API_CATEGORYTALK1, Messages.WIKI_API_CATEGORYTALK2, 17);
	}

	public String getTalkspace(String namespace) {
		return TALKSPACE_MAP.get(namespace);
	}
}
