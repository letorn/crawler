package dao;

import model.Technology;

import org.springframework.stereotype.Repository;

import dao.data.Stack;
import dao.data.Store;

@Repository
public class TechnologyDao extends Store<Technology> {

	public Technology get(Object id) {
		return Stack.technologyIdMap.get(id);
	}

	public Technology get(String code) {
		return Stack.technologyCodeMap.get(code);
	}

}
