package info.bliki.gae.db;

import info.bliki.gae.model.AuthorityEntity;
import info.bliki.gae.model.GroupAuthorityEntity;
import info.bliki.gae.model.GroupMemberEntity;
import info.bliki.gae.model.RoleEntity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.jamwiki.DataAccessException;
import org.jamwiki.DataHandler;
import org.jamwiki.Environment;
import org.jamwiki.WikiBase;
import org.jamwiki.WikiException;
import org.jamwiki.WikiMessage;
import org.jamwiki.authentication.JAMWikiAuthenticationConfiguration;
import org.jamwiki.authentication.RoleImpl;
import org.jamwiki.authentication.WikiUserDetails;
import org.jamwiki.db.WikiDatabase;
import org.jamwiki.model.Category;
import org.jamwiki.model.Role;
import org.jamwiki.model.RoleMap;
import org.jamwiki.model.Topic;
import org.jamwiki.model.TopicVersion;
import org.jamwiki.model.VirtualWiki;
import org.jamwiki.model.WikiGroup;
import org.jamwiki.model.WikiUser;
import org.jamwiki.utils.LinkUtil;
import org.jamwiki.utils.NamespaceHandler;
import org.jamwiki.utils.Pagination;
import org.jamwiki.utils.WikiLink;
import org.jamwiki.utils.WikiUtil;

public class GAEDataHandler implements DataHandler {
  /**
  *
  */
  private void addGroupMember(String username, Long groupId)
      throws DataAccessException {
    GroupMemberEntity group = new GroupMemberEntity(username, groupId);
    GroupMemberService.save(group);
  }

  @Override
  public boolean authenticate(String username, String password)
      throws DataAccessException {
    return UserService.isAuthenticated(username, password);
  }

  @Override
  public List<Category> getAllCategories(String virtualWiki,
      Pagination pagination) throws DataAccessException {
    return CategoryService.getAll(virtualWiki);
  }

  @Override
  public List<Role> getAllRoles() throws DataAccessException {
    List<RoleEntity> rs = RoleService.getAll();
    if (rs != null) {
      List<Role> roles = new ArrayList<Role>();
      for (int i = 0; i < rs.size(); i++) {
        roles.add(initRole(rs.get(i)));
      }
      return roles;
    }
    return null;
  }

  @Override
  public List<RoleMap> getRoleMapByLogin(String loginFragment)
      throws DataAccessException {
    if (StringUtils.isBlank(loginFragment)) {
      return new ArrayList<RoleMap>();
    }
    // Connection conn = null;
    // PreparedStatement stmt = null;
    // ResultSet rs = null;
    // try {
    // conn = DatabaseConnection.getConnection();
    // stmt = conn.prepareStatement(STATEMENT_SELECT_AUTHORITIES_LOGIN);
    // loginFragment = '%' + loginFragment.toLowerCase() + '%';
    // stmt.setString(1, loginFragment);
    // rs = stmt.executeQuery();
    // WikiUserService.getAll();
    LinkedHashMap<Long, RoleMap> roleMaps = new LinkedHashMap<Long, RoleMap>();
    // while (rs.next()) {
    // Long userId = rs.getInt("wiki_user_id");
    // RoleMap roleMap = new RoleMap();
    // if (roleMaps.containsKey(userId)) {
    // roleMap = roleMaps.get(userId);
    // } else {
    // roleMap.setUserId(userId);
    // roleMap.setUserLogin(rs.getString("username"));
    // }
    // roleMap.addRole(rs.getString("authority"));
    // roleMaps.put(userId, roleMap);
    // }
    return new ArrayList<RoleMap>(roleMaps.values());
    // } finally {
    // DatabaseConnection.closeConnection(conn, stmt, rs);
    // }
  }

  @Override
  public List<RoleMap> getRoleMapByRole(String roleName)
      throws DataAccessException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Role> getRoleMapGroup(String groupName)
      throws DataAccessException {
    List<GroupAuthorityEntity> list = GroupAuthorityService
        .getByGroupname(groupName);
    if (list != null) {
      List<Role> roles = new ArrayList<Role>();
      RoleImpl role;
      for (GroupAuthorityEntity entity : list) {
        role = new RoleImpl(entity.getAuthority());
        role.setDescription(entity.getGroupName());
        roles.add(role);
      }
      return roles;
    }
    return null;
  }

