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
package org.jamwiki;

import java.util.List;
import java.util.Locale;

import org.jamwiki.model.Category;
import org.jamwiki.model.Role;
import org.jamwiki.model.RoleMap;
import org.jamwiki.model.Topic;
import org.jamwiki.model.VirtualWiki;
import org.jamwiki.model.WikiUser;
import org.jamwiki.utils.Pagination;

/**
 * This interface provides all methods needed when retrieving or modifying Wiki
 * data. Any database or other persistency class must implement this interface,
 * and there should also be a corresponding &lt;data-handler&gt; entry for the
 * class in the <code>jamwiki-configuration.xml</code> file.
 * 
 * @see org.jamwiki.WikiBase#getDataHandler
 */
public interface DataHandler {

  /** Ansi data handler class */
  public static final String DATA_HANDLER_ANSI = "org.jamwiki.db.AnsiDataHandler";
  /** DB2 data handler class */
  public static final String DATA_HANDLER_DB2 = "org.jamwiki.db.DB2DataHandler";
  /** DB2/400 data handler class */
  public static final String DATA_HANDLER_DB2400 = "org.jamwiki.db.DB2400DataHandler";
  /** HSql data handler class */
  public static final String DATA_HANDLER_HSQL = "org.jamwiki.db.HSqlDataHandler";
  /** MSSql data handler class */
  public static final String DATA_HANDLER_MSSQL = "org.jamwiki.db.MSSqlDataHandler";
  /** MySql data handler class */
  public static final String DATA_HANDLER_MYSQL = "org.jamwiki.db.MySqlDataHandler";
  /** Oracle data handler class */
  public static final String DATA_HANDLER_ORACLE = "org.jamwiki.db.OracleDataHandler";
  /** Postgres data handler class */
  public static final String DATA_HANDLER_POSTGRES = "org.jamwiki.db.PostgresDataHandler";
  /** Sybase ASA data handler class */
  public static final String DATA_HANDLER_ASA = "org.jamwiki.db.SybaseASADataHandler";

  /**
   * Return a List of all Category objects for a given virtual wiki.
   * 
   * @param virtualWiki
   *          The virtual wiki for which categories are being retrieved.
   * @param pagination
   *          A Pagination object indicating the total number of results and
   *          offset for the results to be retrieved.
   * @return A List of all Category objects for a given virutal wiki.
   * @throws DataAccessException
   *           Thrown if any error occurs during method execution.
   */
  List<Category> getAllCategories(String virtualWiki, Pagination pagination)
      throws DataAccessException;


  /**
   * Retrieve a List of RoleMap objects for all users whose login
   * contains the given login fragment.
   *
   * @param loginFragment A value that must be contained with the user's
   *  login.  This method will return partial matches, so "name" will
   *  match "name", "firstname" and "namesake".
   * @return A list of RoleMap objects containing all roles for all
   *  users whose login contains the login fragment.  If no matches are
   *  found then this method returns an empty List.  This method will
   *  never return <code>null</code>.
   * @throws DataAccessException Thrown if any error occurs during method execution.
   */
  List<RoleMap> getRoleMapByLogin(String loginFragment) throws DataAccessException;

  /**
   * Retrieve a list of RoleMap objects for all users and groups who
   * have been assigned the specified role.
   *
   * @param roleName The name of the role being queried against.
   * @return A list of RoleMap objects containing all roles for all
   *  users and groups who have been assigned the specified role.  If no
   *  matches are found then this method returns an empty List.  This
   *  method will never return <code>null</code>.
   * @throws DataAccessException Thrown if any error occurs during method execution.
   */
  List<RoleMap> getRoleMapByRole(String roleName) throws DataAccessException;

  /**
   * Retrieve all roles assigned to a given group.  Note that for an implementation
   * to work with Spring Security this method MUST return an array of {@link
   * org.jamwiki.authentication.RoleMapImpl} objects.
   *
   * @param groupName The name of the group for whom roles are being retrieved.
   * @return An array of Role objects for the given group, or an empty
   *  array if no roles are assigned to the group.  This method will
   *  never return <code>null</code>.
   * @throws DataAccessException Thrown if any error occurs during method execution.
   */ 
  List<Role> getRoleMapGroup(String groupName) throws DataAccessException;

  /**
   * Retrieve a list of RoleMap objects for all groups.
   *
   * @return A list of RoleMap objects containing all roles for all
   *  groups.  If no matches are found then this method returns an empty
   *  List.  This method will never return <code>null</code>.
   * @throws DataAccessException Thrown if any error occurs during method execution.
   */
  List<RoleMap> getRoleMapGroups() throws DataAccessException;

  /**
   * Retrieve all roles assigned to a given user.  Note that for an implementation
   * to work with Spring Security this method MUST return an array of {@link
   * org.jamwiki.authentication.RoleMapImpl} objects.
   *
   * @param login The login of the user for whom roles are being retrieved.
   * @return A list of Role objects for the given user, or an empty
   *  array if no roles are assigned to the user.  This method will
   *  never return <code>null</code>.
   * @throws DataAccessException Thrown if any error occurs during method execution.
   */
  List<Role> getRoleMapUser(String login) throws DataAccessException;

