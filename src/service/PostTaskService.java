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

		Map<String, Map<String, String>> postCategoryMap = Holder.getPostCategoryCodeMap();
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

		Map<String, String> postExperienceCodeMap = Holder.getPostExperienceCodeMap();
		for (String code : postExperienceCodeMap.keySet()) {
			Map<String, String> map = new HashMap<String, String>();
			String name = postExperienceCodeMap.get(code);
			map.put("name", name);
			map.put("code", code);
			postExperiences.add(map);
		}
		Collections.sort(postExperiences, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> map1, Map<String, String> map2) {
				return map1.get("code").compareTo(map2.get("code"));
			}
		});

		Map<String, String> postEducationCodeMap = Holder.getPostEducationCodeMap();
		for (String code : postEducationCodeMap.keySet()) {
			Map<String, String> map = new HashMap<String, String>();
			String name = postEducationCodeMap.get(code);
			map.put("name", name);
			map.put("code", code);
			postEducations.add(map);
		}
		Collections.sort(postEducations, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> map1, Map<String, String> map2) {
				return map1.get("code").compareTo(map2.get("code"));
			}
		});

		Map<String, String> enterpriseCategoryMap = Holder.getEnterpriseCategoryCodeMap();
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

		Map<String, String> enterpriseNatureMap = Holder.getEnterpriseNatureCodeMap();
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

		Map<String, String> enterpriseScaleMap = Holder.getEnterpriseScaleCodeMap();
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

	public boolean existCollector(String cid) {
		for (String id : collectors.keySet())
			if (cid.contains(id))
				return true;
		return false;
	}

	public boolean existCollector(String region, String area, String nid) {
		String cid = Ver.bl(area) ? String.format("%s.%s", nid, region) : String.format("%s.%s.%s", nid, region, area);
		for (String id : collectors.keySet())
			if (cid.contains(id))
				return true;
		return false;
	}

	public boolean addCollector(String region, String area, String nid) {
		Map<String, Object> norm = norms.get(nid);
		if (norm != null) {
			Collector collector = new Collector(region, area, norm);
			collectors.put(collector.getId(), collector);
			return true;
		}
		return false;
	}

	public boolean startCollector(String cid) {
		Collector collector = collectors.get(cid);
		if (collector != null)
			return collector.start();
		return false;
	}

	public boolean pauseCollector(String cid) {
		Collector collector = collectors.get(cid);
		if (collector != null)
			return collector.pause();
		return false;
	}

	public boolean stopCollector(String cid) {
		Collector collector = collectors.get(cid);
		if (collector != null)
			return collector.stop();
		return false;
	}

	public boolean deleteCollector(String cid) {
		Collector collector = collectors.remove(cid);
		if (collector != null) {
			return collector.clear();
		}
		return false;
	}

	public boolean existPost(String cid, String url) {
		if (cid != null) {
			Collector collector = collectors.get(cid);
			if (collector != null)
				return collector.existPost(url);
		}
		return false;
	}

	public Post getPost(String url) {
		return Holder.getPost(url);
	}

	public Post getPost(String cid, String url) {
		Collector collector = collectors.get(cid);
		if (collector != null)
			return collector.getPost(url);
		return null;
	}

	public Enterprise getEnterprise(String url) {
		return Holder.getEnterprise(url);
	}

	public Enterprise getEnterprise(String cid, String url) {
		Collector collector = collectors.get(cid);
		if (collector != null)
			return collector.getEnterprise(url);
		return null;
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

	public boolean savePost(Post updatedPost, Enterprise updatedEnterprise) {
		Post post = Holder.getPost(updatedPost.getDataUrl());
		Enterprise enterprise = Holder.getEnterprise(updatedEnterprise.getDataUrl());
		if (post != null && enterprise != null) {

			enterprise.setStatus(0);

			if (StringUtils.isNotBlank(updatedEnterprise.getName()) && !updatedEnterprise.getName().equals(enterprise.getName())) {
				enterprise.setName(updatedEnterprise.getName());
			}

			if (Ver.nb(updatedEnterprise.getCategoryCode()) && !updatedEnterprise.getCategoryCode().equals(enterprise.getCategoryCode())) {
				String categoryName = Holder.getEnterpriseCategoryCode(updatedEnterprise.getCategoryCode());
				if (categoryName != null) {
					enterprise.setCategory(categoryName);
					enterprise.setCategoryCode(updatedEnterprise.getCategoryCode());
				}
			}

			if (Ver.nb(updatedEnterprise.getAddress()) && !updatedEnterprise.getAddress().equals(enterprise.getAddress())) {
				enterprise.setAddress(updatedEnterprise.getAddress());
				String areaCode = Holder.getAreaCode(updatedEnterprise.getAddress());
				if (Ver.nb(areaCode) && !areaCode.equals(enterprise.getAreaCode())) {
					enterprise.setAreaCode(areaCode);
					Double[] point = Client.getPoint(updatedEnterprise.getAddress());
					if (point != null) {
						enterprise.setLbsId(enterprise.getLbsId());
						enterprise.setLbsLon(point[0]);
						enterprise.setLbsLat(point[1]);
					}
				}
			}

			if (Ver.bl(enterprise.getName()) || Ver.bl(enterprise.getCategoryCode()) || Ver.bl(enterprise.getNatureCode()) || Ver.bl(enterprise.getScaleCode()) || Ver.bl(enterprise.getAreaCode()) || Ver.bl(enterprise.getAddress()) || enterprise.getLbsLon() == null || enterprise.getLbsLat() == null || Ver.bl(enterprise.getDataSrc()) || Ver.bl(enterprise.getDataUrl()) || enterprise.getCreateDate() == null)
				enterprise.setStatus(-1);

			post.setStatus(0);

			if (StringUtils.isNotBlank(updatedPost.getName()) && !updatedPost.getName().equals(post.getName())) {
				post.setName(updatedPost.getName());
			}

			if (StringUtils.isNotBlank(updatedPost.getCategoryCode()) && !updatedPost.getCategoryCode().equals(post.getCategoryCode())) {
				Map<String, String> categoryMap = Holder.getPostCategoryCode(updatedPost.getCategoryCode());
				if (categoryMap != null) {
					post.setCategory(categoryMap.get("name"));
					post.setCategoryCode(updatedPost.getCategoryCode());
				}
			}

			if (Ver.bl(post.getName()) || Ver.bl(post.getCategoryCode()) || Ver.bl(post.getNatureCode()) || Ver.bl(post.getExperienceCode()) || Ver.bl(post.getEducationCode()) || Ver.bl(post.getDataSrc()) || Ver.bl(post.getDataUrl()) || post.getUpdateDate() == null || Ver.bl(post.getEnterpriseUrl()) || Ver.bl(post.getEnterpriseName()))
				post.setStatus(-1);

			if (enterprise.getStatus() == -1)
				post.setStatus(-1);

			if (post.getStatus() != -1) {
				post.setAreaCode(enterprise.getAreaCode());
				post.setAddress(enterprise.getAddress());
				post.setLbsLon(enterprise.getLbsLon());
				post.setLbsLat(enterprise.getLbsLat());

				if (Holder.saveEnterprise(enterprise))
					if (Holder.savePost(post))
						return true;
			}
		}
		return false;
	}

	public boolean savePost(String cid, Post updatedPost, Enterprise updatedEnterprise) {
		Collector collector = collectors.get(cid);
		Post post = collector.getPost(updatedPost.getDataUrl());
		Enterprise enterprise = collector.getEnterprise(updatedEnterprise.getDataUrl());
		if (post != null && enterprise != null) {

			enterprise.setStatus(0);

			if (StringUtils.isNotBlank(updatedEnterprise.getName()) && !updatedEnterprise.getName().equals(enterprise.getName())) {
				enterprise.setName(updatedEnterprise.getName());
			}

			if (Ver.nb(updatedEnterprise.getCategoryCode()) && !updatedEnterprise.getCategoryCode().equals(enterprise.getCategoryCode())) {
				String categoryName = Holder.getEnterpriseCategoryCode(updatedEnterprise.getCategoryCode());
				if (categoryName != null) {
					enterprise.setCategory(categoryName);
					enterprise.setCategoryCode(updatedEnterprise.getCategoryCode());
				}
			}

			if (Ver.nb(updatedEnterprise.getAddress()) && !updatedEnterprise.getAddress().equals(enterprise.getAddress())) {
				enterprise.setAddress(updatedEnterprise.getAddress());
				String areaCode = Holder.getAreaCode(updatedEnterprise.getAddress());
				if (Ver.nb(areaCode) && !areaCode.equals(enterprise.getAreaCode())) {
					enterprise.setAreaCode(areaCode);
					Double[] point = Client.getPoint(updatedEnterprise.getAddress());
					if (point != null) {
						enterprise.setLbsId(enterprise.getLbsId());
						enterprise.setLbsLon(point[0]);
						enterprise.setLbsLat(point[1]);
					}
				}
			}

			if (Ver.bl(enterprise.getName()) || Ver.bl(enterprise.getCategoryCode()) || Ver.bl(enterprise.getNatureCode()) || Ver.bl(enterprise.getScaleCode()) || Ver.bl(enterprise.getAreaCode()) || Ver.bl(enterprise.getAddress()) || enterprise.getLbsLon() == null || enterprise.getLbsLat() == null || Ver.bl(enterprise.getDataSrc()) || Ver.bl(enterprise.getDataUrl()) || enterprise.getCreateDate() == null)
				enterprise.setStatus(-1);

			post.setStatus(0);

			if (StringUtils.isNotBlank(updatedPost.getName()) && !updatedPost.getName().equals(post.getName())) {
				post.setName(updatedPost.getName());
			}

			if (StringUtils.isNotBlank(updatedPost.getCategoryCode()) && !updatedPost.getCategoryCode().equals(post.getCategoryCode())) {
				Map<String, String> categoryMap = Holder.getPostCategoryCode(updatedPost.getCategoryCode());
				if (categoryMap != null) {
					post.setCategory(categoryMap.get("name"));
					post.setCategoryCode(updatedPost.getCategoryCode());
				}
			}

			if (Ver.bl(post.getName()) || Ver.bl(post.getCategoryCode()) || Ver.bl(post.getNatureCode()) || Ver.bl(post.getExperienceCode()) || Ver.bl(post.getEducationCode()) || Ver.bl(post.getDataSrc()) || Ver.bl(post.getDataUrl()) || post.getUpdateDate() == null || Ver.bl(post.getEnterpriseUrl()) || Ver.bl(post.getEnterpriseName()))
				post.setStatus(-1);

			if (enterprise.getStatus() == -1)
				post.setStatus(-1);

			if (post.getStatus() != -1) {
				post.setAreaCode(enterprise.getAreaCode());
				post.setAddress(enterprise.getAddress());
				post.setLbsLon(enterprise.getLbsLon());
				post.setLbsLat(enterprise.getLbsLat());

				if (Holder.saveEnterprise(enterprise))
					if (Holder.savePost(post))
						if (collector.saveEnterprise(enterprise))
							if (collector.savePost(post))
								return true;
			}
		}
		return false;
	}

	public List<Marker<Post>> getPostMarkers(int zoom) {
		return Holder.getPostCluster().getMarkers(zoom);
	}

	public List<Marker<Post>> getPostMarkers(String cid, int zoom) {
		return collectors.get(cid).getPostCluster().getMarkers(zoom);
	}

	public List<Post> getMarkerPosts(int zoom, double[] point, int start, int limit) {
		List<Post> list = new ArrayList<Post>();
		Marker<Post> marker = Holder.getPostCluster().getMarker(zoom, point);
		if (marker != null) {
			List<Post> posts = marker.getPoints();
			for (int i = start; i < posts.size() && list.size() < limit; i++)
				list.add(posts.get(i));
		}
		return list;
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

	public int getMarkerPostSize(int zoom, double[] point) {
		Marker<Post> marker = Holder.getPostCluster().getMarker(zoom, point);
		if (marker != null)
			return marker.getPoints().size();
		return 0;
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
