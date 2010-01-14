/**
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, version 2.1, dated February 1999.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the latest version of the GNU Lesser General
 * Public License as published by the Free Software Foundation;
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program (LICENSE.txt); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jamwiki.utils;


import info.bliki.gae.db.GAEDataHandler;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jamwiki.DataAccessException;
import org.jamwiki.DataHandler;
import org.jamwiki.Environment;
import org.jamwiki.WikiBase;
import org.jamwiki.WikiVersion;
import org.jamwiki.model.VirtualWiki;

/**
 * This class provides a variety of general utility methods for handling
 * wiki-specific functionality such as retrieving topics from the URL.
 */
public class WikiUtil {

  private static final WikiLogger logger = WikiLogger.getLogger(WikiUtil.class
      .getName());

  /** webapp context path, initialized from JAMWikiFilter. */
  public static String WEBAPP_CONTEXT_PATH = null;
  private static Pattern INVALID_ROLE_NAME_PATTERN = null;
  private static Pattern INVALID_TOPIC_NAME_PATTERN = null;
  private static Pattern VALID_USER_LOGIN_PATTERN = null;
  public static final String PARAMETER_TOPIC = "topic";
  public static final String PARAMETER_VIRTUAL_WIKI = "virtualWiki";
  public static final String PARAMETER_WATCHLIST = "watchlist";

  static {
    try {
      INVALID_ROLE_NAME_PATTERN = Pattern.compile(Environment
          .getValue(Environment.PROP_PATTERN_INVALID_ROLE_NAME));
      INVALID_TOPIC_NAME_PATTERN = Pattern.compile(Environment
          .getValue(Environment.PROP_PATTERN_INVALID_TOPIC_NAME));
      VALID_USER_LOGIN_PATTERN = Pattern.compile(Environment
          .getValue(Environment.PROP_PATTERN_VALID_USER_LOGIN));
    } catch (PatternSyntaxException e) {
      logger.severe("Unable to compile pattern", e);
    }
  }

  /**
   * Create a pagination object based on parameters found in the current
   * request.
   * 
   * @param request
   *          The servlet request object.
   * @return A Pagination object constructed from parameters found in the
   *         request object.
   */
  public static Pagination buildPagination(HttpServletRequest request) {
    int num = Environment.getIntValue(Environment.PROP_RECENT_CHANGES_NUM);
    if (request.getParameter("num") != null) {
      try {
        num = Integer.parseInt(request.getParameter("num"));
      } catch (NumberFormatException e) {
        // invalid number
      }
    }
    int offset = 0;
    if (request.getParameter("offset") != null) {
      try {
        offset = Integer.parseInt(request.getParameter("offset"));
      } catch (NumberFormatException e) {
        // invalid number
      }
    }
    return new Pagination(num, offset);
  }

