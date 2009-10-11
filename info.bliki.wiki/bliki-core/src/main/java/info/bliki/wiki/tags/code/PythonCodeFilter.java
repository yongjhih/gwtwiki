package info.bliki.wiki.tags.code;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Syntax highlighting support for Python source codes
 * 
 */
public class PythonCodeFilter extends AbstractCPPBasedCodeFilter implements SourceCodeFormatter {

	private static HashMap<String, String> KEYWORD_SET = new HashMap<String, String>();

	private static final String[] KEYWORDS = { "False", "None", "True", "and", "as", "assert", "class", "break", "continue", "def",
			"del", "else", "elif", "except", "finally", "for", "from", "global", "is", "import", "in", "if", "lambda", "nonlocal", "not",
			"or", "pass", "print", "raise", "return", "try", "with", "while", "yield" };

	// private static final String[] OBJECT_WORDS =
	// {
	// };

	private static HashSet<String> OBJECT_SET = new HashSet<String>();

	static {
		for (int i = 0; i < KEYWORDS.length; i++) {
			createHashMap(KEYWORD_SET, KEYWORDS[i]);
		}
		// for (int i = 0; i < OBJECT_WORDS.length; i++) {
		// OBJECT_SET.add(OBJECT_WORDS[i]);
		// }
	}

	public PythonCodeFilter() {
	}

	public String filter(String input) {
		char[] source = input.toCharArray();
		int currentPosition = 0;
		int identStart = 0;
		char currentChar = ' ';

		HashMap<String, String> keywordsSet = getKeywordSet();
		HashSet<String> objectsSet = getObjectSet();
		StringBuilder result = new StringBuilder(input.length() + input.length() / 4);
		boolean identFound = false;
		// result.append("<font color=\"#000000\">");
		try {
			while (true) {
				currentChar = source[currentPosition++];
				if ((currentChar >= 'A' && currentChar <= 'Z') || (currentChar == '_') || (currentChar >= 'a' && currentChar <= 'z')) {
					identStart = currentPosition - 1;
					identFound = true;
					// start of identifier ?
					while ((currentChar >= 'a' && currentChar <= 'z') || (currentChar >= 'A' && currentChar <= 'Z') || currentChar == '_') {
						currentChar = source[currentPosition++];
					}
					currentPosition = appendIdentifier(input, identStart, currentPosition, keywordsSet, objectsSet, result);
					identFound = false;
					continue; // while loop
				} else if (currentChar == '\"') { // strings
					result.append(FONT_STRINGS);
					appendChar(result, currentChar);
					while (currentPosition < input.length()) {
						currentChar = source[currentPosition++];
						appendChar(result, currentChar);
						if (currentChar == '\\') {
							currentChar = source[currentPosition++];
							appendChar(result, currentChar);
							continue;
						}
						if (currentChar == '\"') {
							break;
						}
					}
					result.append(FONT_END);
					continue;
				} else if (currentChar == '\'') { // strings
					result.append(FONT_STRINGS);
					appendChar(result, currentChar);
					while (currentPosition < input.length()) {
						currentChar = source[currentPosition++];
						appendChar(result, currentChar);
						if (currentChar == '\\') {
							currentChar = source[currentPosition++];
							appendChar(result, currentChar);
							continue;
						}
						if (currentChar == '\'') {
							break;
						}
					}
					result.append(FONT_END);
					continue;
				} else if (currentChar == '#' && ((currentPosition > 1 && source[currentPosition - 2] == '\n') ||
						                              (currentPosition == 1))) {
					// line comment
					result.append(FONT_COMMENT);
					appendChar(result, currentChar);
					appendChar(result, source[currentPosition++]);
					while (currentPosition < input.length()) {
						currentChar = source[currentPosition++];
						appendChar(result, currentChar);
						if (currentChar == '\n') {
							break;
						}
					}
					result.append(FONT_END);
					continue;
				}
				appendChar(result, currentChar);

			}
		} catch (IndexOutOfBoundsException e) {
			if (identFound) {
				currentPosition = appendIdentifier(input, identStart, currentPosition, keywordsSet, null, result);
			}
		}
		// result.append(FONT_END);
		return result.toString();
	}

	/**
	 * @return Returns the KEYWORD_SET.
	 */
	@Override
	public HashMap<String, String> getKeywordSet() {
		return KEYWORD_SET;
	}

	public String getName() {
		return "python";
	}

	/**
	 * @return Returns the OBJECT_SET.
	 */
	@Override
	public HashSet<String> getObjectSet() {
		return OBJECT_SET;
	}

}