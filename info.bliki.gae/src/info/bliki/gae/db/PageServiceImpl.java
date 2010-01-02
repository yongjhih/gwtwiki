package info.bliki.gae.db;

import info.bliki.gae.model.Page;

import java.util.Collections;
import java.util.List;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.stereotype.Repository;

@Repository
public class PageServiceImpl implements PageService {
  PersistenceManager pm = null;

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

  @Override
  public Page save(Page page) {
    try {
      pm = PMF.get().getPersistenceManager();
      page = pm.makePersistent(page);
    } finally {
      pm.close();
    }
    return page;
  }

  @Override
  public Page update(Page page) {
    Page existingEntity = null; 
    try {
      pm = PMF.get().getPersistenceManager();
      existingEntity = pm.makePersistent(page);
      existingEntity.setTitle(page.getTitle());
      existingEntity.setContent(page.getContent());
      Page detachedPage = pm.detachCopy(existingEntity);
      cache.put(detachedPage.getTitle(), detachedPage);
    } finally {
      pm.close();
    }
    return existingEntity;
  }

  @Override
  public void delete(Page page) {
    try {
      cache.remove(page.getTitle());
      pm = PMF.get().getPersistenceManager();
      pm.deletePersistent(page);
    } finally {
      pm.close();
    }
  }

  @Override
  public Page findByKey(Long key) {
    Page detachedPage = null, object = null;
    try {
      pm = PMF.get().getPersistenceManager();
      object = pm.getObjectById(Page.class, key);
      detachedPage = pm.detachCopy(object);
      cache.put(detachedPage.getTitle(), detachedPage);
    } catch (JDOObjectNotFoundException e) {
      return null;
    } finally {
      pm.close();
    }
    return detachedPage;

  }

  @Override
  public Page findByTitle(String topicName) {
    Page page = (Page) cache.get(topicName);
    if (page != null) {
      return page;
    }
    try {
      pm = PMF.get().getPersistenceManager();

      Query query = pm.newQuery(Page.class);
      query.setFilter("title == topicName");
      query.declareParameters("String topicName");
      try {
        List<Page> results = (List<Page>) query.execute(topicName);
        if (results.iterator().hasNext()) {
          for (Page e : results) {
            Page detachedPage = pm.detachCopy(e);
            cache.put(detachedPage.getTitle(), detachedPage);
            return e;
          }
        } else {
          // ... no results ...
        }
      } finally {
        query.closeAll();
      }
      return null;
    } catch (JDOObjectNotFoundException e) {
      return null;
    } finally {
      pm.close();
    }
  }

  public List<Page> getAll() {
    List<Page> object = null;
    try {
      String query = "select from " + Page.class.getName();
      pm = PMF.get().getPersistenceManager();
      object = (List<Page>) pm.newQuery(query).execute();
      object.size();
      // detachedCopy = pm.detachCopy(object);
      return object;
    } finally {
      pm.close();
    }
  }
}
