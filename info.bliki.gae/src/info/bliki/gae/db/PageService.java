package info.bliki.gae.db;

import info.bliki.gae.model.Page;

import java.util.List;



public interface PageService {
	Page save(Page page);
	Page update(Page page);
	void delete(Page page);
	Page findByKey(Long key);
	Page findByTitle(String title);
	List<Page> getAll();
}
