package info.bliki.wiki.template;

import info.bliki.wiki.filter.WikipediaScanner;
import info.bliki.wiki.model.IWikiModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A template parser function for <code>{{lcfirst: ... }}</code> <i>first character to lower case</i>
 * syntax
 * 
 */
public class LCFirst extends AbstractTemplateFunction {
	public final static ITemplateFunction CONST = new LCFirst();

	public LCFirst() {

	}

	public String parseFunction(char[] src, int beginIndex, int endIndex, IWikiModel model) throws IOException {
		List<String> list = new ArrayList<String>();
		WikipediaScanner.splitByPipe(src, beginIndex, endIndex, list);
		return parseFunction(list, model, null, 0, 0);
	}

	public String parseFunction(List<String> list, IWikiModel model, char[] src, int beginIndex, int endIndex) {
		if (list.size() > 0) {
			String word = parse(list.get(0), model);
			if (word.length() > 0) {
				return Character.toLowerCase(word.charAt(0)) + word.substring(1);
			}
			return "";
		}
		return null;
	}
}
