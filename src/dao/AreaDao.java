package dao;

import java.util.List;

import model.Area;

import org.springframework.stereotype.Repository;

import dao.data.Stack;
import dao.data.Store;

@Repository
public class AreaDao extends Store<Area> {

	public Area get(Object id) {
		return Stack.areaIdMap.get(id);
	}

	public List<Area> find(int areaType) {
		return Stack.areaTypeMap.get(areaType);
	}

}
