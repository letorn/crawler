package dao;

import java.util.List;

import model.EntAccount;

import org.springframework.stereotype.Repository;

import dao.data.Stack;
import dao.data.Store;

@Repository
public class EntAccountDao extends Store<EntAccount> {

	public EntAccount get(Object id) {
		return Stack.entAccountIdMap.get(id);
	}

	public Boolean add(EntAccount entAccount) {
		if (super.add(entAccount)) {
			Stack.entAccountIdMap.put(entAccount.getAccountId(), entAccount);
			Stack.entAccountEntIdMap.put(entAccount.getEntId(), entAccount);
		}
		return false;
	}

	public Boolean add(List<EntAccount> list) {
		if (super.add(list))
			for (EntAccount entAccount : list) {
				Stack.entAccountIdMap.put(entAccount.getAccountId(), entAccount);
				Stack.entAccountEntIdMap.put(entAccount.getEntId(), entAccount);
			}
		return false;
	}

	public Boolean update(EntAccount entAccount) {
		if (super.update(entAccount)) {
			Stack.entAccountIdMap.put(entAccount.getAccountId(), entAccount);
			Stack.entAccountEntIdMap.put(entAccount.getEntId(), entAccount);
		}
		return false;
	}

	public Boolean update(List<EntAccount> list) {
		if (super.update(list))
			for (EntAccount entAccount : list) {
				Stack.entAccountIdMap.put(entAccount.getAccountId(), entAccount);
				Stack.entAccountEntIdMap.put(entAccount.getEntId(), entAccount);
			}
		return false;
	}

	public int getLastAutoAccountNum() {
		int num = -1;
		for (EntAccount entAccount : Stack.entAccountIdMap.values()) {
			if (entAccount.getCreateMode() == 2) {
				String account = entAccount.getAccount();
				if (account.matches("^zcdh\\d{7}$")) {
					int lastNum = Integer.valueOf(account.replaceAll("^zcdh", ""));
					if (lastNum > num)
						num = lastNum;
				}
			}
		}
		return num;
	}

}
