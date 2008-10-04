package info.bliki.wiki.template;

import info.bliki.wiki.filter.Encoder;
import info.bliki.wiki.model.WikiModel;

import java.util.Map;

/**
 * Wiki model implementation which allows some special JUnit tests for template
 * parser functions
 * 
 */
public class ParserFunctionModel extends WikiModel {
	final static String CONCAT = "{{{1|}}}{{{2|}}}{{{3|}}}{{{4|}}}{{{5|}}}{{{6|}}}{{{7|}}}{{{8|}}}{{{9|}}}{{{10|}}}";

	/**
	 * Add German namespaces to the wiki model
	 * 
	 * @param imageBaseURL
	 * @param linkBaseURL
	 */
	public ParserFunctionModel(String imageBaseURL, String linkBaseURL) {
		super(imageBaseURL, linkBaseURL);
	}

	/**
	 * Add templates: &quot;Test&quot;, &quot;Templ1&quot;, &quot;Templ2&quot;,
	 * &quot;Include Page&quot;
	 * 
	 */
	@Override
	public String getRawWikiContent(String namespace, String articleName, Map<String, String> map) {
		String result = super.getRawWikiContent(namespace, articleName, map);
		if (result != null) {
			return result;
		}
		String name = Encoder.encodeTitleUrl(articleName);
		if (name.equals("Concat")) {
			return CONCAT;
		}
		return null;
	}

}
