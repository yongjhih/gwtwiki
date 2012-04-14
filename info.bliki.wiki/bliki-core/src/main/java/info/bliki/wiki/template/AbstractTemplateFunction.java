package info.bliki.wiki.template;

import info.bliki.wiki.filter.TemplateParser;
import info.bliki.wiki.model.IWikiModel;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A template parser function for <code>{{ #if: ... }}</code> syntax
 * 
 */
public abstract class AbstractTemplateFunction implements ITemplateFunction {
	// public final static ITemplateFunction CONST = new
	// AbstractTemplateFunction();

	public AbstractTemplateFunction() {

	}

	public String parseFunction(char[] src, int beginIndex, int endIndex, IWikiModel model) throws IOException {
		return null;
	}

	public String getFunctionDoc() {
		return null;
	}

	public String parseFunction(List<String> parts, IWikiModel model, char[] src, int beginIndex, int endIndex) throws IOException {
		return parseFunction(parts, model, src, beginIndex, endIndex, false);
	}

	public abstract String parseFunction(List<String> parts, IWikiModel model, char[] src, int beginIndex, int endIndex,
			boolean isSubst) throws IOException;

	/**
	 * Parse the given plain content string with the template parser.
	 * 
	 * @param plainContent
	 * @param model
	 * @return
	 */
	public String parse(String plainContent, IWikiModel model) {
		if (plainContent == null || plainContent.length() == 0) {
			return "";
		}
		StringBuilder buf = new StringBuilder(plainContent.length());
		try {
			TemplateParser.parse(plainContent, model, buf, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buf.toString().trim();
	}

	public String parsePreprocess(String plainContent, IWikiModel model, Map<String, String> templateParameterMap) {
		if (plainContent == null || plainContent.length() == 0) {
			return "";
		}
		StringBuilder buf = new StringBuilder(plainContent.length());
		try {
			TemplateParser.parsePreprocessRecursive(plainContent, model, buf, false, false, templateParameterMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buf.toString().trim();
	}
}
