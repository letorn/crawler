package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import util.WebContext;
import crawler.post.Collector;
import crawler.post.Holder;
import crawler.post.model.Enterprise;
import crawler.post.model.Post;

@Service
public class PostTaskService {

	private static Map<String, Map<String, Object>> norms = new ConcurrentHashMap<String, Map<String, Object>>();
	private static Map<String, Map<String, Object>> billNorms = new ConcurrentHashMap<String, Map<String, Object>>();
	private static Map<String, Map<String, Object>> postNorms = new ConcurrentHashMap<String, Map<String, Object>>();
	private static Map<String, Map<String, Object>> enterpriseNorms = new ConcurrentHashMap<String, Map<String, Object>>();

	private static List<Map<String, String>> postCategories = new ArrayList<Map<String, String>>();
	private static List<Map<String, String>> postExperiences = new ArrayList<Map<String, String>>();
	private static List<Map<String, String>> postEducations = new ArrayList<Map<String, String>>();

	private static List<Map<String, String>> enterpriseCategories = new ArrayList<Map<String, String>>();
	private static List<Map<String, String>> enterpriseNatures = new ArrayList<Map<String, String>>();
	private static List<Map<String, String>> enterpriseScales = new ArrayList<Map<String, String>>();

	private static Map<String, Collector> collectors = new ConcurrentHashMap<String, Collector>();

	@PostConstruct
	private void init() {
		File normDir = new File(WebContext.getAppRoot(), "norm");
		File[] normFiles = normDir.listFiles();
		for (File normFile : normFiles) {
			String name = normFile.getName();
			if (name.startsWith("post-")) {
				Map<String, Object> norm = toMap(normFile);
				String nid = (String) norm.get("nid");
				norms.put(nid, norm);
				billNorms.put(nid, (Map<String, Object>) norm.get("bill"));
				postNorms.put(nid, (Map<String, Object>) norm.get("post"));
				enterpriseNorms.put(nid, (Map<String, Object>) norm.get("enterprise"));
			}
		}

		Map<String, Map<String, String>> postCategoryMap = Holder.getPostCategories();
		for (String code : postCategoryMap.keySet()) {
			Map<String, String> map = new HashMap<String, String>();
			Map<String, String> postCategory = postCategoryMap.get(code);
			map.put("code", postCategory.get("code"));
			map.put("name", postCategory.get("name"));
			map.put("group", postCategory.get("group"));
			postCategories.add(postCategoryMap.get(code));
		}
		Collections.sort(postCategories, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> map1, Map<String, String> map2) {
				return map1.get("code").compareTo(map2.get("code"));
			}
		});

		Map<String, Map<String, String>> postExperienceMap = Holder.getPostExperiences();
		for (String code : postExperienceMap.keySet()) {
			Map<String, String> map = new HashMap<String, String>();
			Map<String, String> postExperience = postExperienceMap.get(code);
			map.put("code", postExperience.get("paramCode"));
			map.put("name", postExperience.get("paramName"));
			postExperiences.add(map);
		}
		Collections.sort(postExperiences, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> map1, Map<String, String> map2) {
				return map1.get("code").compareTo(map2.get("code"));
			}
		});

		Map<String, Map<String, String>> postEducationMap = Holder.getPostEducations();
		for (String code : postEducationMap.keySet()) {
			Map<String, String> map = new HashMap<String, String>();
			Map<String, String> postEducation = postEducationMap.get(code);
			map.put("code", postEducation.get("paramCode"));
			map.put("name", postEducation.get("paramName"));
			postEducations.add(map);
		}
		Collections.sort(postEducations, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> map1, Map<String, String> map2) {
				return map1.get("code").compareTo(map2.get("code"));
			}
		});

		Map<String, String> enterpriseCategoryMap = Holder.getEnterpriseCategories();
		for (String code : enterpriseCategoryMap.keySet()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("code", code);
			map.put("name", enterpriseCategoryMap.get(code));
			enterpriseCategories.add(map);
		}
		Collections.sort(enterpriseCategories, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> map1, Map<String, String> map2) {
				return map1.get("code").compareTo(map2.get("code"));
			}
		});

		Map<String, String> enterpriseNatureMap = Holder.getEnterpriseNatures();
		for (String code : enterpriseNatureMap.keySet()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("code", code);
			map.put("name", enterpriseNatureMap.get(code));
			enterpriseNatures.add(map);
		}
		Collections.sort(enterpriseNatures, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> map1, Map<String, String> map2) {
				return map1.get("code").compareTo(map2.get("code"));
			}
		});

		Map<String, String> enterpriseScaleMap = Holder.getEnterpriseScales();
		for (String code : enterpriseScaleMap.keySet()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("code", code);
			map.put("name", enterpriseScaleMap.get(code));
			enterpriseScales.add(map);
		}
		Collections.sort(enterpriseScales, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> map1, Map<String, String> map2) {
				return map1.get("code").compareTo(map2.get("code"));
			}
		});
	}

	public Map<String, Map<String, Object>> getBillNorms() {
		return billNorms;
	}

	public Map<String, Collector> getCollectors() {
		return collectors;
	}

	public Collector getCollector(String cid) {
		return collectors.get(cid);
	}

	public Boolean existCollector(String cid) {
		for (String id : collectors.keySet())
			if (cid.contains(id))
				return true;
		return false;
	}

	public Boolean existCollector(String region, String area, String norm) {
		String cid = StringUtils.isBlank(area) ? String.format("%s.%s", norm, region) : String.format("%s.%s.%s", norm, region, area);
		for (String id : collectors.keySet())
			if (cid.contains(id))
				return true;
		return false;
	}

	public Boolean addCollector(String region, String area, String nid) {
		Map<String, Object> norm = norms.get(nid);
		if (norm != null) {
			Collector collector = new Collector(region, area, norm);
			collectors.put(collector.getId(), collector);
			return true;
		}
		return false;
	}

	public Boolean startCollector(String cid) {
		Collector collector = collectors.get(cid);
		if (collector != null)
			return collector.start();
		return false;
	}

	public Boolean pauseCollector(String cid) {
		Collector collector = collectors.get(cid);
		if (collector != null)
			return collector.pause();
		return false;
	}

	public Boolean stopCollector(String cid) {
		Collector collector = collectors.get(cid);
		if (collector != null)
			return collector.stop();
		return false;
	}

	public Boolean deleteCollector(String cid) {
		Collector collector = collectors.remove(cid);
		if (collector != null) {
			return collector.clear();
		}
		return false;
	}

	public Boolean existPost(String cid, String url) {
		Collector collector = collectors.get(cid);
		if (collector != null)
			return collector.existPost(url);
		return false;
	}

	public List<Map<String, String>> getPostCategories() {
		return postCategories;
	}

	public List<Map<String, String>> getPostExperiences() {
		return postExperiences;
	}

	public List<Map<String, String>> getPostEducations() {
		return postEducations;
	}

	public List<Map<String, String>> getEnterpriseCategories() {
		return enterpriseCategories;
	}

	public List<Map<String, String>> getEnterpriseNatures() {
		return enterpriseNatures;
	}

	public List<Map<String, String>> getEnterpriseScales() {
		return enterpriseScales;
	}

	public Boolean savePost(String cid, Post post, Enterprise enterprise) {
		Collector collector = collectors.get(cid);
		return false;
	}

	private static Map<String, Object> toMap(File file) {
		StringBuffer fileBuffer = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			for (String line = br.readLine(); null != line; line = br.readLine()) {
				fileBuffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(true);
		return (Map<String, Object>) JSONObject.fromObject(fileBuffer.toString(), jsonConfig);
	}
}
