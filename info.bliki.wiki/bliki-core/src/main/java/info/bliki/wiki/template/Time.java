package info.bliki.wiki.template;

import info.bliki.wiki.model.IWikiModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * A template parser function for <code>{{ #time: ... }}</code> syntax.
 * 
 * NOT COMPLETE YET!!!
 * 
 * See <a
 * href="http://www.mediawiki.org/wiki/Help:Extension:ParserFunctions#.23time">
 * Mediwiki's Help:Extension:ParserFunctions - #time</a>
 * 
 */
public class Time extends AbstractTemplateFunction {
	public final static ITemplateFunction CONST = new Time();

	public Time() {

	}

	public String parseFunction(List<String> list, IWikiModel model, char[] src, int beginIndex, int endIndex) {
		if (list.size() > 0) {
			Date date;
			DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, model.getLocale());
			if (list.size() > 1) {
				String dateTimeParameter = list.get(1);

				try {
					date = df.parse(dateTimeParameter);
				} catch (ParseException e) {
					return "<span class=\"error\">Error: invalid time</span>";
				}
			} else {
				date = model.getCurrentTimeStamp();
			}

			String condition = parse(list.get(0), model);
			if (condition.equals("U")) {
				return secondsSinceJanuary1970(list);
			}
			return df.format(date);
		}
		return null;
	}

	private String secondsSinceJanuary1970(List<String> list) {
		Date date = new Date();
		long secondsSince1970 = date.getTime() / 1000;
		return Long.toString(secondsSince1970);
	}
}
