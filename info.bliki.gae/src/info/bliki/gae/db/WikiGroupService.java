package info.bliki.gae.db;

import java.util.Collections;
import java.util.List;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import org.jamwiki.model.OS;
import org.jamwiki.model.WikiGroup;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.OQuery;
import com.googlecode.objectify.Objectify;

public class WikiGroupService {
  public static Cache cache = null;

  static {
    try {
      CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
      cache = cacheFactory.createCache(Collections.emptyMap());
    } catch (CacheException e) {
      // ...
      // e.printStackTrace();
    }
  }

  public static WikiGroup save(WikiGroup page) {
    Objectify ofy = OS.begin();
    ofy.put(page);
    return page;
  }

  public static WikiGroup update(WikiGroup role) {
    WikiGroup existingEntity = null;
    try {
      Objectify ofy = OS.begin();
      existingEntity = ofy.get(WikiGroup.class, role.getGroupId());
      existingEntity.setName(role.getName());
      existingEntity.setDescription(role.getDescription());
      ofy.put(existingEntity);
      cache.put(existingEntity.getName(), existingEntity);
    } catch (EntityNotFoundException enf) {
    }
    return existingEntity;
  }

  public static void delete(WikiGroup role) {
    cache.remove(role.getName());
    Objectify ofy = OS.begin();
    ofy.delete(role);
  }

  public static WikiGroup findByName(String name) {
    WikiGroup role = (WikiGroup) cache.get(name);
    if (role != null) {
      return role;
    }
    try {
      Objectify ofy = OS.begin();
      OQuery<WikiGroup> q = OS.createQuery(WikiGroup.class);
      q.filter("name", name);
      role = ofy.prepare(q).asSingle();
      cache.put(role.getName(), role);
      return role;
    } catch (NullPointerException npe) {
    }
    return null;
  }

  public static List<WikiGroup> getAll() {
    List<WikiGroup> resultList = null;
    Objectify ofy = OS.begin();
    OQuery<WikiGroup> q = OS.createQuery(WikiGroup.class);
    resultList = ofy.prepare(q).asList();
    return resultList;
  }

}
