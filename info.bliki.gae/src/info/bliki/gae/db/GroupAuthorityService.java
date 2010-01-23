package info.bliki.gae.db;

import info.bliki.gae.model.GroupAuthorityEntity;

import java.util.List;

import org.jamwiki.model.OS;
import org.jamwiki.model.WikiGroup;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.OQuery;
import com.googlecode.objectify.Objectify;

public class GroupAuthorityService {

  public static GroupAuthorityEntity save(GroupAuthorityEntity page) {
    Objectify ofy = OS.begin();
    ofy.put(page);
    return page;
  }

  public static GroupAuthorityEntity update(GroupAuthorityEntity role) {
    GroupAuthorityEntity existingEntity = null;
    try {
      Objectify ofy = OS.begin();
      existingEntity = ofy.get(GroupAuthorityEntity.class, role
          .getGroupAuthorityId());
      existingEntity.setGroupId(role.getGroupId());
      existingEntity.setAuthority(role.getAuthority());
      ofy.put(existingEntity);
    } catch (EntityNotFoundException enf) {
    }
    return existingEntity;
  }

  public static void delete(GroupAuthorityEntity role) {
    Objectify ofy = OS.begin();
    ofy.delete(role);
  }

  public static void deleteByGroupId(Long groupId) {
    GroupAuthorityEntity role;
    try {
      Objectify ofy = OS.begin();
      OQuery<GroupAuthorityEntity> q = OS
          .createQuery(GroupAuthorityEntity.class);
      q.filter("groupId", groupId);
      Iterable<GroupAuthorityEntity> it = ofy.prepare(q).asIterable();
      for (GroupAuthorityEntity groupAuthorityEntity : it) {
        ofy.delete(groupAuthorityEntity);
      }
    } catch (NullPointerException npe) {
    }
  }

  public static List<GroupAuthorityEntity> getByGroupname(String groupName) {
    try {
      Objectify ofy = OS.begin();
      OQuery<GroupAuthorityEntity> q = OS
          .createQuery(GroupAuthorityEntity.class);
      q.filter("groupName", groupName);
      return ofy.prepare(q).asList();
    } catch (NullPointerException npe) {
    }
    return null;
  }

  public static List<GroupAuthorityEntity> getAll() {
    List<GroupAuthorityEntity> resultList = null;
    Objectify ofy = OS.begin();
    OQuery<GroupAuthorityEntity> q = OS.createQuery(GroupAuthorityEntity.class);
    resultList = ofy.prepare(q).asList();
    return resultList;
  }

}
