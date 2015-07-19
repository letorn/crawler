package dao;

import model.TechnologyGategory;

import org.springframework.stereotype.Repository;

import dao.data.Stack;
import dao.data.Store;

@Repository
public class TechnologyGategoryDao extends Store<TechnologyGategory> {

	public TechnologyGategory get(Object id) {
		return Stack.technologyGategoryIdMap.get(id);
	}

	public TechnologyGategory get(String code) {
		return Stack.technologyGategoryCodeMap.get(code);
	}

}