  /**
   * Utility method to retrieve an instance of the current data handler.
   * 
   * @return An instance of the current data handler.
   * @throws IOException
   *           Thrown if a data handler instance can not be instantiated.
   */
  public static DataHandler dataHandlerInstance() throws IOException {
    return new GAEDataHandler();
  }
  /**
   * Given an article name, return the appropriate comments topic article name.
   * For example, if the article name is "Topic" then the return value is
   * "Comments:Topic".
   *
   * @param name The article name from which a comments article name is to
   *  be constructed.
   * @return The comments article name for the article name.
   */
  public static String extractCommentsLink(String name) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("Topic name must not be empty in extractCommentsLink");
    }
    WikiLink wikiLink = LinkUtil.parseWikiLink(name);
    if (StringUtils.isBlank(wikiLink.getNamespace())) {
      return NamespaceHandler.NAMESPACE_COMMENTS + NamespaceHandler.NAMESPACE_SEPARATOR + name;
    }
    String namespace = wikiLink.getNamespace();
    String commentsNamespace = NamespaceHandler.getCommentsNamespace(namespace);
    return (!StringUtils.isBlank(commentsNamespace)) ? commentsNamespace + NamespaceHandler.NAMESPACE_SEPARATOR + wikiLink.getArticle() : NamespaceHandler.NAMESPACE_COMMENTS + NamespaceHandler.NAMESPACE_SEPARATOR + wikiLink.getArticle();
  }

  /**
   * Given an article name, extract an appropriate topic article name.  For
   * example, if the article name is "Comments:Topic" then the return value
   * is "Topic".
   *
   * @param name The article name from which a topic article name is to be
   *  constructed.
   * @return The topic article name for the article name.
   */
  public static String extractTopicLink(String name) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("Topic name must not be empty in extractTopicLink");
    }
    WikiLink wikiLink = LinkUtil.parseWikiLink(name);
    if (StringUtils.isBlank(wikiLink.getNamespace())) {
      return name;
    }
    String namespace = wikiLink.getNamespace();
    String mainNamespace = NamespaceHandler.getMainNamespace(namespace);
    return (!StringUtils.isBlank(mainNamespace)) ? mainNamespace + NamespaceHandler.NAMESPACE_SEPARATOR + wikiLink.getArticle() : wikiLink.getArticle();
  }
  
  /**
   * Determine the URL for the default virtual wiki topic, not including the application server context.
   */
  public static String findDefaultVirtualWikiUrl(String virtualWikiName) {
    if (StringUtils.isBlank(virtualWikiName)) {
      virtualWikiName = WikiBase.DEFAULT_VWIKI;
    }
    String target = Environment.getValue(Environment.PROP_BASE_DEFAULT_TOPIC);
    try {
      VirtualWiki virtualWiki = WikiBase.getDataHandler().lookupVirtualWiki(virtualWikiName);
      target = virtualWiki.getDefaultTopicName();
    } catch (DataAccessException e) {
      logger.warning("Unable to retrieve default topic for virtual wiki", e);
    }
    return "/" + virtualWikiName + "/" + target;
  }
  
  /**
   * Return the URL of the index page for the wiki.
   * 
   * @throws DataAccessException
   *           Thrown if any error occurs while retrieving data.
   */
  public static String getBaseUrl() throws DataAccessException {
    String url = Environment.getValue(Environment.PROP_SERVER_URL);
    url += LinkUtil.buildTopicUrl(WEBAPP_CONTEXT_PATH, WikiBase.DEFAULT_VWIKI,
        Environment.getValue(Environment.PROP_BASE_DEFAULT_TOPIC), true);
    return url;
  }

  /**
   * Retrieve a parameter from the servlet request. This method works around
   * some issues encountered when retrieving non-ASCII values from URL
   * parameters.
   * 
   * @param request
   *          The servlet request object.
   * @param name
   *          The parameter name to be retrieved.
   * @param decodeUnderlines
   *          Set to <code>true</code> if underlines should be automatically
   *          converted to spaces.
   * @return The decoded parameter value retrieved from the request.
   */
  public static String getParameterFromRequest(HttpServletRequest request,
      String name, boolean decodeUnderlines) {
    String value = null;
    if (request.getMethod().equalsIgnoreCase("GET")) {
      // parameters passed via the URL are URL encoded, so request.getParameter
      // may
      // not interpret non-ASCII characters properly. This code attempts to work
      // around that issue by manually decoding. yes, this is ugly and it would
      // be
      // great if someone could eventually make it unnecessary.
      String query = request.getQueryString();
      if (StringUtils.isBlank(query)) {
        return null;
      }
      String prefix = name + "=";
      int pos = query.indexOf(prefix);
      if (pos != -1 && (pos + prefix.length()) < query.length()) {
        value = query.substring(pos + prefix.length());
        if (value.indexOf('&') != -1) {
          value = value.substring(0, value.indexOf('&'));
        }
      }
      return Utilities.decodeAndEscapeTopicName(value, decodeUnderlines);
    }
    value = request.getParameter(name);
    if (value == null) {
      value = (String) request.getAttribute(name);
    }
    if (value == null) {
      return null;
    }
    return Utilities.decodeTopicName(value, decodeUnderlines);
  }

  /**
   * Retrieve a topic name from the servlet request. This method will retrieve a
   * request parameter matching the PARAMETER_TOPIC value, and will decode it
   * appropriately.
   * 
   * @param request
   *          The servlet request object.
   * @return The decoded topic name retrieved from the request.
   */
  public static String getTopicFromRequest(HttpServletRequest request) {
    return WikiUtil.getParameterFromRequest(request, WikiUtil.PARAMETER_TOPIC,
        true);
  }

  /**
   * Retrieve a topic name from the request URI. This method will retrieve the
   * portion of the URI that follows the virtual wiki and decode it
   * appropriately.
   * 
   * @param request
   *          The servlet request object.
   * @return The decoded topic name retrieved from the URI.
   */
  public static String getTopicFromURI(HttpServletRequest request) {
    // skip one directory, which is the virutal wiki
    String topic = retrieveDirectoriesFromURI(request, 1);
    if (topic == null) {
      logger.warning("No topic in URL: " + request.getRequestURI());
      return null;
    }
    int pos = topic.indexOf('#');
    if (pos != -1) {
      // strip everything after and including '#'
      if (pos == 0) {
        logger.warning("No topic in URL: " + request.getRequestURI());
        return null;
      }
      topic = topic.substring(0, pos);
    }
    pos = topic.indexOf('?');
    if (pos != -1) {
      // strip everything after and including '?'
      if (pos == 0) {
        logger.warning("No topic in URL: " + request.getRequestURI());
        return null;
      }
      topic = topic.substring(0, pos);
    }
    pos = topic.indexOf(';');
    if (pos != -1) {
      // some servlet containers return parameters of the form
      // ";jsessionid=1234" when getRequestURI is called.
      if (pos == 0) {
        logger.warning("No topic in URL: " + request.getRequestURI());
        return null;
      }
      topic = topic.substring(0, pos);
    }
    if (!StringUtils.isBlank(topic)) {
      topic = Utilities.decodeAndEscapeTopicName(topic, true);
    }
    return topic;
  }

  /**
   * Retrieve a virtual wiki name from the servlet request. This method will
   * retrieve a request parameter matching the PARAMETER_VIRTUAL_WIKI value, and
   * will decode it appropriately.
   * 
   * @param request
   *          The servlet request object.
   * @return The decoded virtual wiki name retrieved from the request.
   */
  public static String getVirtualWikiFromRequest(HttpServletRequest request) {
    String virtualWiki = request.getParameter(WikiUtil.PARAMETER_VIRTUAL_WIKI);
    if (virtualWiki == null) {
      virtualWiki = (String) request
          .getAttribute(WikiUtil.PARAMETER_VIRTUAL_WIKI);
    }
    if (virtualWiki==null || virtualWiki.length()==0) {
      return WikiBase.DEFAULT_VWIKI;
    }
//    if (virtualWiki == null) {
//      return null;
//    }
    return Utilities.decodeTopicName(virtualWiki, true);
  }

  /**
   * Retrieve a virtual wiki name from the request URI. This method will
   * retrieve the portion of the URI that immediately follows the servlet
   * context and decode it appropriately.
   * 
   * @param request
   *          The servlet request object.
   * @return The decoded virtual wiki name retrieved from the URI.
   */
  public static String getVirtualWikiFromURI(HttpServletRequest request) {
    String uri = retrieveDirectoriesFromURI(request, 0);
    if (StringUtils.isBlank(uri)) {
      logger.info("No virtual wiki found in URL: " + request.getRequestURI());
      return null;
    }
    // default the virtual wiki to the URI since the user may have accessed a
    // URL of
    // the form /context/virtualwiki with no trailing slash
    String virtualWiki = uri;
    int slashIndex = uri.indexOf('/');
    if (slashIndex != -1) {
      virtualWiki = uri.substring(0, slashIndex);
    }
    return Utilities.decodeAndEscapeTopicName(virtualWiki, true);
  }
  /**
   * Determine if the system properties file exists and has been initialized.
   * This method is primarily used to determine whether or not to display
   * the system setup page or not.
   *
   * @return <code>true</code> if the properties file has NOT been initialized,
   *  <code>false</code> otherwise.
   */
  public static boolean isFirstUse() {
    return false;//!Environment.getBooleanValue(Environment.PROP_BASE_INITIALIZED);
  }

  /**
   * Determine if the system code has been upgraded from the configured system
   * version.  Thus if the system is upgraded, this method returns <code>true</code>
   *
   * @return <code>true</code> if the system has been upgraded, <code>false</code>
   *  otherwise.
   */
  public static boolean isUpgrade() {
    if (WikiUtil.isFirstUse()) {
      return false;
    }
    WikiVersion oldVersion = new WikiVersion(Environment.getValue(Environment.PROP_BASE_WIKI_VERSION));
    WikiVersion currentVersion = new WikiVersion(WikiVersion.CURRENT_WIKI_VERSION);
    return false; //oldVersion.before(currentVersion);
  }
  /**
   * Utility method for retrieving values from the URI. This method will attempt
   * to properly convert the URI encoding, and then offers a way to return
   * directories after the initial context directory. For example, if the URI is
   * "/context/first/second/third" and this method is called with a skipCount of
   * 1, the return value is "second/third".
   * 
   * @param request
   *          The servlet request object.
   * @param skipCount
   *          The number of directories to skip.
   * @return A UTF-8 encoded portion of the URL that skips the web application
   *         context and skipCount directories, or <code>null</code> if the
   *         number of directories is less than skipCount.
   */
  private static String retrieveDirectoriesFromURI(HttpServletRequest request,
      int skipCount) {
    String uri = request.getRequestURI().trim();
    // FIXME - needs testing on other platforms
    uri = Utilities.convertEncoding(uri, "ISO-8859-1", "UTF-8");
    String contextPath = request.getContextPath().trim();
    if (StringUtils.isBlank(uri) || contextPath == null) {
      return null;
    }
    // make sure there are no instances of "//" in the URL
    uri = uri.replaceAll("(/){2,}", "/");
    if (uri.length() <= contextPath.length()) {
      return null;
    }
    uri = uri.substring(contextPath.length() + 1);
    int i = 0;
    while (i < skipCount) {
      int slashIndex = uri.indexOf('/');
      if (slashIndex == -1) {
        return null;
      }
      uri = uri.substring(slashIndex + 1);
      i++;
    }
    return uri;
  }
}
