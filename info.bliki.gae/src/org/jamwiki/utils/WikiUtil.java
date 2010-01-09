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
   * Utility method to retrieve an instance of the current data handler.
   * 
   * @return An instance of the current data handler.
   * @throws IOException
   *           Thrown if a data handler instance can not be instantiated.
   */
  public static DataHandler dataHandlerInstance() throws IOException {
    return new GAEDataHandler();
  }


}
