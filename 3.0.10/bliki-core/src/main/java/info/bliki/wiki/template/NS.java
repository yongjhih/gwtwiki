package info.bliki.wiki.template;

import info.bliki.wiki.filter.WikipediaScanner;
import info.bliki.wiki.model.IWikiModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A template parser function for <code>{{ns: ... }}</code> <i>namespace/i>
 * syntax
 * 
 */
public class NS extends AbstractTemplateFunction {
	public final static String[] NAMESPACE = { "Media", "Special", "", "Talk", "User", "User_talk", "Meta", "Meta_talk", "Image",
			"Image_talk", "MediaWiki", "MediaWiki_talk", "Template", "Template_talk", "Help", "Help_talk", "Category", "Category_talk" };

	public final static String[] NAMESPACE_LOWERCASE = { "media", "special", "", "talk", "user", "user_talk", "project",
			"project_talk", "image", "image_talk", "mediawiki", "mediawiki_talk", "template", "template_talk", "help", "help_talk",
			"category", "category_talk" };

	public final static Map<String, String> NAMESPACE_MAP = new HashMap<String, String>();

	public final static ITemplateFunction CONST = new NS();

	static {
		for (int i = 0; i < NAMESPACE_LOWERCASE.length; i++) {
			NAMESPACE_MAP.put(NAMESPACE_LOWERCASE[i], NAMESPACE[i]);
		}
	}

	public NS() {

	}

	public String parseFunction(char[] src, int beginIndex, int endIndex, IWikiModel model) throws IOException {
		List<String> list = new ArrayList<String>();
		WikipediaScanner.splitByPipe(src, beginIndex, endIndex, list);
		if (list.size() > 0) {
			String arg0 = parse(list.get(0), model);
			try {
				int numberCode = Integer.valueOf(arg0).intValue();
				if (numberCode >= (-2) || numberCode <= 15) {
					return NAMESPACE[numberCode + 2];
				}
			} catch (NumberFormatException nfe) {
				// the given argument could not be parsed as integer number

				arg0 = arg0.replaceAll(" ", "_");
				String value = NAMESPACE_MAP.get(arg0.toLowerCase());
				if (value != null) {
					return value;
				}
				return "[[:Template:Ns:"+arg0+"]]";
			} 
		}
		return null;
	}

}
