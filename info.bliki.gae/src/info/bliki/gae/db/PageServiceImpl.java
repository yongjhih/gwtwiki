package info.bliki.gae.db;

import info.bliki.gae.model.Page;

import java.util.Collections;
import java.util.List;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import org.springframework.stereotype.Repository;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.OQuery;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;

@Repository
public class PageServiceImpl implements PageService {
  public static Cache cache = null;

  static {
    try {
      ObjectifyFactory.register(Page.class);
      CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
      cache = cacheFactory.createCache(Collections.emptyMap());
    } catch (CacheException e) {
      // ...
      // e.printStackTrace();
    }
  }

  @Override
  public Page save(Page page) {
    Objectify ofy = ObjectifyFactory.begin();
    ofy.put(page);
    return page;
  }

  @Override
  public Page update(Page page) {
    Page existingEntity = null;
    try {
      Objectify ofy = ObjectifyFactory.begin();
      existingEntity = ofy.get(Page.class, page.getTitle());
      existingEntity.setTitle(page.getTitle());
      existingEntity.setContent(page.getContent());
      ofy.put(existingEntity);
      cache.put(existingEntity.getTitle(), existingEntity);
    } catch (EntityNotFoundException enf) {
    }
    return existingEntity;
  }

  @Override
  public void delete(Page page) {
    cache.remove(page.getTitle());
    Objectify ofy = ObjectifyFactory.begin();
    ofy.delete(page);
  }

  @Override
  public Page findByTitle(String title) {
    Page page = (Page) cache.get(title);
    if (page != null) {
      return page;
    }
    try {
      Objectify ofy = ObjectifyFactory.begin();
      OQuery<Page> q = ObjectifyFactory.createQuery(Page.class);
      q.filter("title", title);
      page = ofy.prepare(q).asSingle();
      cache.put(page.getTitle(), page);
      return page;
    } catch (NullPointerException npe) {
    }
    return null;
  }

  @Override
  public String getHTMLContent(String title) {
    Page page = findByTitle(title);
    if (page != null) {
      return page.getHtmlContent();
    }
    return "";
  }

  public List<Page> getAll() {
    List<Page> resultList = null;
    Objectify ofy = ObjectifyFactory.begin();
    OQuery<Page> q = ObjectifyFactory.createQuery(Page.class);
    resultList = ofy.prepare(q).asList();
    return resultList;
  }
}
