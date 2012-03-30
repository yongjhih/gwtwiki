package info.bliki.wiki.template;

import info.bliki.wiki.model.IWikiModel;

import java.util.List;

/**
 * A template parser function for <code>{{titleparts: ... }}</code>. This
 * function separates a pagetitle into segments based on slashes, then returns
 * some of those segments as output. See <a href=
 * "https://www.mediawiki.org/wiki/Help:Extension:ParserFunctions#.23titleparts"
 * >Mediawiki - Help:Extension:ParserFunctions - Titleparts</a>
 * 
 */
public class Titleparts extends AbstractTemplateFunction {
	public final static ITemplateFunction CONST = new Titleparts();

	public Titleparts() {

	}

	public String parseFunction(List<String> list, IWikiModel model, char[] src, int beginIndex, int endIndex) {
		if (list.size() > 0) {
			String pagename = parse(list.get(0), model);
			int numberOfSegments = 0;
			if (list.size() > 1) {
				try {
					String str = parse(list.get(1), model);
					numberOfSegments = Integer.parseInt(str);
				} catch (NumberFormatException nfe) {

				}
			}
			if (numberOfSegments > 0) {
				int indx = -1;
				while (numberOfSegments > 0) {
					indx = pagename.indexOf('/', ++indx);
					if (--numberOfSegments == 0) {
						if (indx >= 0) {
							return pagename.substring(0, indx);
						}
						return pagename;
					}
					if (indx < 0) {
						return pagename;
					}
				}
			} else {
				int indx = pagename.length();
				while (numberOfSegments < 0) {
					indx = pagename.lastIndexOf('/', --indx);
					if (++numberOfSegments == 0) {
						if (indx >= 0) {
							return pagename.substring(0, indx);
						}
						return "";
					}
					if (indx < 0) {
						return "";
					}
				}
			}

			return pagename;
		}
		return null;
	}
}
