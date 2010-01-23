package info.bliki.gae.db;

import info.bliki.gae.model.UserAuthorityEntity;

import java.util.List;

import org.jamwiki.model.OS;

import com.googlecode.objectify.OQuery;
import com.googlecode.objectify.Objectify;

public class UserAuthorityService {

  public static UserAuthorityEntity save(UserAuthorityEntity page) {
    Objectify ofy = OS.begin();
    ofy.put(page);
    return page;
  }

//  public static UserAuthorityEntity update(UserAuthorityEntity role) {
//    UserAuthorityEntity existingEntity = null;
//    try {
//      Objectify ofy = OS.begin();
//      existingEntity = ofy.get(UserAuthorityEntity.class, role.getUserAuthorityId());
//      existingEntity.setGroupId(role.getGroupId());
//      existingEntity.setAuthority(role.getAuthority());
//      ofy.put(existingEntity);
//    } catch (EntityNotFoundException enf) {
//    }
//    return existingEntity;
//  }

  public static void delete(UserAuthorityEntity role) {
    Objectify ofy = OS.begin();
    ofy.delete(role);
  }

  public static void deleteByUsername(String username) {
    UserAuthorityEntity role;
    try {
      Objectify ofy = OS.begin();
      OQuery<UserAuthorityEntity> q = OS.createQuery(UserAuthorityEntity.class);
      q.filter("username", username);
      Iterable<UserAuthorityEntity> it = ofy.prepare(q).asIterable();
      for (UserAuthorityEntity groupAuthorityEntity : it) {
        ofy.delete(groupAuthorityEntity);
      }
    } catch (NullPointerException npe) {
    }
  }


  public static List<UserAuthorityEntity> getAll() {
    List<UserAuthorityEntity> resultList = null;
    Objectify ofy = OS.begin();
    OQuery<UserAuthorityEntity> q = OS.createQuery(UserAuthorityEntity.class);
    resultList = ofy.prepare(q).asList();
    return resultList;
  }

}
