package info.bliki.wiki.template;

import info.bliki.wiki.filter.WikipediaScanner;
import info.bliki.wiki.model.IWikiModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A template parser function for <code>{{ #switch: ... }}</code> syntax
 * 
 */
public class Switch extends AbstractTemplateFunction {
	public final static ITemplateFunction CONST = new Switch();

	public Switch() {

	}

	public String parseFunction(char[] src, int beginIndex, int endIndex, IWikiModel model) throws IOException {
		List<String> list = new ArrayList<String>();
		WikipediaScanner.splitByPipe(src, beginIndex, endIndex, list);
		if (list.size() > 2) {
			String first = parse(list.get(0), model);
			// StringBuilder firstBuffer = new StringBuilder(first.length());
			// TemplateParser.parse(first, model, firstBuffer, false);
			String conditionString = first.trim();
			boolean valueFound = false;
			for (int i = 1; i < list.size(); i++) {
				String temp = parse(list.get(i), model);
				int index = temp.indexOf('=');
				String leftHandSide;
				if (index >= 0) {
					if (valueFound == true) {
						return temp.substring(index + 1).trim();
					}
					leftHandSide = temp.substring(0, index).trim();
				} else {
					leftHandSide = temp.trim();
				}
				String parsedLHS = parse(leftHandSide, model);
				if (equalsTypes(conditionString, parsedLHS)) {
					if (index >= 0) {
						return temp.substring(index + 1).trim();
					} else {
						valueFound = true;
					}
				}

			}
		}
		return null;
	}

	private boolean equalsTypes(String first, String second) {

		boolean result = false;
		if (first.length() == 0) {
			return second.length() == 0;
		}
		if (second.length() == 0) {
			return first.length() == 0;
		}
		if (first.charAt(0) == '+') {
			first = first.substring(1);
		}
		if (second.charAt(0) == '+') {
			second = second.substring(1);
		}

		try {
			double d1 = Double.parseDouble(first);
			double d2 = Double.parseDouble(second);
			if (d1 == d2) {
				result = true;
			}
		} catch (NumberFormatException e) {
			result = first.equals(second);
		}

		return result;
	}
}
