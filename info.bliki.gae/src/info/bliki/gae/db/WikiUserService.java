package info.bliki.gae.db;

import java.util.Collections;
import java.util.List;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import org.jamwiki.model.OS;
import org.jamwiki.model.WikiUser;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.OQuery;
import com.googlecode.objectify.Objectify;



public class WikiUserService {
  public static Cache WIKIUSER_CACHE = null;

  static {
    try {
      CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
      WIKIUSER_CACHE = cacheFactory.createCache(Collections.emptyMap());
    } catch (CacheException e) {
      // ...
      // e.printStackTrace();
    }
  }

  public static WikiUser save(WikiUser wikiUser) {
    Objectify ofy = OS.begin();
    ofy.put(wikiUser);
    WIKIUSER_CACHE.put(wikiUser.getUsername(), wikiUser);
    return wikiUser;
  }

  public static WikiUser update(WikiUser wikiUser) {
    WikiUser existingEntity = null;
    try {

      Objectify ofy = OS.begin();
      existingEntity = ofy.get(WikiUser.class, wikiUser.getUserId());
      WIKIUSER_CACHE.remove(existingEntity.getUsername());
      existingEntity.setEmail(wikiUser.getEmail());
      existingEntity.setUsername(wikiUser.getUsername());
      existingEntity.setGAEUser(wikiUser.getGAEUser());
      ofy.put(existingEntity);
      WIKIUSER_CACHE.put(existingEntity.getUsername(), existingEntity);
    } catch (EntityNotFoundException enf) {
    }
    return existingEntity;
  }

  public static void delete(WikiUser wikiUser) {
    WIKIUSER_CACHE.remove(wikiUser.getUserId());
    Objectify ofy = OS.begin();
    ofy.delete(wikiUser);
  }

  public static WikiUser findById(Long userId) {
    if (userId==null) {
      return null;
    }
    Objectify ofy = OS.begin();
    return ofy.find(WikiUser.class, userId);
  }

  public static List<WikiUser> findByFragment(String usernameFragment) {
    List<WikiUser> resultList = null;
    Objectify ofy = OS.begin();
    OQuery<WikiUser> q = OS.createQuery(WikiUser.class);
    q.filter("username in", usernameFragment);
    resultList = ofy.prepare(q).asList();
    return resultList;
  }
  
  public static WikiUser findByName(String username) {
    WikiUser wikiUser = (WikiUser) WIKIUSER_CACHE.get(username);
    if (wikiUser != null) {
      return wikiUser;
    }
    try {
      Objectify ofy = OS.begin();
      OQuery<WikiUser> q = OS.createQuery(WikiUser.class);
      q.filter("username", username);
      wikiUser = ofy.prepare(q).asSingle();
      WIKIUSER_CACHE.put(wikiUser.getUsername(), wikiUser);
      return wikiUser;
    } catch (NullPointerException npe) {
    }
    return null;
  }
  
  public static WikiUser findByEMail(String email) {
    WikiUser wikiUser = null;
    try {
      Objectify ofy = OS.begin();
      OQuery<WikiUser> q = OS.createQuery(WikiUser.class);
      q.filter("email", email);
      wikiUser = ofy.prepare(q).asSingle();
      WIKIUSER_CACHE.put(wikiUser.getUsername(), wikiUser);
      return wikiUser;
    } catch (NullPointerException npe) {
    }
    return null;
  }

  /**
   * get the wiki user from the database. If it not exist, create a new wiki
   * user from the google appengine user.
   * 
   * @return
   */
  public static WikiUser getWikiUser() {
    final UserService userService = UserServiceFactory.getUserService();
    final User gaeUser = userService.getCurrentUser();
    if (gaeUser != null) {
      String email;
      email = gaeUser.getEmail();
      WikiUser wikiUser = (WikiUser) WIKIUSER_CACHE.get(email);
      if (wikiUser != null) {
        return wikiUser;
      }
      Objectify ofy = OS.begin();
      try {
        OQuery<WikiUser> q = OS.createQuery(WikiUser.class);
        q.filter("email", email);
        wikiUser = ofy.prepare(q).asSingle();
        WIKIUSER_CACHE.put(wikiUser.getUsername(), wikiUser);
        return wikiUser;
      } catch (NullPointerException npe) {
      }

      if (wikiUser == null) {
        wikiUser = new WikiUser();
        wikiUser.setGAEUser(gaeUser);
        wikiUser.setEmail(email);
        String username = gaeUser.getNickname();
        if (username == null) {
          username = email;
        }
        wikiUser.setUsername(username);
        ofy.put(wikiUser);
        WIKIUSER_CACHE.put(wikiUser.getUsername(), wikiUser);
      }
    }
    return null;
  }

  public static List<WikiUser> getAll() {
    List<WikiUser> resultList = null;
    Objectify ofy = OS.begin();
    OQuery<WikiUser> q = OS.createQuery(WikiUser.class);
    resultList = ofy.prepare(q).asList();
    return resultList;
  }
}
// WikiUser save(WikiUser user);
//
// WikiUser update(WikiUser user);
//
// void delete(WikiUser user);
//
// WikiUser findByEMail(String email);
//
// List<WikiUser> getAll();
// }
