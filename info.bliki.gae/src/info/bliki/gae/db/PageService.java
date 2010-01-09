package info.bliki.gae.db;

import java.util.List;

import org.jamwiki.model.Topic;

public interface PageService {
  Topic save(Topic page);

  Topic update(Topic page);

  void delete(Topic page);

  Topic findByTitle(String title);

  String getHTMLContent(String title);

  List<Topic> getAll();
}
