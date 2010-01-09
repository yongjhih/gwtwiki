package info.bliki.gae.db;


import java.util.Collections;
import java.util.List;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import org.jamwiki.model.Topic;
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
      ObjectifyFactory.register(Topic.class);
      CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
      cache = cacheFactory.createCache(Collections.emptyMap());
    } catch (CacheException e) {
      // ...
      // e.printStackTrace();
    }
  }

  @Override
  public Topic save(Topic page) {
    Objectify ofy = ObjectifyFactory.begin();
    ofy.put(page);
    return page;
  }

  @Override
  public Topic update(Topic page) {
    Topic existingEntity = null;
    try {
      Objectify ofy = ObjectifyFactory.begin();
      existingEntity = ofy.get(Topic.class, page.getName());
      existingEntity.setName(page.getName());
      existingEntity.setTopicContent(page.getTopicContent());
      ofy.put(existingEntity);
      cache.put(existingEntity.getName(), existingEntity);
    } catch (EntityNotFoundException enf) {
    }
    return existingEntity;
  }

  @Override
  public void delete(Topic page) {
    cache.remove(page.getName());
    Objectify ofy = ObjectifyFactory.begin();
    ofy.delete(page);
  }

  @Override
  public Topic findByTitle(String title) {
    Topic page = (Topic) cache.get(title);
    if (page != null) {
      return page;
    }
    try {
      Objectify ofy = ObjectifyFactory.begin();
      OQuery<Topic> q = ObjectifyFactory.createQuery(Topic.class);
      q.filter("title", title);
      page = ofy.prepare(q).asSingle();
      cache.put(page.getName(), page);
      return page;
    } catch (NullPointerException npe) {
    }
    return null;
  }

  @Override
  public String getHTMLContent(String title) {
    Topic page = findByTitle(title);
    if (page != null) {
      return page.getHtmlContent();
    }
    return "";
  }

  public List<Topic> getAll() {
    List<Topic> resultList = null;
    Objectify ofy = ObjectifyFactory.begin();
    OQuery<Topic> q = ObjectifyFactory.createQuery(Topic.class);
    resultList = ofy.prepare(q).asList();
    return resultList;
  }
}
