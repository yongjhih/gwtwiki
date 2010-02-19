package info.bliki.gae.db;

import java.util.List;

import org.jamwiki.model.OS;
import org.jamwiki.model.Topic;
import org.jamwiki.model.TopicVersion;

import com.googlecode.objectify.OQuery;
import com.googlecode.objectify.Objectify;

public class TopicVersionService {

  public static TopicVersion save(TopicVersion version) {
    Objectify ofy = OS.begin();
    ofy.put(version);
    return version;
  }

  public static void delete(TopicVersion version) {
    Objectify ofy = OS.begin();
    ofy.delete(version);
  }

  public static TopicVersion findById(Long versionId) {
    if (versionId == null) {
      return null;
    }
    Objectify ofy = OS.begin();
    return ofy.find(TopicVersion.class, versionId);
  }

  public static List<TopicVersion> findByTopic(Topic topic) {
    Objectify ofy = OS.begin();
    OQuery<TopicVersion> q = OS.createQuery(TopicVersion.class);
    q.filter("topicId", OS.createKey(topic));
    return ofy.prepare(q).asList();
  }
  
  public static List<TopicVersion> getAll() {
    List<TopicVersion> resultList = null;
    Objectify ofy = OS.begin();
    OQuery<TopicVersion> q = OS.createQuery(TopicVersion.class);
    resultList = ofy.prepare(q).asList();
    return resultList;
  }

}
