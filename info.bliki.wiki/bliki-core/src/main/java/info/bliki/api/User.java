package info.bliki.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages user data from the <a href="http://meta.wikimedia.org/w/api.php">Wikimedia API</a>
 */
public class User {
	public static final String SUCCESS_ID = "Success";

	public static final String ILLEGAL_ID = "Illegal";

	String result;

	String userid;

	String username;

	String normalizedUsername;

	String token;

	String password;

	String actionUrl;

	Connector connector;

	public User() {
		super();
		this.result = "";
		this.userid = "";
		this.username = "";
		this.normalizedUsername = "";
		this.token = "";
		this.password = "";
		this.actionUrl = "";
		this.connector = new Connector();
	}

	public User(String username, String password, String actionUrl) {
		super();
		this.result = ILLEGAL_ID;
		this.userid = "";
		this.username = username;
		this.normalizedUsername = "";
		this.token = "";
		this.password = password;
		this.actionUrl = actionUrl;
		this.connector = new Connector();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			return normalizedUsername.length() > 0 && normalizedUsername.equals(((User) obj).normalizedUsername);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return normalizedUsername.hashCode();
	}

	public boolean login() {
		return connector.login(this) != null;
	}

	public List<Page> queryContent(List<String> listOfTitleStrings) {
		return connector.queryContent(this, listOfTitleStrings);
	}

	public List<Page> queryContent(String[] listOfTitleStrings) {
		return queryContent(arrayToList(listOfTitleStrings));
	}

	public List<Page> queryCategories(List<String> listOfTitleStrings) {
		return connector.queryCategories(this, listOfTitleStrings);
	}

	public List<Page> queryCategories(String[] listOfTitleStrings) {
		return queryCategories(arrayToList(listOfTitleStrings));
	}

	public List<Page> queryInfo(List<String> listOfTitleStrings) {
		return connector.queryInfo(this, listOfTitleStrings);
	}

	public List<Page> queryInfo(String[] listOfTitleStrings) {
		return queryInfo(arrayToList(listOfTitleStrings));
	}

	public List<Page> queryLinks(List<String> listOfTitleStrings) {
		return connector.queryLinks(this, listOfTitleStrings);
	}

	public List<Page> queryLinks(String[] listOfTitleStrings) {
		return queryLinks(arrayToList(listOfTitleStrings));
	}

	public List<Page> queryImageinfo(List<String> listOfImageStrings) {
		return connector.queryImageinfo(this, listOfImageStrings);
	}
	
	public List<Page> queryImageinfo(String[] listOfImageStrings) {
		return queryImageinfo(arrayToList(listOfImageStrings));
	}
	 
	// TODO
	// public boolean submit(String actionUrl, String title, String uploadContent,
	// String summary, String timestamp, boolean minorEdit,
	// boolean watchThis) {
	// return connector.submit(this, actionUrl, title, uploadContent, summary,
	// timestamp, minorEdit, watchThis);
	// }

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public String getActionUrl() {
		return actionUrl;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "Result: " + result + "; UserID: " + userid + "; UserName: " + username + "; NormalizedUsername: " + normalizedUsername
				+ "; Token: " + token + "; ActionURL: " + actionUrl;
	}

	public Connector getConnector() {
		return connector;
	}

	public String getNormalizedUsername() {
		return normalizedUsername;
	}

	public void setNormalizedUsername(String normalizedUsername) {
		this.normalizedUsername = normalizedUsername;
	}

	private List<String> arrayToList(String[] listOfTitleStrings) {
		List<String> list = new ArrayList<String>();
		if (listOfTitleStrings != null) {
			for (int i = 0; i < listOfTitleStrings.length; i++) {
				list.add(listOfTitleStrings[i]);
			}
		}
		return list;
	}
}