  @Override
  public List<RoleMap> getRoleMapGroups() throws DataAccessException {
    LinkedHashMap<Long, RoleMap> roleMaps = new LinkedHashMap<Long, RoleMap>();
    List<GroupAuthorityEntity> list = GroupAuthorityService.getAll();
    for (GroupAuthorityEntity groupAuthorityEntity : list) {
      Long groupId = groupAuthorityEntity.getGroupId();
      RoleMap roleMap = new RoleMap();
      if (roleMaps.containsKey(groupId)) {
        roleMap = roleMaps.get(groupId);
      } else {
        roleMap.setGroupId(groupId);
        roleMap.setGroupName(groupAuthorityEntity.getGroupName());
      }
      roleMap.addRole(groupAuthorityEntity.getAuthority());
      roleMaps.put(groupId, roleMap);

    }
    return new ArrayList<RoleMap>(roleMaps.values());
  }

  @Override
  public List<Role> getRoleMapUser(String login) throws DataAccessException {
    // TODO Auto-generated method stub
    List<AuthorityEntity> list = AuthorityService.findByName(login);
    if (list != null) {
      RoleImpl roleImpl;
      List<Role> roles = new ArrayList<Role>();
      for (AuthorityEntity authorityEntity : list) {
        roleImpl = new RoleImpl(authorityEntity.getAuthority());
        roleImpl.setDescription(roleImpl.getDescription());
      }
      return roles;
    }
    return null;
  }

  @Override
  public List<VirtualWiki> getVirtualWikiList() throws DataAccessException {
    List<VirtualWiki> results = VirualWikiService.getAll();
    if (results == null || results.size() == 0) {
      // TODO allow multiple virtual wikis
      VirtualWiki vw = new VirtualWiki(WikiBase.DEFAULT_VWIKI, Environment
          .getValue(Environment.PROP_BASE_DEFAULT_TOPIC));
      ArrayList<VirtualWiki> list = new ArrayList<VirtualWiki>();
      list.add(vw);
      return list;
    }
    return results;
  }

  private Role initRole(RoleEntity rs) {
    Role role = new RoleImpl(rs.getName());
    role.setDescription(rs.getDescription());
    return role;
  }

  @Override
  public List<Category> lookupCategoryTopics(String virtualWiki,
      String categoryName) throws DataAccessException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Topic lookupTopic(String virtualWiki, String topicName,
      boolean deleteOK, Object transactionObject) {
    // TODO check this

    // if (StringUtils.isBlank(virtualWiki) || StringUtils.isBlank(topicName)) {
    if (StringUtils.isBlank(topicName)) {
      return null;
    }
    // String key = WikiCache.key(virtualWikiName, topicName);
    // if (transactionObject == null) {
    // // retrieve topic from the cache only if this call is not currently a
    // part
    // // of a transaction to avoid retrieving data that might have been updated
    // // as part of this transaction and would thus now be out of date
    // Element cacheElement = WikiCache.retrieveFromCache(CACHE_TOPICS, key);
    // if (cacheElement != null) {
    // Topic cacheTopic = (Topic)cacheElement.getObjectValue();
    // return (cacheTopic == null || (!deleteOK && cacheTopic.getDeleteDate() !=
    // null)) ? null : new Topic(cacheTopic);
    // }
    // }
    WikiLink wikiLink = LinkUtil.parseWikiLink(topicName);
    String namespace = wikiLink.getNamespace();
    boolean caseSensitive = true;
    if (namespace != null) {
      if (namespace.equals(NamespaceHandler.NAMESPACE_SPECIAL)) {
        // invalid namespace
        return null;
      }
      if (namespace.equals(NamespaceHandler.NAMESPACE_TEMPLATE)
          || namespace.equals(NamespaceHandler.NAMESPACE_USER)
          || namespace.equals(NamespaceHandler.NAMESPACE_CATEGORY)) {
        // user/template/category namespaces are case-insensitive
        caseSensitive = false;
      }
    }
    // Topic topic = new Topic();
    // topic.setAdminOnly(false);
    // topic.setName(topicName);
    // topic.setVirtualWiki(virtualWikiName);
    // long currentVersionId = 0;
    // if (currentVersionId > 0) {
    // topic.setCurrentVersionId(currentVersionId);
    // }
    return PageService.findByTitle(topicName);
  }

  @Override
  public VirtualWiki lookupVirtualWiki(String virtualWikiName)
      throws DataAccessException {
    // TODO allow multiple virtual wikis
    return new VirtualWiki(WikiBase.DEFAULT_VWIKI, Environment
        .getValue(Environment.PROP_BASE_DEFAULT_TOPIC));
  }

  /**
   * Retrieve a WikiGroup object for a given group name.
   * 
   * @param groupName
   *          The group name for the group being queried.
   * @return The WikiGroup object for the given group name, or <code>null</code>
   *         if no matching group exists.
   * @throws DataAccessException
   *           Thrown if any error occurs during method execution.
   */
  @Override
  public WikiGroup lookupWikiGroup(String groupName) throws DataAccessException {
    return WikiGroupService.findByName(groupName);
  }

  @Override
  public WikiUser lookupWikiUser(Long userId)
      throws org.jamwiki.DataAccessException {
    return WikiUserService.findById(userId);
  }

