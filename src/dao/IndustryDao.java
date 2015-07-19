package dao;

import java.util.ArrayList;
import java.util.List;

import model.Industry;

import org.springframework.stereotype.Repository;

import dao.data.Stack;
import dao.data.Store;

@Repository
public class IndustryDao extends Store<Industry> {

	public List<Industry> findAll() {
		return new ArrayList<Industry>(Stack.industryIdMap.values());
	}

}
