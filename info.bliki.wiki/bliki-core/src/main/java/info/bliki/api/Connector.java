package info.bliki.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.xml.sax.SAXException;

/**
 * Manages the queries for the <a
 * href="http://meta.wikimedia.org/w/api.php">Wikimedia API</a>
 */
public class Connector {
	final static public String USER_AGENT = "plog4u.org/3.0";

	final static public String UTF8_CHARSET = "utf-8";

	// create a ConnectionManager
	private MultiThreadedHttpConnectionManager manager;

	private HttpClient client;

	public Connector() {
		manager = new MultiThreadedHttpConnectionManager();
		// manager.setMaxConnectionsPerHost(6);
		// manager.setMaxTotalConnections(18);
		// manager.setConnectionStaleCheckingEnabled(true);
		// open the conversation
		client = new HttpClient(manager);
		// setHTTPClientParameters(client);
	}

	/**
	 * Complete the Users login information The user must contain a username,
	 * password and actionURL
	 * 
	 * @param user
	 *          the completed user information or <code>null</code>, if the
	 *          login fails
	 * @return
	 */
	public User login(User user) {
		PostMethod method = new PostMethod(user.getActionUrl());
		String userName = user.getUsername();

		if (userName == null || userName.trim().length() == 0) {
			// no nothing for dummy users
			return user;
		}

		method.setFollowRedirects(false);
		method.addRequestHeader("User-Agent", USER_AGENT);
		NameValuePair[] params = new NameValuePair[] { new NameValuePair("action", "login"), new NameValuePair("format", "xml"),
				new NameValuePair("lgname", userName), new NameValuePair("lgpassword", user.getPassword()) };
		method.addParameters(params);

		try {
			int responseCode = client.executeMethod(method);
			if (responseCode == 200) {
				String responseBody = method.getResponseBodyAsString();
				XMLUserParser parser = new XMLUserParser(user, responseBody);
				parser.parse();
				if (!user.getResult().equals(User.SUCCESS_ID)) {
					return null;
				}
				return user;
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}

		return null;
	}

	public List<Page> queryContent(User user, List<String> listOfTitleStrings) {
		String[] valuePairs = { "prop", "revisions", "rvprop", "timestamp|user|comment|content" };
		return query(user, listOfTitleStrings, valuePairs);
	}

	public List<Page> queryCategories(User user, List<String> listOfTitleStrings) {
		String[] valuePairs = { "prop", "categories" };
		return query(user, listOfTitleStrings, valuePairs);
	}

	public List<Page> queryInfo(User user, List<String> listOfTitleStrings) {
		String[] valuePairs = { "prop", "info" };
		return query(user, listOfTitleStrings, valuePairs);
	}

	public List<Page> queryLinks(User user, List<String> listOfTitleStrings) {
		String[] valuePairs = { "prop", "links" };
		return query(user, listOfTitleStrings, valuePairs);
	}

	public List<Page> queryImageinfo(User user, List<String> listOfImageStrings) {
		String[] valuePairs = { "prop", "imageinfo", "iiprop", "url" };
		return query(user, listOfImageStrings, valuePairs);
	}

	/**
	 * 
	 * @param user
	 *          user login information
	 * @param listOfTitleStrings
	 *          a list of title Strings "ArticleA,ArticleB,..."
	 * @return
	 */
	public List<Page> query(User user, List<String> listOfTitleStrings, String[] valuePairs) {
		PostMethod method = new PostMethod(user.getActionUrl());

		method.setFollowRedirects(false);

		method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		method.setRequestHeader("User-Agent", USER_AGENT);

		StringBuffer titlesString = new StringBuffer();
		for (int i = 0; i < listOfTitleStrings.size(); i++) {
			titlesString.append(listOfTitleStrings.get(i));
			if (i < listOfTitleStrings.size() - 1) {
				titlesString.append("|");
			}
		}
		int k = 0;
		int size = 6;
		if (valuePairs != null) {
			size = 6 + (valuePairs.length / 2);
		}

		try {
			NameValuePair[] params = new NameValuePair[size];

			params[k++] = new NameValuePair("action", "query");
			// don't encode the title for the NameValuePair !
			params[k++] = new NameValuePair("titles", titlesString.toString());
			params[k++] = new NameValuePair("lgusername", user.getUserid());
			params[k++] = new NameValuePair("lguserid", user.getNormalizedUsername());
			params[k++] = new NameValuePair("lgtoken", user.getToken());
			params[k++] = new NameValuePair("format", "xml");
			if (valuePairs != null && valuePairs.length > 0) {
				for (int i = 0; i < valuePairs.length; i += 2) {
					params[k++] = new NameValuePair(valuePairs[i], valuePairs[i + 1]);
				}
			}
			method.addParameters(params);

			int responseCode = client.executeMethod(method);
			if (responseCode == 200) {
				String responseBody = method.getResponseBodyAsString();
				// System.out.println(responseBody);
				XMLPagesParser parser = new XMLPagesParser(responseBody);
				parser.parse();
				return parser.getPagesList();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		// no pages parsed!?
		return new ArrayList<Page>();
	}

	// TODO: this doesn't work at the moment:
	// public boolean submit(User user, String actionUrl, String title, String
	// uploadContent, String summary, String timestamp,
	// boolean minorEdit, boolean watchThis) {
	//
	// PostMethod method = new PostMethod(actionUrl);
	//
	// method.setFollowRedirects(false);
	// method.addRequestHeader("User-Agent", USER_AGENT);
	// method.addRequestHeader("Content-Type",
	// PostMethod.FORM_URL_ENCODED_CONTENT_TYPE + "; charset=" + UTF8_CHARSET);
	// try {
	//
	// NameValuePair[] params = new NameValuePair[] { new NameValuePair("title",
	// title),
	// new NameValuePair("wpTextbox1", uploadContent), new
	// NameValuePair("wpEdittime", timestamp),
	// new NameValuePair("wpSummary", summary), new NameValuePair("wpEditToken",
	// user.getToken()),
	// new NameValuePair("wpSave", "yes"), new NameValuePair("action", "submit")
	// };
	// method.addParameters(params);
	// if (minorEdit)
	// method.addParameter("wpMinoredit", "1");
	// if (watchThis)
	// method.addParameter("wpWatchthis", "1");
	//
	// int responseCode = client.executeMethod(method);
	// String responseBody = method.getResponseBodyAsString();
	// // log(method);
	//
	// // since 11dec04 there is a single linefeed instead of an empty
	// // page.. trim() helps.
	// if (responseCode == 302 && responseBody.trim().length() == 0) {
	// // log("store successful, reloading");
	// // Loaded loaded = load(actionUrl, config.getUploadCharSet(),
	// // title);
	// // result = new Stored(actionUrl, config.getUploadCharSet(),
	// // loaded.title, loaded.content, false);
	// return true;
	// } else if (responseCode == 200) {
	// // // log("store not successful, conflict detected");
	// // Parsed parsed = parseBody(config.getUploadCharSet(),
	// // responseBody);
	// // Content cont = new Content(parsed.timestamp, parsed.body);
	// // result = new Stored(actionUrl, config.getUploadCharSet(),
	// // parsed.title, cont, true);
	// // } else {
	// // throw new UnexpectedAnswerException(
	// // "store not successful: expected 200 OK, got "
	// // + method.getStatusLine());
	// }
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// } catch (HttpException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// method.releaseConnection();
	// }
	// return false;
	// }

}