  @Override
  public WikiUser lookupWikiUser(String username)
      throws org.jamwiki.DataAccessException {
    return WikiUserService.findByName(username);
  }

  @Override
  public int lookupWikiUserCount() throws org.jamwiki.DataAccessException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String lookupWikiUserEncryptedPassword(String username)
      throws org.jamwiki.DataAccessException {
    WikiUserDetails userDetails = WikiUserDetailsService.findByName(username);
    if (userDetails == null) {
      return null;
    }
    return userDetails.getPassword();
  }

  @Override
  public List<String> lookupWikiUsers(Pagination pagination)
      throws org.jamwiki.DataAccessException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setup(Locale locale, WikiUser user, String username,
      String encryptedPassword) throws DataAccessException, WikiException {
    WikiDatabase.initialize();

    WikiDatabase.setup(locale, user, username, encryptedPassword);
  }

  @Override
  public void writeRole(Role role, boolean update) throws DataAccessException,
      WikiException {
    this.validateRole(role);
    RoleEntity re = new RoleEntity();
    re.setDescription(role.getDescription());
    re.setName(role.getAuthority());
    // if (update) {
    // RoleService.update(re);
    // } else {
    RoleService.save(re);
    // }

  }

  @Override
  public void writeRoleMapGroup(Long groupId, String groupName,
      List<String> roles) throws DataAccessException, WikiException {
    try {
      // status = DatabaseConnection.startTransaction();
      GroupAuthorityService.deleteByGroupId(groupId);
      for (String authority : roles) {
        this.validateAuthority(authority);
        // this.queryHandler().insertUserAuthority(username, authority, conn);
        GroupAuthorityService.save(new GroupAuthorityEntity(groupId, groupName,
            authority));
      }
      // refresh the current role requirements
      JAMWikiAuthenticationConfiguration.resetJamwikiAnonymousAuthorities();
      JAMWikiAuthenticationConfiguration.resetDefaultGroupRoles();
    } catch (WikiException e) {
      // DatabaseConnection.rollbackOnException(status, e);
      throw e;
    }
  }

  @Override
  public void writeRoleMapUser(String username, List<String> roles)
      throws DataAccessException, WikiException {
    try {
      // status = DatabaseConnection.startTransaction();
      AuthorityService.deleteByName(username);
      for (String authority : roles) {
        this.validateAuthority(authority);
        // this.queryHandler().insertUserAuthority(username, authority, conn);
        AuthorityService.save(new AuthorityEntity(username, authority));
      }
    } catch (WikiException e) {
      // DatabaseConnection.rollbackOnException(status, e);
      throw e;
    }
  }

  @Override
  public void writeTopic(Topic topic, TopicVersion topicVersion,
      LinkedHashMap<String, String> categories, List<String> links)
      throws DataAccessException, WikiException {
    // TODO Auto-generated method stub
    PageService.save(topic, categories);
  }

  @Override
  public void writeVirtualWiki(VirtualWiki virtualWiki)
      throws DataAccessException, WikiException {
    VirualWikiService.save(virtualWiki);
  }

  @Override
  public void writeWikiGroup(WikiGroup group) throws DataAccessException,
      WikiException {
    WikiGroupService.save(group);
  }

  @Override
  public void writeWikiUser(WikiUser user, String username,
      String encryptedPassword) throws org.jamwiki.DataAccessException,
      WikiException {
    WikiUtil.validateUserName(user.getUsername());

    try {
      // status = DatabaseConnection.startTransaction();
      if (user.getUserId() == null) {
        WikiUserDetails userDetails = new WikiUserDetails(username,
            encryptedPassword, true, true, true, true,
            JAMWikiAuthenticationConfiguration.getDefaultGroupRoles());
        WikiUserDetailsService.save(userDetails);
        WikiUserService.save(user);
        // this.addWikiUser(user, conn);
        // add all users to the registered user group
        this.addGroupMember(user.getUsername(), WikiBase
            .getGroupRegisteredUser().getGroupId());
        // FIXME - reconsider this approach of separate entries for every
        // virtual wiki
        // List<VirtualWiki> virtualWikis = this.getVirtualWikiList();
        // for (VirtualWiki virtualWiki : virtualWikis) {
        // LogItem logItem = LogItem.initLogItem(user, virtualWiki.getName());
        // this.addLogItem(logItem, conn);
        // RecentChange change = RecentChange.initRecentChange(logItem);
        // this.addRecentChange(change, conn);
        // }
      } else {
        if (!StringUtils.isBlank(encryptedPassword)) {
          WikiUserDetails userDetails = new WikiUserDetails(username,
              encryptedPassword, true, true, true, true,
              JAMWikiAuthenticationConfiguration.getDefaultGroupRoles());
          // this.updateUserDetails(userDetails, conn);
        }
        // this.updateWikiUser(user, conn);
        WikiUserService.update(user);
      }
      // } catch (DataAccessException e) {
      // DatabaseConnection.rollbackOnException(status, e);
      // throw e;
    } catch (WikiException e) {
      // DatabaseConnection.rollbackOnException(status, e);
      throw e;
    }
  }

