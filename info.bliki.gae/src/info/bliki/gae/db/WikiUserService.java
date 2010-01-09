package info.bliki.gae.db;

import java.util.List;

import org.jamwiki.model.WikiUser;

public interface WikiUserService {
  WikiUser save(WikiUser user);

  WikiUser update(WikiUser user);

  void delete(WikiUser user);

  WikiUser findByEMail(String email);

  List<WikiUser> getAll();
}
