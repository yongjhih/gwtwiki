package info.bliki.wiki.template;

import info.bliki.wiki.model.IWikiModel;

import java.util.List;

/**
 * A template parser function for <code>{{safesubst: ... }}</code>. See <a
 * href="http://en.wikipedia.org/wiki/en:Help:Substitution#safesubst:"
 * >Wikipedia-Help:Substitution</a>
 * 
 */
public class Safesubst extends AbstractTemplateFunction {
	public final static ITemplateFunction CONST = new Safesubst();

	public Safesubst() {

	}

	public String parseFunction(List<String> parts, IWikiModel model, char[] src, int beginIndex, int endIndex) {
		if (model.isTemplateTopic()) {
			StringBuilder template = new StringBuilder(endIndex - beginIndex + 4);
			template.append("{{");
			template.append(src, beginIndex, endIndex - beginIndex);
			template.append("}}");
			return parse(template.toString(), model);
		}
		return "";
	}
}