  /**
  *
  */
  private static void checkLength(String value, int maxLength)
      throws WikiException {
    if (value != null && value.length() > maxLength) {
      throw new WikiException(new WikiMessage("error.fieldlength", value,
          Integer.valueOf(maxLength).toString()));
    }
  }

  /**
  *
  */
  protected void validateAuthority(String role) throws WikiException {
    checkLength(role, 30);
  }

  /**
  *
  */
  protected void validateCategory(Category category) throws WikiException {
    checkLength(category.getName(), 200);
    checkLength(category.getSortKey(), 200);
  }

  /**
  *
  */
  // protected void validateLogItem(LogItem logItem) throws WikiException {
  // checkLength(logItem.getUserDisplayName(), 200);
  // checkLength(logItem.getLogParamString(), 500);
  // logItem.setLogComment(StringUtils.substring(logItem.getLogComment(), 0,
  // 200));
  // }

  /**
  *
  */
  // protected void validateRecentChange(RecentChange change) throws
  // WikiException {
  // checkLength(change.getTopicName(), 200);
  // checkLength(change.getAuthorName(), 200);
  // checkLength(change.getVirtualWiki(), 100);
  // change.setChangeComment(StringUtils.substring(change.getChangeComment(), 0,
  // 200));
  // checkLength(change.getParamString(), 500);
  // }

  /**
  *
  */
  protected void validateRole(Role role) throws WikiException {
    checkLength(role.getAuthority(), 30);
    role.setDescription(StringUtils.substring(role.getDescription(), 0, 200));
  }

  /**
  *
  */
  protected void validateTopic(Topic topic) throws WikiException {
    checkLength(topic.getName(), 200);
    checkLength(topic.getRedirectTo(), 200);
  }

  /**
  *
  */
  protected void validateTopicVersion(TopicVersion topicVersion)
      throws WikiException {
    checkLength(topicVersion.getAuthorDisplay(), 100);
    checkLength(topicVersion.getVersionParamString(), 500);
    topicVersion.setEditComment(StringUtils.substring(topicVersion
        .getEditComment(), 0, 200));
  }

  /**
  *
  */
  protected void validateUserDetails(WikiUserDetails userDetails)
      throws WikiException {
    checkLength(userDetails.getUsername(), 100);
    // do not throw exception containing password info
    if (userDetails.getPassword() != null
        && userDetails.getPassword().length() > 100) {
      throw new WikiException(new WikiMessage("error.fieldlength", "-", "100"));
    }
  }

  /**
  *
  */
  protected void validateVirtualWiki(VirtualWiki virtualWiki)
      throws WikiException {
    checkLength(virtualWiki.getName(), 100);
    checkLength(virtualWiki.getDefaultTopicName(), 200);
  }

  /**
  *
  */
  protected void validateWatchlistEntry(String topicName) throws WikiException {
    checkLength(topicName, 200);
  }

  /**
  *
  */
  // protected void validateWikiFile(WikiFile wikiFile) throws WikiException {
  // checkLength(wikiFile.getFileName(), 200);
  // checkLength(wikiFile.getUrl(), 200);
  // checkLength(wikiFile.getMimeType(), 100);
  // }

  /**
  *
  */
  // protected void validateWikiFileVersion(WikiFileVersion wikiFileVersion)
  // throws WikiException {
  // checkLength(wikiFileVersion.getUrl(), 200);
  // checkLength(wikiFileVersion.getMimeType(), 100);
  // checkLength(wikiFileVersion.getAuthorDisplay(), 100);
  // wikiFileVersion.setUploadComment(StringUtils.substring(wikiFileVersion.getUploadComment(),
  // 0, 200));
  // }

  /**
  *
  */
  protected void validateWikiGroup(WikiGroup group) throws WikiException {
    checkLength(group.getName(), 30);
    group.setDescription(StringUtils.substring(group.getDescription(), 0, 200));
  }

  /**
  *
  */
  protected void validateWikiUser(WikiUser user) throws WikiException {
    checkLength(user.getUsername(), 100);
    checkLength(user.getDisplayName(), 100);
    checkLength(user.getCreateIpAddress(), 39);
    checkLength(user.getLastLoginIpAddress(), 39);
    checkLength(user.getDefaultLocale(), 8);
    checkLength(user.getEmail(), 100);
    checkLength(user.getEditor(), 50);
    checkLength(user.getSignature(), 255);
  }
}
