package dao;

import java.util.ArrayList;
import java.util.List;

import model.Post;

import org.springframework.stereotype.Repository;

import dao.data.Stack;
import dao.data.Store;

@Repository
public class PostDao extends Store<Post> {

	public Post get(Object id) {
		return Stack.postIdMap.get(id);
	}

	public List<Post> findAll() {
		return new ArrayList<Post>(Stack.postIdMap.values());
	}

}
