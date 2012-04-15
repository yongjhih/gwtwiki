package info.bliki.wiki.template;

import info.bliki.wiki.model.IWikiModel;

import java.util.List;

/**
 * A template parser function for <code>{{ #if: ... }}</code> syntax. See <a
 * href
 * ="http://www.mediawiki.org/wiki/Help:Extension:ParserFunctions">Mediwiki's
 * Help:Extension:ParserFunctions</a>
 * 
 */
public class If extends AbstractTemplateFunction {
	public final static ITemplateFunction CONST = new If();

	public If() {

	}

	@Override
	public String parseFunction(List<String> list, IWikiModel model, char[] src, int beginIndex, int endIndex, boolean isSubst) {
		if (list.size() > 1) {
			String ifCondition = isSubst ? list.get(0) : parse(list.get(0), model);
			if (ifCondition.length() > 0) {
				// &lt;then text&gt;
				return isSubst ? list.get(1) : parse(list.get(1), model);
			} else {
				if (list.size() >= 3) {
					// &lt;else text&gt;
					return isSubst ? list.get(2) : parse(list.get(2), model);
				}
			}
		}
		return null;
	}
}
