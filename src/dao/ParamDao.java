package dao;

import java.util.ArrayList;
import java.util.List;

import model.Param;

import org.springframework.stereotype.Repository;

import dao.data.Stack;
import dao.data.Store;

@Repository
public class ParamDao extends Store<Param> {

	public Param get(Object id) {
		return Stack.paramIdMap.get(id);
	}

	public Param get(String code) {
		return Stack.paramCodeMap.get(code);
	}

	public List<Param> findAll() {
		return new ArrayList<Param>(Stack.paramIdMap.values());
	}

}
