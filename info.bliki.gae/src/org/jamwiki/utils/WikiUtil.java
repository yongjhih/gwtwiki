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
import org.jamwiki.DataHandler;
import org.jamwiki.Environment;

/**
 * This class provides a variety of general utility methods for handling
 * wiki-specific functionality such as retrieving topics from the URL.
 */
public class WikiUtil {

	private static final WikiLogger logger = WikiLogger.getLogger(WikiUtil.class.getName());

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
			INVALID_ROLE_NAME_PATTERN = Pattern.compile(Environment.getValue(Environment.PROP_PATTERN_INVALID_ROLE_NAME));
			INVALID_TOPIC_NAME_PATTERN = Pattern.compile(Environment.getValue(Environment.PROP_PATTERN_INVALID_TOPIC_NAME));
			VALID_USER_LOGIN_PATTERN = Pattern.compile(Environment.getValue(Environment.PROP_PATTERN_VALID_USER_LOGIN));
		} catch (PatternSyntaxException e) {
			logger.severe("Unable to compile pattern", e);
		}
	}

	/**
   * Create a pagination object based on parameters found in the current
   * request.
   *
   * @param request The servlet request object.
   * @return A Pagination object constructed from parameters found in the
   *  request object.
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
   * Retrieve a virtual wiki name from the servlet request.  This method
   * will retrieve a request parameter matching the PARAMETER_VIRTUAL_WIKI
   * value, and will decode it appropriately.
   *
   * @param request The servlet request object.
   * @return The decoded virtual wiki name retrieved from the request.
   */
  public static String getVirtualWikiFromRequest(HttpServletRequest request) {
    String virtualWiki = request.getParameter(WikiUtil.PARAMETER_VIRTUAL_WIKI);
    if (virtualWiki == null) {
      virtualWiki = (String)request.getAttribute(WikiUtil.PARAMETER_VIRTUAL_WIKI);
    }
    if (virtualWiki == null) {
      return null;
    }
    return Utilities.decodeTopicName(virtualWiki, true);
  }

  /**
   * Retrieve a virtual wiki name from the request URI.  This method will
   * retrieve the portion of the URI that immediately follows the servlet
   * context and decode it appropriately.
   *
   * @param request The servlet request object.
   * @return The decoded virtual wiki name retrieved from the URI.
   */
  public static String getVirtualWikiFromURI(HttpServletRequest request) {
    String uri = retrieveDirectoriesFromURI(request, 0);
    if (StringUtils.isBlank(uri)) {
      logger.info("No virtual wiki found in URL: " + request.getRequestURI());
      return null;
    }
    // default the virtual wiki to the URI since the user may have accessed a URL of
    // the form /context/virtualwiki with no trailing slash
    String virtualWiki = uri;
    int slashIndex = uri.indexOf('/');
    if (slashIndex != -1) {
      virtualWiki = uri.substring(0, slashIndex);
    }
    return Utilities.decodeAndEscapeTopicName(virtualWiki, true);
  }
  
  /**
   * Utility method for retrieving values from the URI.  This method
   * will attempt to properly convert the URI encoding, and then offers a way
   * to return directories after the initial context directory.  For example,
   * if the URI is "/context/first/second/third" and this method is called
   * with a skipCount of 1, the return value is "second/third".
   *
   * @param request The servlet request object.
   * @param skipCount The number of directories to skip.
   * @return A UTF-8 encoded portion of the URL that skips the web application
   *  context and skipCount directories, or <code>null</code> if the number of
   *  directories is less than skipCount.
   */
  private static String retrieveDirectoriesFromURI(HttpServletRequest request, int skipCount) {
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
