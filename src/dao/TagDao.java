package dao;

import java.util.ArrayList;
import java.util.List;

import model.Tag;

import org.springframework.stereotype.Repository;

import dao.data.Stack;
import dao.data.Store;

@Repository
public class TagDao extends Store<Tag> {

	/*public Tag get(Object id) {
		return Stack.tagIdMap.get(id);
	}

	public List<Tag> findAll() {
		return new ArrayList<Tag>(Stack.tagIdMap.values());
	}*/

}
