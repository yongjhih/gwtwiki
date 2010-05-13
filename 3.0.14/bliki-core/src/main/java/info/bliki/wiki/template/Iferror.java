package info.bliki.wiki.template;

import info.bliki.wiki.filter.WikipediaScanner;
import info.bliki.wiki.model.IWikiModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A template parser function for <code>{{ #iferror: ... }}</code> syntax. See
 * <a href
 * ="http://www.mediawiki.org/wiki/Help:Extension:ParserFunctions">Mediwiki's
 * Help:Extension:ParserFunctions</a>
 * 
 */
public class Iferror extends AbstractTemplateFunction {
	private final static Pattern CLASS_ERROR_PATTERN = Pattern.compile("class=\"error\"");
	public final static ITemplateFunction CONST = new Iferror();

	public Iferror() {

	}

	public String parseFunction(char[] src, int beginIndex, int endIndex, IWikiModel model) throws IOException {
		List<String> list = new ArrayList<String>();
		WikipediaScanner.splitByPipe(src, beginIndex, endIndex, list);
		if (list.size() > 0) {
			boolean error = false;
			String iferrorCondition = parse(list.get(0), model);
			if (iferrorCondition.length() > 0) {
				error = CLASS_ERROR_PATTERN.matcher(iferrorCondition).find();
			}
			if (error) {
				// &lt;then text&gt;
				if (list.size() >= 2) {
					return parse(list.get(1), model);
				}
				return "";
			} else {
				if (list.size() >= 3) {
					// &lt;else text&gt;
					return parse(list.get(2), model);
				}
				return iferrorCondition;
			}
		}
		return null;
	}
}
