package info.bliki.gae.db;

import java.util.Collections;
import java.util.List;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import org.jamwiki.model.OS;
import org.jamwiki.model.VirtualWiki;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.OQuery;
import com.googlecode.objectify.Objectify;

public class VirualWikiService {
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

  public static VirtualWiki save(VirtualWiki page) {
    Objectify ofy = OS.begin();
    ofy.put(page);
    return page;
  }

  public static VirtualWiki update(VirtualWiki role) {
    VirtualWiki existingEntity = null;
    try {
      Objectify ofy = OS.begin();
      existingEntity = ofy.get(VirtualWiki.class, role.getVirtualWikiId());
      existingEntity.setName(role.getName());
      existingEntity.setDefaultTopicName(role.getDefaultTopicName());
      ofy.put(existingEntity);
      cache.put(existingEntity.getName(), existingEntity);
    } catch (EntityNotFoundException enf) {
    }
    return existingEntity;
  }

  public static void delete(VirtualWiki role) {
    cache.remove(role.getName());
    Objectify ofy = OS.begin();
    ofy.delete(role);
  }

  public static VirtualWiki findByName(String name) {
    VirtualWiki role = (VirtualWiki) cache.get(name);
    if (role != null) {
      return role;
    }
    try {
      Objectify ofy = OS.begin();
      OQuery<VirtualWiki> q = OS.createQuery(VirtualWiki.class);
      q.filter("title", name);
      role = ofy.prepare(q).asSingle();
      cache.put(role.getName(), role);
      return role;
    } catch (NullPointerException npe) {
    }
    return null;
  }

  public static List<VirtualWiki> getAll() {
    List<VirtualWiki> resultList = null;
    Objectify ofy = OS.begin();
    OQuery<VirtualWiki> q = OS.createQuery(VirtualWiki.class);
    resultList = ofy.prepare(q).asList();
    return resultList;
  }

}
