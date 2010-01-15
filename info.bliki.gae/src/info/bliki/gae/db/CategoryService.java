package info.bliki.gae.db;

import java.util.Collections;
import java.util.List;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import org.jamwiki.model.Category;
import org.jamwiki.model.OS;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.OQuery;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;

public class CategoryService {
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

  public static Category save(Category page) {
    Objectify ofy = OS.begin();
    ofy.put(page);
    return page;
  }

  public static Category update(Category page) {
    Category existingEntity = null;
    try {
      Objectify ofy = OS.begin();
      existingEntity = ofy.get(Category.class, page.getName());
      existingEntity.setName(page.getName());
      ofy.put(existingEntity);
      cache.put(existingEntity.getName(), existingEntity);
    } catch (EntityNotFoundException enf) {
    }
    return existingEntity;
  }

  public static void delete(Category page) {
    cache.remove(page.getName());
    Objectify ofy = OS.begin();
    ofy.delete(page);
  }

  public static Category findByName(String name) {
    Category page = (Category) cache.get(name);
    if (page != null) {
      return page;
    }
    try {
      Objectify ofy = OS.begin();
      OQuery<Category> q = OS.createQuery(Category.class);
      q.filter("title", name);
      page = ofy.prepare(q).asSingle();
      cache.put(page.getName(), page);
      return page;
    } catch (NullPointerException npe) {
    }
    return null;
  }

  public static List<Category> getAll() {
    List<Category> resultList = null;
    Objectify ofy = OS.begin();
    OQuery<Category> q = OS.createQuery(Category.class);
    resultList = ofy.prepare(q).asList();
    return resultList;
  }

  // Category save(Category page);
  //
  // Category update(Category page);
  //
  // void delete(Category page);
  //
  // Category findByTitle(String title);
  //
  // String getHTMLContent(String title);
  //
  // List<Category> getAll();
}
