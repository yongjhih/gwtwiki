package info.bliki.gae.db;

import info.bliki.gae.model.RoleEntity;

import java.util.Collections;
import java.util.List;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import org.jamwiki.model.OS;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.OQuery;
import com.googlecode.objectify.Objectify;

public class RoleService {
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

  public static RoleEntity save(RoleEntity page) {
    Objectify ofy = OS.begin();
    ofy.put(page);
    return page;
  }

  // public static RoleEntity update(RoleEntity role) {
  // RoleEntity existingEntity = null;
  // try {
  // Objectify ofy = OS.begin();
  // existingEntity = ofy.get(RoleEntity.class, role.getName());
  // existingEntity.setDescription(role.getDescription());
  // ofy.put(existingEntity);
  // cache.put(existingEntity.getName(), existingEntity);
  // } catch (EntityNotFoundException enf) {
  // }
  // return existingEntity;
  // }

  public static void delete(RoleEntity role) {
    cache.remove(role.getName());
    Objectify ofy = OS.begin();
    ofy.delete(role);
  }

  public static RoleEntity findByName(String name) {
    RoleEntity role = (RoleEntity) cache.get(name);
    if (role != null) {
      return role;
    }
    try {
      Objectify ofy = OS.begin();
      OQuery<RoleEntity> q = OS.createQuery(RoleEntity.class);
      q.filter("name", name);
      role = ofy.prepare(q).asSingle();
      cache.put(role.getName(), role);
      return role;
    } catch (NullPointerException npe) {
    }
    return null;
  }

  public static List<RoleEntity> getAll() {
    List<RoleEntity> resultList = null;
    Objectify ofy = OS.begin();
    OQuery<RoleEntity> q = OS.createQuery(RoleEntity.class);
    resultList = ofy.prepare(q).asList();
    return resultList;
  }

}
