package dao;

import java.util.List;

import model.EntEnterprise;

import org.springframework.stereotype.Repository;

import dao.data.Stack;
import dao.data.Store;

@Repository
public class EntEnterpriseDao extends Store<EntEnterprise> {

	public EntEnterprise get(Long id) {
		return Stack.entEnterpriseIdMap.get(id);
	}

	public Boolean add(EntEnterprise entEnterprise) {
		if (super.add(entEnterprise)) {
			Stack.entEnterpriseIdMap.put(entEnterprise.getEntId(), entEnterprise);
			Stack.entEnterpriseNameMap.put(entEnterprise.getEntName(), entEnterprise);
		}
		return false;
	}

	public Boolean add(List<EntEnterprise> list) {
		if (super.add(list))
			for (EntEnterprise entEnterprise : list) {
				Stack.entEnterpriseIdMap.put(entEnterprise.getEntId(), entEnterprise);
				Stack.entEnterpriseNameMap.put(entEnterprise.getEntName(), entEnterprise);
			}
		return false;
	}

	public Boolean update(EntEnterprise entEnterprise) {
		if (super.update(entEnterprise)) {
			Stack.entEnterpriseIdMap.put(entEnterprise.getEntId(), entEnterprise);
			Stack.entEnterpriseNameMap.put(entEnterprise.getEntName(), entEnterprise);
		}
		return false;
	}

	public Boolean update(List<EntEnterprise> list) {
		if (super.update(list))
			for (EntEnterprise entEnterprise : list) {
				Stack.entEnterpriseIdMap.put(entEnterprise.getEntId(), entEnterprise);
				Stack.entEnterpriseNameMap.put(entEnterprise.getEntName(), entEnterprise);
			}
		return false;
	}

}
