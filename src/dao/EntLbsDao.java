package dao;

import java.util.List;

import model.EntLbs;

import org.springframework.stereotype.Repository;

import dao.data.Stack;
import dao.data.Store;

@Repository
public class EntLbsDao extends Store<EntLbs> {

	public EntLbs get(Long id) {
		return Stack.entLbsIdMap.get(id);
	}

	public Boolean add(EntLbs entLbs) {
		if (super.add(entLbs))
			Stack.entLbsIdMap.put(entLbs.getLbsId(), entLbs);
		return false;
	}

	public Boolean add(List<EntLbs> list) {
		if (super.add(list))
			for (EntLbs entLbs : list)
				Stack.entLbsIdMap.put(entLbs.getLbsId(), entLbs);
		return false;
	}

	public Boolean update(EntLbs entLbs) {
		if (super.update(entLbs))
			Stack.entLbsIdMap.put(entLbs.getLbsId(), entLbs);
		return false;
	}

	public Boolean update(List<EntLbs> list) {
		if (super.update(list))
			for (EntLbs entLbs : list)
				Stack.entLbsIdMap.put(entLbs.getLbsId(), entLbs);
		return false;
	}

}
