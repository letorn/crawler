package dao;

import java.util.List;

import model.EntPostStatus;

import org.springframework.stereotype.Repository;

import dao.data.Stack;
import dao.data.Store;

@Repository
public class EntPostStatusDao extends Store<EntPostStatus> {

	public EntPostStatus get(Object id) {
		return Stack.entPostStatusIdMap.get(id);
	}

	public EntPostStatus get(Long postId) {
		return Stack.entPostStatusPostIdMap.get(postId);
	}

	public Boolean add(EntPostStatus entPostStatus) {
		if (super.add(entPostStatus)) {
			Stack.entPostStatusIdMap.put(entPostStatus.getPsId(), entPostStatus);
			Stack.entPostStatusPostIdMap.put(entPostStatus.getPostId(), entPostStatus);
		}
		return false;
	}

	public Boolean add(List<EntPostStatus> list) {
		if (super.add(list))
			for (EntPostStatus entPostStatus : list) {
				Stack.entPostStatusIdMap.put(entPostStatus.getPsId(), entPostStatus);
				Stack.entPostStatusPostIdMap.put(entPostStatus.getPostId(), entPostStatus);
			}
		return false;
	}

	public Boolean update(EntPostStatus entPostStatus) {
		if (super.update(entPostStatus)) {
			Stack.entPostStatusIdMap.put(entPostStatus.getPsId(), entPostStatus);
			Stack.entPostStatusPostIdMap.put(entPostStatus.getPostId(), entPostStatus);
		}
		return false;
	}

	public Boolean update(List<EntPostStatus> list) {
		if (super.update(list))
			for (EntPostStatus entPostStatus : list) {
				Stack.entPostStatusIdMap.put(entPostStatus.getPsId(), entPostStatus);
				Stack.entPostStatusPostIdMap.put(entPostStatus.getPostId(), entPostStatus);
			}
		return false;
	}

}
