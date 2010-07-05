package info.bliki.wiki.tags.code;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Syntax highlighting support for SQL 'source' commands
 */
public class SQLCodeFilter extends AbstractCPPBasedCodeFilter implements SourceCodeFormatter {

	private static final String[] KEYWORDS = {
		"alter",
		"and",
		"blob",
		"boolean",
		"character",
		"clob",
		"column",
		"constraint",
		"create",
		"default",
		"delete",
		"drop",
		"false",
		"from",
		"in",
		"insert",
		"integer",
		"key",
		"lob",
		"not",
		"null",
		"or",
		"procedure",
		"references",
		"select",
		"set",
		"table",
		"timestamp",
		"true",
		"update",
		"varchar",
		"where",
	};

	private static HashMap<String, String> KEYWORD_SET =
		new HashMap<String, String>();

	static {
		for (String k : KEYWORDS) {
			createHashMap(KEYWORD_SET, k);
		}
	}

	public SQLCodeFilter() {
		// empty
	}

	/**
	* @return Returns the KEYWORD_SET.
	*/
	@Override
	public HashMap<String, String> getKeywordSet() {
		return KEYWORD_SET;
	}

	/**
	* @return Returns the OBJECT_SET.
	*/
	@Override
	public HashMap<String, String> getObjectSet() {
		return null;
	}

	Pattern wp = Pattern.compile("[a-z]+", Pattern.MULTILINE);
	// Pattern cp = Pattern.compile("^\\s*--.*$", Pattern.MULTILINE);

	/** Do the work of filtering one chunk of code in a <source> type element
	 */
	@Override
	public String filter(String input) {
		StringBuffer sb = new StringBuffer();
		Matcher wm = wp.matcher(input);

		HashMap<String, String> keywordSet = getKeywordSet();

		while (wm.find()) {
			String word = wm.group(0);
			if (keywordSet.get(word.toLowerCase()) != null) {
				wm.appendReplacement(sb, FONT_KEYWORD + word + FONT_END);
			} else {
				wm.appendReplacement(sb, word);
			}
		}
		wm.appendTail(sb);

		String ret = sb.toString();

		return ret.replaceAll("\n", "<br/>");

	}
 
	@Override
	public boolean isKeywordCaseSensitive() {
		return false;
	}
}
