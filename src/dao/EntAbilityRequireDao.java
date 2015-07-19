package dao;

import java.util.List;

import model.EntAbilityRequire;

import org.springframework.stereotype.Repository;

import dao.data.Stack;
import dao.data.Store;

@Repository
public class EntAbilityRequireDao extends Store<EntAbilityRequire> {

	public EntAbilityRequire get(Object id) {
		return Stack.entAbilityRequireIdMap.get(id);
	}

	public EntAbilityRequire getExperience(Long postId) {
		return Stack.entAbilityRequireExperiencePostIdMap.get(postId);
	}

	public EntAbilityRequire getEducation(Long postId) {
		return Stack.entAbilityRequireEducationPostIdMap.get(postId);
	}

	public Boolean add(EntAbilityRequire entAbilityRequire) {
		if (super.add(entAbilityRequire)) {
			Stack.entAbilityRequireIdMap.put(entAbilityRequire.getEntAbilityId(), entAbilityRequire);
			if (entAbilityRequire.getPostId() != null)
				if ("-0000000000003".equals(entAbilityRequire.getTechnologyCode()))
					Stack.entAbilityRequireExperiencePostIdMap.put(entAbilityRequire.getPostId(), entAbilityRequire);
				else if ("-0000000000004".equals(entAbilityRequire.getTechnologyCode()))
					Stack.entAbilityRequireEducationPostIdMap.put(entAbilityRequire.getPostId(), entAbilityRequire);
		}
		return false;
	}

	public Boolean add(List<EntAbilityRequire> list) {
		if (super.add(list))
			for (EntAbilityRequire entAbilityRequire : list) {
				Stack.entAbilityRequireIdMap.put(entAbilityRequire.getEntAbilityId(), entAbilityRequire);
				if (entAbilityRequire.getPostId() != null)
					if ("-0000000000003".equals(entAbilityRequire.getTechnologyCode()))
						Stack.entAbilityRequireExperiencePostIdMap.put(entAbilityRequire.getPostId(), entAbilityRequire);
					else if ("-0000000000004".equals(entAbilityRequire.getTechnologyCode()))
						Stack.entAbilityRequireEducationPostIdMap.put(entAbilityRequire.getPostId(), entAbilityRequire);
			}
		return false;
	}

	public Boolean update(EntAbilityRequire entAbilityRequire) {
		if (super.update(entAbilityRequire)) {
			Stack.entAbilityRequireIdMap.put(entAbilityRequire.getEntAbilityId(), entAbilityRequire);
			if (entAbilityRequire.getPostId() != null)
				if ("-0000000000003".equals(entAbilityRequire.getTechnologyCode()))
					Stack.entAbilityRequireExperiencePostIdMap.put(entAbilityRequire.getPostId(), entAbilityRequire);
				else if ("-0000000000004".equals(entAbilityRequire.getTechnologyCode()))
					Stack.entAbilityRequireEducationPostIdMap.put(entAbilityRequire.getPostId(), entAbilityRequire);
		}
		return false;
	}

	public Boolean update(List<EntAbilityRequire> list) {
		if (super.update(list))
			for (EntAbilityRequire entAbilityRequire : list) {
				Stack.entAbilityRequireIdMap.put(entAbilityRequire.getEntAbilityId(), entAbilityRequire);
				if (entAbilityRequire.getPostId() != null)
					if ("-0000000000003".equals(entAbilityRequire.getTechnologyCode()))
						Stack.entAbilityRequireExperiencePostIdMap.put(entAbilityRequire.getPostId(), entAbilityRequire);
					else if ("-0000000000004".equals(entAbilityRequire.getTechnologyCode()))
						Stack.entAbilityRequireEducationPostIdMap.put(entAbilityRequire.getPostId(), entAbilityRequire);
			}
		return false;
	}

}
