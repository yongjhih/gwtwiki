package info.bliki.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.xml.sax.SAXException;

/**
 * Manages the queries for the <a
 * href="http://meta.wikimedia.org/w/api.php">Wikimedia API</a>
 */
public class Connector {
    final static public String USER_AGENT = "plog4u.org/3.0";

    final static public String UTF8_CHARSET = "utf-8";

    public final static String PARAM_LOGIN_USERNAME = "lgusername";
    public final static String PARAM_LOGIN_USERID = "lguserid";
    public final static String PARAM_LOGIN_TOKEN = "lgtoken";
    public final static String PARAM_FORMAT = "format";
    public final static String PARAM_ACTION = "action";
    public final static String PARAM_TITLES = "titles";
    public final static String PARAM_PAGE = "page";

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
     *          the completed user information or <code>null</code>, if the login
     *          fails
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
        String lgDomain = user.getDomain();
        NameValuePair[] params;
        if (lgDomain.length() > 0) {
            params = new NameValuePair[] { new NameValuePair("action", "login"), new NameValuePair("format", "xml"),
                    new NameValuePair("lgname", userName), new NameValuePair("lgpassword", user.getPassword()),
                    new NameValuePair("lgdomain", user.getDomain()) };
        } else {
            params = new NameValuePair[] { new NameValuePair("action", "login"), new NameValuePair("format", "xml"),
                    new NameValuePair("lgname", userName), new NameValuePair("lgpassword", user.getPassword()) };
        }
        method.addParameters(params);

        try {
            int responseCode = client.executeMethod(method);
            if (responseCode == HttpStatus.SC_OK) {
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

    public List<Page> queryImageinfo(User user, List<String> listOfImageStrings, int imageWidth) {
        String[] valuePairs = { "prop", "imageinfo", "iiprop", "url", "iiurlwidth", Integer.toString(imageWidth) };
        return query(user, listOfImageStrings, valuePairs);
    }


    /**
     *
     * @param user
     *          user login information
     * @param listOfTitleStrings
     *          a list of title Strings "ArticleA,ArticleB,..."
     * @param valuePairs
     *          pairs of query strings which should be appended to the Mediawiki
     *          API URL
     * @return
     */
    public List<Page> query(User user, List<String> listOfTitleStrings, String[] valuePairs) {
        try {
            String responseBody = queryXML(user, listOfTitleStrings, valuePairs);
            if (responseBody != null) {
                // System.out.println(responseBody);
                XMLPagesParser parser = new XMLPagesParser(responseBody);
                parser.parse();
                return parser.getPagesList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        // no pages parsed!?
        return new ArrayList<Page>();
    }

    /**
     * Get the raw XML result from the Mediawiki API
     *
     * @param user
     *          user login information
     * @param valuePairs
     *          pairs of query strings which should be appended to the Mediawiki
     *          API URL
     * @return the raw XML string produced by the query; <code>null</code>
     *         otherwise
     */
    public String queryXML(User user, String[] valuePairs) {
        return queryXML(user, new ArrayList<String>(), valuePairs);
    }

    /**
     * Get the raw XML result from the Mediawiki API
     *
     * @param user
     *          user login information
     * @param listOfTitleStrings
     *          a list of possibly empty title Strings "ArticleA,ArticleB,..."
     * @param valuePairs
     *          pairs of query strings which should be appended to the Mediawiki
     *          API URL
     * @return the raw XML string produced by the query; <code>null</code>
     *         otherwise
     */
    public String queryXML(User user, List<String> listOfTitleStrings, String[] valuePairs) {
        PostMethod method = createAuthenticatedPostMethod(user);

        StringBuffer titlesString = new StringBuffer();
        for (int i = 0; i < listOfTitleStrings.size(); i++) {
            titlesString.append(listOfTitleStrings.get(i));
            if (i < listOfTitleStrings.size() - 1) {
                titlesString.append("|");
            }
        }

        method.addParameter(new NameValuePair(PARAM_ACTION, "query"));
        if (titlesString.length() > 0) {
            // don't encode the title for the NameValuePair !
            method.addParameter(new NameValuePair(PARAM_TITLES, titlesString.toString()));
        }
        if (valuePairs != null && valuePairs.length > 0) {
            for (int i = 0; i < valuePairs.length; i += 2) {
                method.addParameter(new NameValuePair(valuePairs[i], valuePairs[i + 1]));
            }
        }
        return executeHttpMethod(method);
    }

    public Parse parse(User user, String pageName) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(PARAM_PAGE, pageName);
        String xmlResponse = sendParseXML(user, params);
        if (xmlResponse != null) {
            try {
                XMLParseParser xmlParseParser = new XMLParseParser(xmlResponse);
                xmlParseParser.parse();
                return xmlParseParser.getParse();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Sends request for parse action
     * @param user user login information
     * @param params additional parameters
     * @return the raw XML string produced by the query; <code>null</code> otherwise
     */
    public String sendParseXML(User user, Map<String,String> params) {
        PostMethod method = createAuthenticatedPostMethod(user);
        if (params != null && !params.isEmpty()) {
            for (Map.Entry entry : params.entrySet()) {
                method.addParameter(new NameValuePair((String) entry.getKey(), (String) entry.getValue()));
            }
        }
        method.addParameter(new NameValuePair(PARAM_ACTION, "parse"));
        return executeHttpMethod(method);
    }

    private String executeHttpMethod(HttpMethod method) {
        try{
            int responseCode = client.executeMethod(method);
            if (responseCode == HttpStatus.SC_OK) {
                return method.getResponseBodyAsString();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        return null;
    }

    private PostMethod createAuthenticatedPostMethod(User user) {
        PostMethod method = new PostMethod(user.getActionUrl());

        method.setFollowRedirects(false);

        method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        method.setRequestHeader("User-Agent", USER_AGENT);

        method.addParameter(new NameValuePair(PARAM_LOGIN_USERNAME, user.getUserid()));
        method.addParameter(new NameValuePair(PARAM_LOGIN_USERID, user.getNormalizedUsername()));
        method.addParameter(new NameValuePair(PARAM_LOGIN_TOKEN, user.getToken()));
        method.addParameter(new NameValuePair(PARAM_FORMAT, "xml"));
        return method;
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
