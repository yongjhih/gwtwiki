package info.bliki.wiki.dump;

public class WikiArticle {
	private String text;
	private String title;
	private String timeStamp;
	private String id = null;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * @param id
	 *          the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public void setText(String newText) {
		text = newText;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void setTitle(String newTitle) {
		title = newTitle;
	}

	@Override
	public String toString() {
		return title + "\n" + text;
	}
}
