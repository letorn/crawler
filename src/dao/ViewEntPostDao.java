package dao;

import java.util.List;

import model.ViewEntPost;

import org.springframework.stereotype.Repository;

import dao.data.Stack;
import dao.data.Store;

@Repository
public class ViewEntPostDao extends Store<ViewEntPost> {

	public ViewEntPost get(Object id) {
		return Stack.viewEntPostIdMap.get(id);
	}

	public ViewEntPost getByPostId(Long postId) {
		return Stack.viewEntPostPostIdMap.get(postId);
	}
	
	public ViewEntPost getByEntId(Long entId) {
		return Stack.viewEntPostEntIdMap.get(entId);
	}
	
	public Boolean add(ViewEntPost viewEntPost) {
		if (super.add(viewEntPost)) {
			Stack.viewEntPostIdMap.put(viewEntPost.getId(), viewEntPost);
			Stack.viewEntPostPostIdMap.put(viewEntPost.getPostId(), viewEntPost);
			Stack.viewEntPostEntIdMap.put(viewEntPost.getEntId(), viewEntPost);
		}
		return false;
	}

	public Boolean add(List<ViewEntPost> list) {
		if (super.add(list))
			for (ViewEntPost viewEntPost : list) {
				Stack.viewEntPostIdMap.put(viewEntPost.getId(), viewEntPost);
				Stack.viewEntPostPostIdMap.put(viewEntPost.getPostId(), viewEntPost);
				Stack.viewEntPostEntIdMap.put(viewEntPost.getEntId(), viewEntPost);
			}
		return false;
	}

	public Boolean update(ViewEntPost viewEntPost) {
		if (super.update(viewEntPost)) {
			Stack.viewEntPostIdMap.put(viewEntPost.getId(), viewEntPost);
			Stack.viewEntPostPostIdMap.put(viewEntPost.getPostId(), viewEntPost);
			Stack.viewEntPostEntIdMap.put(viewEntPost.getEntId(), viewEntPost);
		}
		return false;
	}

	public Boolean update(List<ViewEntPost> list) {
		if (super.update(list))
			for (ViewEntPost viewEntPost : list) {
				Stack.viewEntPostIdMap.put(viewEntPost.getId(), viewEntPost);
				Stack.viewEntPostPostIdMap.put(viewEntPost.getPostId(), viewEntPost);
				Stack.viewEntPostEntIdMap.put(viewEntPost.getEntId(), viewEntPost);
			}
		return false;
	}

}
