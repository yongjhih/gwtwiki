package info.bliki.gae.db;

import info.bliki.gae.model.PropertyEntity;

import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.jamwiki.model.OS;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.OQuery;
import com.googlecode.objectify.Objectify;

public class PropertyService {

  public static PropertyEntity save(PropertyEntity page) {
    Objectify ofy = OS.begin();
    ofy.put(page);
    return page;
  }

  public static void delete(PropertyEntity property) {
    Objectify ofy = OS.begin();
    ofy.delete(property);
  }

  public static PropertyEntity findByKey(String key) {
    try {
      Objectify ofy = OS.begin();
      return ofy.get(PropertyEntity.class, key);
    } catch (EntityNotFoundException enf) {
    }
    return null;
  }

  public static List<PropertyEntity> getAll() {
    List<PropertyEntity> resultList = null;
    Objectify ofy = OS.begin();
    OQuery<PropertyEntity> q = OS.createQuery(PropertyEntity.class);
    resultList = ofy.prepare(q).asList();
    return resultList;
  }

  public static void saveAll(Properties properties) {
    PropertyEntity page;
    Objectify ofy = OS.begin();

    Enumeration<Object> keyEnum = properties.keys();
    while (keyEnum.hasMoreElements()) {
      String key = (String) keyEnum.nextElement();
      String value = (String) properties.get(key.toString());
      if (value != null) {
        page = new PropertyEntity(key, value);
        ofy.put(page);
      }
    }
  }
}
