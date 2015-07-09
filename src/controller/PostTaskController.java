package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import service.PostTaskService;
import crawler.post.Collector;
import crawler.post.model.Bill;
import crawler.post.model.Enterprise;
import crawler.post.model.Post;

@Controller
@RequestMapping("/posttask/")
public class PostTaskController {

	@Resource
	private PostTaskService postTaskService;

	@RequestMapping("norms.do")
	@ResponseBody
	public Map<String, Object> norms() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (String norm : postTaskService.getBillNorms().keySet()) {
			Map<String, Object> d = new HashMap<String, Object>();
			d.put("name", norm);
			data.add(d);
		}
		resultMap.put("data", data);
		resultMap.put("success", true);
		return resultMap;
	}

	@RequestMapping("tasks.do")
	@ResponseBody
	public Map<String, Object> tasks() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (Collector collector : postTaskService.getCollectors().values()) {
			Map<String, Object> d = new HashMap<String, Object>();
			d.put("cid", collector.getId());
			d.put("norm", collector.getNid());
			d.put("region", collector.getRegion());
			d.put("area", collector.getArea());
			Integer[] billSizes = collector.billSizes();
			d.put("ignoredBillSize", billSizes[1]);
			d.put("billSize", billSizes[0] + billSizes[1] + billSizes[2]);
			Integer[] postSizes = collector.postSizes();
			d.put("failedPostSize", postSizes[0]);
			d.put("ignoredPostSize", postSizes[2]);
			d.put("insertedPostSize", postSizes[3]);
			d.put("updatedPostSize", postSizes[4]);
			d.put("processedPostSize", postSizes[2] + postSizes[3] + postSizes[4]);
			d.put("explorerStatus", collector.getExplorer().getStatus());
			d.put("collectorStatus", collector.getStatus());
			data.add(d);
		}
		resultMap.put("data", data);
		resultMap.put("success", true);
		return resultMap;
	}

	@RequestMapping("addTask.do")
	@ResponseBody
	public Map<String, Object> addTask(String region, String area, String norm) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(region) || StringUtils.isBlank(norm)) {
			resultMap.put("success", false);
		} else {
			if (postTaskService.existCollector(region, area, norm)) {
				resultMap.put("repeated", true);
			} else {
				postTaskService.addCollector(region, area, norm);
			}
			resultMap.put("success", true);
		}
		return resultMap;
	}

	@RequestMapping("pagedBill.do")
	@ResponseBody
	public Map<String, Object> pagedBill(String cid, Integer start, Integer limit) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid) || !postTaskService.existCollector(cid) || start == null || start < 0 || limit == null || limit < 0 || limit > 100) {
			resultMap.put("success", false);
		} else {
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			Collector collector = postTaskService.getCollector(cid);
			for (Bill bill : collector.findBill(start, limit)) {
				Map<String, Object> d = new HashMap<String, Object>();
				d.put("date", bill.getDate());
				d.put("postUrl", bill.getPostUrl());
				d.put("postName", bill.getPostName());
				d.put("enterpriseUrl", bill.getEnterpriseUrl());
				d.put("enterpriseName", bill.getEnterpriseName());
				d.put("status", bill.getStatus());
				data.add(d);
			}
			resultMap.put("data", data);
			resultMap.put("total", collector.billSize());
			resultMap.put("success", true);
		}
		return resultMap;
	}

	@RequestMapping("pagedPost.do")
	@ResponseBody
	public Map<String, Object> pagedPost(String cid, Integer postStatus, Integer start, Integer limit) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid) || !postTaskService.existCollector(cid) || postStatus == null || postStatus < -1 || postStatus > 3 || start == null || start < 0 || limit == null || limit < 0 || limit > 100) {
			resultMap.put("success", false);
		} else {
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			Collector collector = postTaskService.getCollector(cid);
			for (Post post : collector.findPost(postStatus, start, limit)) {
				Map<String, Object> d = new HashMap<String, Object>();
				d.put("url", post.getUrl());
				d.put("date", post.getDate());
				d.put("name", post.getName());
				d.put("category", post.getCategory());
				d.put("numberText", post.getNumberText());
				d.put("nature", post.getNature());
				d.put("salaryText", post.getSalaryText());
				d.put("experience", post.getExperience());
				d.put("education", post.getEducation());
				d.put("welfare", post.getWelfare());
				d.put("address", post.getAddress());
				d.put("status", post.getStatus());
				data.add(d);
			}
			resultMap.put("data", data);
			resultMap.put("total", collector.postSizes()[postStatus + 1]);
			resultMap.put("success", true);
		}
		return resultMap;
	}

	@RequestMapping("pagedEnterprise.do")
	@ResponseBody
	public Map<String, Object> pagedEnterprise(String cid, Integer start, Integer limit) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid) || !postTaskService.existCollector(cid) || start == null || start < 0 || limit == null || limit < 0 || limit > 100) {
			resultMap.put("success", false);
		} else {
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			Collector collector = postTaskService.getCollector(cid);
			for (Enterprise enterprise : collector.findEnterprise(start, limit)) {
				Map<String, Object> d = new HashMap<String, Object>();
				d.put("url", enterprise.getUrl());
				d.put("name", enterprise.getName());
				d.put("category", enterprise.getCategory());
				d.put("nature", enterprise.getNature());
				d.put("scale", enterprise.getScale());
				d.put("website", enterprise.getWebsite());
				d.put("address", enterprise.getAddress());
				d.put("status", enterprise.getStatus());
				data.add(d);
			}
			resultMap.put("data", data);
			resultMap.put("total", collector.enterpriseSize());
			resultMap.put("success", true);
		}
		return resultMap;
	}

	@RequestMapping("codes.do")
	@ResponseBody
	public Map<String, Object> codes() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("postCategories", postTaskService.getPostCategories());
		resultMap.put("postExperiences", postTaskService.getPostExperiences());
		resultMap.put("postEducations", postTaskService.getPostEducations());
		resultMap.put("enterpriseCategories", postTaskService.getEnterpriseCategories());
		resultMap.put("enterpriseNatures", postTaskService.getEnterpriseNatures());
		resultMap.put("enterpriseScales", postTaskService.getEnterpriseScales());
		resultMap.put("success", true);
		return resultMap;
	}

	@RequestMapping("postDetail.do")
	@ResponseBody
	public Map<String, Object> postDetail(String cid, String url) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid) || StringUtils.isBlank(url) || !postTaskService.existPost(cid, url)) {
			resultMap.put("success", false);
		} else {
			Collector collector = postTaskService.getCollector(cid);
			Post post = collector.getPost(url);
			Map<String, Object> postData = new HashMap<String, Object>();
			postData.put("url", post.getUrl());
			postData.put("date", post.getDate());
			postData.put("name", post.getName());
			postData.put("categoryCode", post.getCategoryCode());
			postData.put("numberText", post.getNumberText());
			postData.put("salaryText", post.getSalaryText());
			postData.put("experienceCode", post.getExperienceCode());
			postData.put("educationCode", post.getEducationCode());
			postData.put("welfare", post.getWelfare());
			postData.put("introduction", post.getIntroduction());
			resultMap.put("post", postData);

			Enterprise enterprise = collector.getEnterprise(post.getEnterpriseUrl());
			Map<String, Object> enterpriseData = new HashMap<String, Object>();
			enterpriseData.put("url", enterprise.getUrl());
			enterpriseData.put("name", enterprise.getName());
			enterpriseData.put("categoryCode", enterprise.getCategoryCode());
			enterpriseData.put("natureCode", enterprise.getNatureCode());
			enterpriseData.put("scaleCode", enterprise.getScaleCode());
			enterpriseData.put("website", enterprise.getWebsite());
			enterpriseData.put("address", enterprise.getAddress());
			enterpriseData.put("introduction", enterprise.getIntroduction());
			resultMap.put("enterprise", enterpriseData);

			resultMap.put("success", true);
		}
		return resultMap;
	}

	@RequestMapping("savePost.do")
	@ResponseBody
	public Map<String, Object> savePost(@RequestBody Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (map == null) {
			resultMap.put("success", false);
		} else {
			JSONObject requestBody = JSONObject.fromObject(map);
			if (requestBody.has("cid")) {
				String cid = requestBody.getString("cid");
				Post post = (Post) JSONObject.toBean(requestBody.getJSONObject("post"), Post.class);
				Enterprise enterprise = (Enterprise) JSONObject.toBean(requestBody.getJSONObject("enterprise"), Enterprise.class);
				if (!postTaskService.existCollector(cid) || post == null || StringUtils.isBlank(post.getUrl()) || enterprise == null || StringUtils.isBlank(enterprise.getUrl())) {
					resultMap.put("success", false);
				} else {
					resultMap.put("success", postTaskService.savePost(cid, post, enterprise));
				}
			} else {
				resultMap.put("success", false);
			}
		}
		return resultMap;
	}

	@RequestMapping("startCollector.do")
	@ResponseBody
	public Map<String, Object> startCollector(String cid) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid)) {
			resultMap.put("success", false);
		} else {
			resultMap.put("success", postTaskService.startCollector(cid));
		}
		return resultMap;
	}

	@RequestMapping("pauseCollector.do")
	@ResponseBody
	public Map<String, Object> pauseCollector(String cid) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid)) {
			resultMap.put("success", false);
		} else {
			resultMap.put("success", postTaskService.pauseCollector(cid));
		}
		return resultMap;
	}

	@RequestMapping("stopCollector.do")
	@ResponseBody
	public Map<String, Object> stopCollector(String cid) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid)) {
			resultMap.put("success", false);
		} else {
			resultMap.put("success", postTaskService.stopCollector(cid));
		}
		return resultMap;
	}

	@RequestMapping("deleteCollector.do")
	@ResponseBody
	public Map<String, Object> deleteCollector(String cid) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid)) {
			resultMap.put("success", false);
		} else {
			resultMap.put("success", postTaskService.deleteCollector(cid));
		}
		return resultMap;
	}

}
