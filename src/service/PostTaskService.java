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
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import util.GeoHash;
import util.WebContext;
import crawler.Client;
import crawler.post.Collector;
import crawler.post.Holder;
import crawler.post.model.Enterprise;
import crawler.post.model.Post;

@Service
public class PostTaskService {

	private static Logger logger = Logger.getLogger(PostTaskService.class);

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
		if (StringUtils.isNotBlank(cid))
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

	public Boolean savePost(String cid, Post updatedPost, Enterprise updatedEnterprise) {
		Collector collector = collectors.get(cid);
		Post post = collector.getPost(updatedPost.getUrl());
		Enterprise enterprise = collector.getEnterprise(updatedEnterprise.getUrl());
		if (post != null && enterprise != null) {
			post.setStatus(0);

			if (StringUtils.isNotBlank(updatedPost.getName()))
				post.setName(updatedPost.getName());

			if (StringUtils.isNotBlank(updatedPost.getCategoryCode())) {
				Map<String, String> categoryMap = Holder.getPostCategory(updatedPost.getCategoryCode());
				if (categoryMap != null) {
					post.setCategoryCode(updatedPost.getCategoryCode());
					post.setCategory(categoryMap.get("name"));
				}
			}

			if (StringUtils.isBlank(post.getSrc()) || StringUtils.isBlank(post.getUrl()) || post.getDate() == null || StringUtils.isBlank(post.getName()) || StringUtils.isBlank(post.getCategoryCode()) || StringUtils.isBlank(post.getNatureCode()) || StringUtils.isBlank(post.getExperienceCode()) || StringUtils.isBlank(post.getEducationCode()) || StringUtils.isBlank(post.getEnterpriseUrl()))
				post.setStatus(-1);

			enterprise.setStatus(0);

			if (StringUtils.isNotBlank(updatedEnterprise.getName()))
				enterprise.setName(updatedEnterprise.getName());

			if (StringUtils.isNotBlank(updatedEnterprise.getCategoryCode())) {
				String categoryName = Holder.getEnterpriseCategory(updatedEnterprise.getCategoryCode());
				if (categoryName != null) {
					enterprise.setCategoryCode(updatedEnterprise.getCategoryCode());
					enterprise.setCategory(categoryName);
				}
			}

			if (StringUtils.isNotBlank(updatedEnterprise.getAddress())) {
				enterprise.setAddress(updatedEnterprise.getAddress());
				String areaCode = Holder.getAreaCode(updatedEnterprise.getAddress());
				if (areaCode != null) {
					enterprise.setAreaCode(areaCode);
					Double[] point = Client.getPoint(updatedEnterprise.getAddress());
					if (point != null) {
						enterprise.setLbsLon(point[0]);
						enterprise.setLbsLat(point[1]);
					}
				}
			}

			if (StringUtils.isBlank(enterprise.getSrc()) || StringUtils.isBlank(enterprise.getUrl()) || enterprise.getDate() == null || StringUtils.isBlank(enterprise.getName()) || StringUtils.isBlank(enterprise.getCategoryCode()) || StringUtils.isBlank(enterprise.getNatureCode()) || StringUtils.isBlank(enterprise.getScaleCode()) || StringUtils.isBlank(enterprise.getAddress()) || StringUtils.isBlank(enterprise.getAreaCode()) || enterprise.getLbsLon() == null || enterprise.getLbsLat() == null)
				enterprise.setStatus(-1);

			if (enterprise.getStatus() != -1 && post.getStatus() != -1)
				if (Holder.existEnterpriseAccount(enterprise.getName()))
					logger.info(String.format("the enterprise has account, %s [date=%s, name=%s,]", enterprise.getUrl(), enterprise.getDate(), enterprise.getName()));
				else if (Holder.saveEnterprise(enterprise))
					if (Holder.savePost(post))
						if (collector.saveEnterprise(enterprise))
							if (collector.savePost(post))
								return true;
		}
		return false;
	}

	public Map<String, Object> getPostPoints(String cid, Integer zoom) {
		Map<String, Map<String, Object>> pointMap = new HashMap<String, Map<String, Object>>();
		List<Enterprise> enterprises = collectors.get(cid).getEnterprises();
		for (int i = 0; i < enterprises.size(); i++) {
			Enterprise enterprise = enterprises.get(i);
			String geohash = GeoHash.encode(enterprise.getLbsLon(), enterprise.getLbsLat());
			Double[] point = GeoHash.decode(geohash, zoom);
			String serialPoint = String.format("%f-%f", point[0], point[0]);
			
		}
		return null;
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
