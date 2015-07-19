package dao;

import java.util.List;

import model.EntPost;

import org.springframework.stereotype.Repository;

import dao.data.Stack;
import dao.data.Store;

@Repository
public class EntPostDao extends Store<EntPost> {

	public EntPost get(Long id) {
		return Stack.entPostIdMap.get(id);
	}

	public Boolean add(EntPost entPost) {
		if (super.add(entPost))
			Stack.entPostIdMap.put(entPost.getId(), entPost);
		return false;
	}

	public Boolean add(List<EntPost> list) {
		if (super.add(list))
			for (EntPost entPost : list)
				Stack.entPostIdMap.put(entPost.getId(), entPost);
		return false;
	}

	public Boolean update(EntPost entPost) {
		if (super.update(entPost))
			Stack.entPostIdMap.put(entPost.getId(), entPost);
		return false;
	}

	public Boolean update(List<EntPost> list) {
		if (super.update(list))
			for (EntPost entPost : list)
				Stack.entPostIdMap.put(entPost.getId(), entPost);
		return false;
	}

}
