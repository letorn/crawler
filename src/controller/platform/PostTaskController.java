package controller.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import crawler.post.Collector;
import crawler.post.Heap;
import crawler.post.model.Bill;
import crawler.post.model.Enterprise;
import crawler.post.model.Post;

@Controller
@RequestMapping("platform/posttask/")
public class PostTaskController {

	@RequestMapping("norms.do")
	@ResponseBody
	public Map<String, Object> norms() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (String norm : Heap.getBillNorms().keySet()) {
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
		for (Collector collector : Heap.getCollectors().values()) {
			Map<String, Object> d = new HashMap<String, Object>();
			d.put("cid", collector.getId());
			d.put("norm", collector.getNorm());
			d.put("region", collector.getRegion());
			d.put("area", collector.getArea());
			Integer[] billSizes = Heap.billSizeGroupByStatus(collector.getId());
			d.put("ignoredBillSize", billSizes[1]);
			d.put("billSize", billSizes[0] + billSizes[1] + billSizes[2]);
			Integer[] postSizes = Heap.postSizeGroupByStatus(collector.getId());
			d.put("ignoredPostSize", postSizes[1]);
			d.put("insertedPostSize", postSizes[2]);
			d.put("updatedPostSize", postSizes[3]);
			d.put("processedPostSize", postSizes[1] + postSizes[2] + postSizes[3]);
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
	public Map<String, Object> addTask(String norm, String region, String area) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(norm) || StringUtils.isBlank(region)) {
			resultMap.put("success", false);
		} else {
			resultMap.put("success", Heap.saveCollector(new Collector(norm, region, area)));
		}
		return resultMap;
	}

	@RequestMapping("pagedBill.do")
	@ResponseBody
	public Map<String, Object> pagedBill(String cid, Integer start, Integer limit) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid) || !Heap.existCollector(cid) || null == start || 0 > start || null == limit || 0 > limit || 100 < limit) {
			resultMap.put("success", false);
		} else {
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			for (Bill bill : Heap.findBill(cid, start, limit)) {
				Map<String, Object> d = new HashMap<String, Object>();
				d.put("date", bill.getDate());
				d.put("postURL", bill.getPostURL());
				d.put("postName", bill.getPostName());
				d.put("enterpriseURL", bill.getEnterpriseURL());
				d.put("enterpriseName", bill.getEnterpriseName());
				d.put("status", bill.getStatus());
				data.add(d);
			}
			resultMap.put("data", data);
			resultMap.put("total", Heap.billSize(cid));
			resultMap.put("success", true);
		}
		return resultMap;
	}

	@RequestMapping("pagedPost.do")
	@ResponseBody
	public Map<String, Object> pagedPost(String cid, Integer postStatus, Integer start, Integer limit) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid) || !Heap.existCollector(cid) || null == postStatus || 1 > postStatus || 3 < postStatus || null == start || 0 > start || null == limit || 0 > limit || 100 < limit) {
			resultMap.put("success", false);
		} else {
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			for (Post post : Heap.findPost(cid, postStatus, start, limit)) {
				Map<String, Object> d = new HashMap<String, Object>();
				d.put("url", post.getURL());
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
			resultMap.put("total", Heap.postSizeGroupByStatus(cid)[postStatus]);
			resultMap.put("success", true);
		}
		return resultMap;
	}

	@RequestMapping("pagedEnterprise.do")
	@ResponseBody
	public Map<String, Object> pagedEnterprise(String cid, Integer start, Integer limit) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid) || !Heap.existCollector(cid) || null == start || 0 > start || null == limit || 0 > limit || 100 < limit) {
			resultMap.put("success", false);
		} else {
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			for (Enterprise enterprise : Heap.findEnterprise(cid, start, limit)) {
				Map<String, Object> d = new HashMap<String, Object>();
				d.put("url", enterprise.getURL());
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
			resultMap.put("total", Heap.enterpriseSize(cid));
			resultMap.put("success", true);
		}
		return resultMap;
	}

	@RequestMapping("postDetail.do")
	@ResponseBody
	public Map<String, Object> postDetail(String cid, String url) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid) || StringUtils.isBlank(url) || !Heap.existPost(cid, url)) {
			resultMap.put("success", false);
		} else {
			Post post = Heap.getPost(cid, url);
			Map<String, Object> postData = new HashMap<String, Object>();
			postData.put("url", post.getURL());
			postData.put("date", post.getDate());
			postData.put("name", post.getName());
			postData.put("category", post.getCategory());
			postData.put("numberText", post.getNumberText());
			postData.put("nature", post.getNature());
			postData.put("salaryText", post.getSalaryText());
			postData.put("experience", post.getExperience());
			postData.put("education", post.getEducation());
			postData.put("welfare", post.getWelfare());
			postData.put("address", post.getAddress());
			postData.put("introduction", post.getIntroduction());
			resultMap.put("post", postData);

			Enterprise enterprise = Heap.getEnterprise(cid, post.getEnterpriseURL());
			Map<String, Object> enterpriseData = new HashMap<String, Object>();
			enterpriseData.put("url", enterprise.getURL());
			enterpriseData.put("name", enterprise.getName());
			enterpriseData.put("category", enterprise.getCategory());
			enterpriseData.put("nature", enterprise.getNature());
			enterpriseData.put("scale", enterprise.getScale());
			enterpriseData.put("website", enterprise.getWebsite());
			enterpriseData.put("address", enterprise.getAddress());
			enterpriseData.put("introduction", enterprise.getIntroduction());
			resultMap.put("enterprise", enterpriseData);

			resultMap.put("success", true);
		}
		return resultMap;
	}

	@RequestMapping("startCollector.do")
	@ResponseBody
	public Map<String, Object> startCollector(String cid) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid) || !Heap.existCollector(cid)) {
			resultMap.put("success", false);
		} else {
			resultMap.put("success", Heap.getCollector(cid).start());
		}
		return resultMap;
	}

	@RequestMapping("pauseCollector.do")
	@ResponseBody
	public Map<String, Object> pauseCollector(String cid) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid) || !Heap.existCollector(cid)) {
			resultMap.put("success", false);
		} else {
			resultMap.put("success", Heap.getCollector(cid).pause());
		}
		return resultMap;
	}

	@RequestMapping("stopCollector.do")
	@ResponseBody
	public Map<String, Object> stopCollector(String cid) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid) || !Heap.existCollector(cid)) {
			resultMap.put("success", false);
		} else {
			resultMap.put("success", Heap.getCollector(cid).stop());
		}
		return resultMap;
	}

	@RequestMapping("deleteCollector.do")
	@ResponseBody
	public Map<String, Object> deleteCollector(String cid) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(cid) || !Heap.existCollector(cid)) {
			resultMap.put("success", false);
		} else {
			Collector collector = Heap.removeCollector(cid);
			if (null == collector) {
				resultMap.put("success", false);
			} else {
				resultMap.put("success", true);
			}
		}
		return resultMap;
	}

}
