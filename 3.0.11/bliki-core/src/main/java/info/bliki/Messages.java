package info.bliki;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Handles the <code>Messages_XX.properties</code> files for I18N support.
 * 
 */
public class Messages {
	public final static String RESOURCE_BUNDLE = "Messages";//$NON-NLS-1$

	private static ResourceBundle resourceBundle = null;

	public final static String WIKI_TAGS_TOC_CONTENT = "wiki.tags.toc.content";

	public final static String WIKI_API_URL = "wiki.api.url";

	public final static String WIKI_API_CATEGORY1 = "wiki.api.category1";

	public final static String WIKI_API_IMAGE1 = "wiki.api.image1";

	public final static String WIKI_API_TEMPLATE1 = "wiki.api.template1";

	public final static String WIKI_API_CATEGORY2 = "wiki.api.category2";

	public final static String WIKI_API_IMAGE2 = "wiki.api.image2";

	public final static String WIKI_API_TEMPLATE2 = "wiki.api.template2";

	public Messages() {
	}

	public static ResourceBundle getResourceBundle(Locale locale) {
		try {
			resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE, locale);
			return resourceBundle;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getString(final ResourceBundle bundle, final String key) {
		try {
			return bundle.getString(key);
		} catch (final Exception e) {
			return "!" + key + "!";//$NON-NLS-2$ //$NON-NLS-1$
		}
	}
}
