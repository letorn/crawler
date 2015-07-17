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

import map.Marker;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import util.Ver;
import util.WebContext;
import crawler.Client;
import crawler.post.Collector;
import crawler.post.Holder;
import crawler.post.model.AbilityParam;
import crawler.post.model.Enterprise;
import crawler.post.model.Lbs;
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

		Map<String, AbilityParam> postExperienceMap = Holder.getPostExperiences();
		for (String code : postExperienceMap.keySet()) {
			Map<String, String> map = new HashMap<String, String>();
			AbilityParam postExperience = postExperienceMap.get(code);
			map.put("code", postExperience.getCode());
			map.put("name", postExperience.getName());
			postExperiences.add(map);
		}
		Collections.sort(postExperiences, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> map1, Map<String, String> map2) {
				return map1.get("code").compareTo(map2.get("code"));
			}
		});

		Map<String, AbilityParam> postEducationMap = Holder.getPostEducations();
		for (String code : postEducationMap.keySet()) {
			Map<String, String> map = new HashMap<String, String>();
			AbilityParam postEducation = postEducationMap.get(code);
			map.put("code", postEducation.getCode());
			map.put("name", postEducation.getName());
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
		Post post = collector.getPost(updatedPost.getDataUrl());
		Enterprise enterprise = collector.getEnterprise(updatedEnterprise.getDataUrl());
		if (post != null && enterprise != null) {

			enterprise.setStatus(0);

			if (StringUtils.isNotBlank(updatedEnterprise.getName()) && !updatedEnterprise.getName().equals(enterprise.getName())) {
				enterprise.setName(updatedEnterprise.getName());
				enterprise.setDirty(true);
			}

			if (Ver.isNotBlank(updatedEnterprise.getCategoryCode()) && !updatedEnterprise.getCategoryCode().equals(enterprise.getCategoryCode())) {
				String categoryName = Holder.getEnterpriseCategory(updatedEnterprise.getCategoryCode());
				if (categoryName != null) {
					enterprise.setCategory(categoryName);
					enterprise.setCategoryCode(updatedEnterprise.getCategoryCode());
					enterprise.setDirty(true);
				}
			}

			if (Ver.isNotBlank(updatedEnterprise.getAddress()) && !updatedEnterprise.getAddress().equals(enterprise.getAddress())) {
				enterprise.setAddress(updatedEnterprise.getAddress());
				enterprise.setDirty(true);
				String areaCode = Holder.getAreaCode(updatedEnterprise.getAddress());
				if (Ver.isNotBlank(areaCode) && !areaCode.equals(enterprise.getAreaCode())) {
					enterprise.setAreaCode(areaCode);
					enterprise.setDirty(true);
					Double[] point = Client.getPoint(updatedEnterprise.getAddress());
					if (point != null) {
						Lbs lbs = enterprise.getLbs();
						if (lbs != null) {
							if (!lbs.getLon().equals(point[0]) || !lbs.getLat().equals(point[1])) {
								lbs.setLon(point[0]);
								lbs.setLat(point[1]);
								lbs.setDirty(true);
							}
						} else {
							lbs = new Lbs();
							lbs.setLon(point[0]);
							lbs.setLat(point[1]);
							enterprise.setLbs(lbs);
						}
					}
				}
			}

			if (Ver.isBlank(enterprise.getName()) || Ver.isBlank(enterprise.getCategoryCode()) || Ver.isBlank(enterprise.getNatureCode()) || Ver.isBlank(enterprise.getScaleCode()) || Ver.isBlank(enterprise.getAreaCode()) || Ver.isBlank(enterprise.getAddress()) || enterprise.getLbs() == null || Ver.isBlank(enterprise.getDataSrc()) || Ver.isBlank(enterprise.getDataUrl()) || enterprise.getCreateDate() == null)
				enterprise.setStatus(-1);

			post.setStatus(0);

			if (StringUtils.isNotBlank(updatedPost.getName()) && !updatedPost.getName().equals(post.getName())) {
				post.setName(updatedPost.getName());
				post.setDirty(true);
			}

			if (StringUtils.isNotBlank(updatedPost.getCategoryCode()) && !updatedPost.getCategoryCode().equals(post.getCategoryCode())) {
				Map<String, String> categoryMap = Holder.getPostCategory(updatedPost.getCategoryCode());
				if (categoryMap != null) {
					post.setCategory(categoryMap.get("name"));
					post.setCategoryCode(updatedPost.getCategoryCode());
					post.setDirty(true);
				}
			}

			if (Ver.isBlank(post.getName()) || Ver.isBlank(post.getCategoryCode()) || Ver.isBlank(post.getNatureCode()) || Ver.isBlank(post.getExperienceCode()) || Ver.isBlank(post.getEducationCode()) || Ver.isBlank(post.getDataSrc()) || Ver.isBlank(post.getDataUrl()) || post.getUpdateDate() == null || Ver.isBlank(post.getEnterpriseUrl()) || Ver.isBlank(post.getEnterpriseName()))
				post.setStatus(-1);

			if (enterprise.getStatus() == -1)
				post.setStatus(-1);

			if (post.getStatus() != -1) {

				if (Ver.isNotBlank(enterprise.getAreaCode()) && !enterprise.getAreaCode().equals(post.getAreaCode())) {
					post.setAreaCode(enterprise.getAreaCode());
					post.setDirty(true);
				}

				if (Ver.isNotBlank(enterprise.getAddress()) && !enterprise.getAddress().equals(post.getAddress())) {
					post.setAddress(enterprise.getAddress());
					post.setDirty(true);
				}

				if (enterprise.getLbs() != null) {
					Lbs lbs = post.getLbs();
					if (lbs != null) {
						if (!lbs.getLon().equals(enterprise.getLbs().getLon()) || !lbs.getLat().equals(enterprise.getLbs().getLat())) {
							lbs.setLon(enterprise.getLbs().getLon());
							lbs.setLat(enterprise.getLbs().getLat());
							lbs.setDirty(true);
						}
					} else {
						lbs = new Lbs();
						lbs.setLon(enterprise.getLbs().getLon());
						lbs.setLat(enterprise.getLbs().getLat());
						post.setLbs(lbs);
					}
				}

				if (Holder.saveEnterprise(enterprise))
					if (Holder.savePost(post))
						if (collector.saveEnterprise(enterprise))
							if (collector.savePost(post))
								return true;
			}
		}
		return false;
	}

	public List<Marker<Post>> getPostMarkers(String cid, int zoom) {
		return collectors.get(cid).getPostCluster().getMarkers(zoom);
	}

	public List<Post> getMarkerPosts(String cid, int zoom, double[] point, int start, int limit) {
		List<Post> list = new ArrayList<Post>();
		Marker<Post> marker = collectors.get(cid).getPostCluster().getMarker(zoom, point);
		if (marker != null) {
			List<Post> posts = marker.getPoints();
			for (int i = start; i < posts.size() && list.size() < limit; i++)
				list.add(posts.get(i));
		}
		return list;
	}

	public int getMarkerPostSize(String cid, int zoom, double[] point) {
		Marker<Post> marker = collectors.get(cid).getPostCluster().getMarker(zoom, point);
		if (marker != null)
			return marker.getPoints().size();
		return 0;
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
