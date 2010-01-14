package info.bliki.gae.db;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.jamwiki.DataAccessException;
import org.jamwiki.DataHandler;
import org.jamwiki.WikiException;
import org.jamwiki.model.Category;
import org.jamwiki.model.Role;
import org.jamwiki.model.RoleMap;
import org.jamwiki.model.Topic;
import org.jamwiki.model.VirtualWiki;
import org.jamwiki.model.WikiUser;
import org.jamwiki.utils.LinkUtil;
import org.jamwiki.utils.NamespaceHandler;
import org.jamwiki.utils.Pagination;
import org.jamwiki.utils.WikiLink;

public class GAEDataHandler implements DataHandler {

  @Override
  public List<Category> getAllCategories(String virtualWiki,
      Pagination pagination) throws DataAccessException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<RoleMap> getRoleMapByLogin(String loginFragment)
      throws DataAccessException {
    // TODO Auto-generated method stub
    return null;
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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<RoleMap> getRoleMapGroups() throws DataAccessException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Role> getRoleMapUser(String login) throws DataAccessException {
    // TODO Auto-generated method stub
    return null;
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
    // TODO Auto-generated method stub
    return new VirtualWiki();
  }

  @Override
  public WikiUser lookupWikiUser(int userId)
      throws org.jamwiki.DataAccessException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public WikiUser lookupWikiUser(String username)
      throws org.jamwiki.DataAccessException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int lookupWikiUserCount() throws org.jamwiki.DataAccessException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String lookupWikiUserEncryptedPassword(String username)
      throws org.jamwiki.DataAccessException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<String> lookupWikiUsers(Pagination pagination)
      throws org.jamwiki.DataAccessException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setup(Locale locale, WikiUser user, String username,
      String encryptedPassword) {
    // TODO Auto-generated method stub

  }

  @Override
  public void writeWikiUser(WikiUser user, String username,
      String encryptedPassword) throws org.jamwiki.DataAccessException,
      WikiException {
    // TODO Auto-generated method stub
    
  }

}
