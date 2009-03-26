package info.bliki.wiki.filter;

import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.util.TagStack;

/**
 * Entry for a wikipedia list string (i.e. '*', '#',...)
 * 
 */
public class WPListElement {
	int fStartPos;

	int fEndPos;

	public static final int OL = 2;

	public static final int UL = 1;

	final char[] fSequence;

	TagStack fStack;

	public WPListElement(int type, int level, final char[] sequence, int start) {
		fSequence = sequence;
		fStartPos = start;
		fStack = null;
	}

	/**
	 * @return Returns the endPos.
	 */
	public int getEndPos() {
		return fEndPos;
	}

	/**
	 * Create the internal TagNodes stack for a single list line
	 * 
	 * @param endPos
	 *          The endPos to set.
	 */
	public void createTagStack(char[] src, IWikiModel wikiModel, int endPos) {
		fEndPos = endPos;
		if (fEndPos > fStartPos) {
			String rawWikiText = new String(src, fStartPos, fEndPos - fStartPos);
			AbstractParser parser = wikiModel.createNewInstance(rawWikiText);
			fStack = parser.parseRecursiveInternal(wikiModel, true, true);
		}
	}

	/**
	 * @return Returns the startPos.
	 */
	public int getStartPos() {
		return fStartPos;
	}

	/**
	 * @param startPos
	 *          The startPos to set.
	 */
	public void setStartPos(int startPos) {
		fStartPos = startPos;
	}

	// public void filter(char[] src, IWikiModel wikiModel) {
	// if (fEndPos > fStartPos) {
	// WikipediaParser.parseRecursive(new String(src, fStartPos, fEndPos -
	// fStartPos), wikiModel, false, true, false);
	// }
	// }

	/**
	 * @return Returns the sequence.
	 */
	char[] getSequence() {
		return fSequence;
	}

	public TagStack getTagStack() {
		return fStack;
	}
}