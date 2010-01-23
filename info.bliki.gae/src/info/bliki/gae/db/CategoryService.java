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

  public static Category update(Category category) {
    Category existingEntity = null;
    try {
      Objectify ofy = OS.begin();
      existingEntity = ofy.get(Category.class, category.getName());
      existingEntity.setChildTopicName(category.getChildTopicName());
      existingEntity.setSortKey(category.getSortKey());
      existingEntity.setVirtualWiki(category.getVirtualWiki());
      existingEntity.setTopicType(category.getTopicType());
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
    Category category = (Category) cache.get(name);
    if (category != null) {
      return category;
    }
    try {
      Objectify ofy = OS.begin();
      OQuery<Category> q = OS.createQuery(Category.class);
      q.filter("title", name);
      category = ofy.prepare(q).asSingle();
      cache.put(category.getName(), category);
      return category;
    } catch (NullPointerException npe) {
    }
    return null;
  }

  public static List<Category> getAll(String virtualWiki) {
    List<Category> resultList = null;
    Objectify ofy = OS.begin();
    OQuery<Category> q = OS.createQuery(Category.class);
    q.filter("virtualWiki", virtualWiki);
    resultList = ofy.prepare(q).asList();
    return resultList;
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
