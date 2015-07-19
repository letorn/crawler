package dao;

import model.CategoryPost;

import org.springframework.stereotype.Repository;

import dao.data.Stack;
import dao.data.Store;

@Repository
public class CategoryPostDao extends Store<CategoryPost> {

	public CategoryPost get(Object id) {
		return Stack.categoryPostIdMap.get(id);
	}

	public CategoryPost get(String code) {
		return Stack.categoryPostCodeMap.get(code);
	}

}