  /**
   * Retrieve a List of Category objects corresponding to all topics that belong
   * to the category, sorted by either the topic name, or category sort key (if
   * specified).
   * 
   * @param virtualWiki 
   *          The virtual wiki for the category being queried.
   * @param categoryName
   *          The name of the category being queried.
   * @return A List of all Category objects corresponding to all topics that
   *         belong to the category, sorted by either the topic name, or
   *         category sort key (if specified).
   * @throws DataAccessException
   *           Thrown if any error occurs during method execution.
   */
  List<Category> lookupCategoryTopics(String virtualWiki, String categoryName)
      throws DataAccessException;

  /**
   * Retrieve a Topic object that matches the given virtual wiki and topic name.
   * 
   * @param virtualWiki
   *          The virtual wiki for the topic being queried.
   * @param topicName
   *          The name of the topic being queried.
   * @param deleteOK
   *          Set to <code>true</code> if deleted topics can be retrieved,
   *          <code>false</code> otherwise.
   * @param transactionObject
   *          If this method is being called as part of a transaction then this
   *          parameter should contain the transaction object, such as a
   *          database connection. If this method is not part of a transaction
   *          then this value should be <code>null</code>.
   * @return A Topic object that matches the given virtual wiki and topic name,
   *         or <code>null</code> if no matching topic exists.
   * @throws DataAccessException
   *           Thrown if any error occurs during method execution.
   */
  Topic lookupTopic(String virtualWiki, String topicName, boolean deleteOK,
      Object transactionObject);

  /**
   * Retrieve a WikiUser object matching a given user ID.
   *
   * @param userId The ID of the WikiUser being retrieved.
   * @return The WikiUser object matching the given user ID, or
   *  <code>null</code> if no matching WikiUser exists.
   * @throws DataAccessException Thrown if any error occurs during method execution.
   */
  WikiUser lookupWikiUser(int userId) throws DataAccessException;

  /**
   * Retrieve a WikiUser object matching a given username.
   *
   * @param username The username of the WikiUser being retrieved.
   * @return The WikiUser object matching the given username, or
   *  <code>null</code> if no matching WikiUser exists.
   * @throws DataAccessException Thrown if any error occurs during method execution.
   */
  WikiUser lookupWikiUser(String username) throws DataAccessException;

  /**
   * Return a count of all wiki users.
   *
   * @return A count of all wiki users.
   * @throws DataAccessException Thrown if any error occurs during method execution.
   */
  int lookupWikiUserCount() throws DataAccessException;

  /**
   * Retrieve a WikiUser object matching a given username.
   *
   * @param username The username of the WikiUser being retrieved.
   * @return The encrypted password for the given user name, or
   *  <code>null</code> if no matching WikiUser exists.
   * @throws DataAccessException Thrown if any error occurs during method execution.
   */
  String lookupWikiUserEncryptedPassword(String username) throws DataAccessException;

  /**
   * Return a List of user logins for all wiki users.
   *
   * @param pagination A Pagination object indicating the total number of
   *  results and offset for the results to be retrieved.
   * @return A List of user logins for all wiki users.
   * @throws DataAccessException Thrown if any error occurs during method execution.
   */
  List<String> lookupWikiUsers(Pagination pagination) throws DataAccessException;

  /**
   * Perform any required setup steps for the DataHandler instance.
   * 
   * @param locale
   *          The locale to be used when setting up the data handler instance.
   *          This parameter will affect any messages or defaults used for the
   *          DataHandler.
   * @param user
   *          The admin user to use when creating default topics and other
   *          DataHandler parameters.
   * @param username
   *          The admin user's username (login).
   * @param encryptedPassword
   *          The admin user's encrypted password. This value is only required
   *          when creating a new admin user.
   * @throws DataAccessException
   *           Thrown if any error occurs during method execution.
   * @throws WikiException
   *           Thrown if a setup failure occurs.
   */
  void setup(Locale locale, WikiUser user, String username,
      String encryptedPassword);

  /**
   * Given a virtual wiki name, return the corresponding VirtualWiki object.
   * 
   * @param virtualWikiName
   *          The name of the VirtualWiki object that is being retrieved.
   * @return The VirtualWiki object that corresponds to the virtual wiki name
   *         being queried, or <code>null</code> if no matching VirtualWiki can
   *         be found.
   * @throws DataAccessException
   *           Thrown if any error occurs during method execution.
   */
  VirtualWiki lookupVirtualWiki(String virtualWikiName)
      throws DataAccessException;
  
  /**
   * Add or update a WikiUser object.  This method will add a new record
   * if the WikiUser does not have a user ID, otherwise it will perform an
   * update.
   *
   * @param user The WikiUser being added or updated.  If the WikiUser does
   *  not have a user ID then a new record is created, otherwise an update
   *  is performed.
   * @param username The user's username (login).
   * @param encryptedPassword The user's encrypted password.  Required only when the
   *  password is being updated.
   * @throws DataAccessException Thrown if any error occurs during method execution.
   * @throws WikiException Thrown if the user information is invalid.
   */
  void writeWikiUser(WikiUser user, String username, String encryptedPassword) throws DataAccessException, WikiException;

}
