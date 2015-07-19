package crawler.post;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import crawler.AttributeCatcher;
import crawler.Client;
import crawler.post.model.Bill;

public class Explorer {

	private Integer status = 0;// 0 停止, 1 启动, 2 暂停, 3 完成
	private Thread thread;

	private Collector collector;

	private String url;
	private Map<String, String> data;
	private String pageKey;
	private String areaKey;
	private String areaValue;

	private String billContainerSelector;
	private Map<String, String> billAttributeSelectors;

	public Explorer(Collector collector) {
		this.collector = collector;

		Map<String, Object> norm = collector.getNorm();
		Map<String, Object> billNorm = (Map<String, Object>) norm.get("bill");

		url = (String) billNorm.get("url");
		data = (Map<String, String>) billNorm.get("data");
		pageKey = (String) billNorm.get("pageKey");
		areaKey = (String) billNorm.get("areaKey");
		Map<String, String> areaCodes = (Map<String, String>) billNorm.get("area");
		areaValue = StringUtils.isBlank(collector.getArea()) ? areaCodes.get(collector.getRegion()) : areaCodes.get(collector.getArea());

		billContainerSelector = (String) billNorm.get("container");
		billAttributeSelectors = (Map<String, String>) billNorm.get("attribute");
	}

	public Boolean start() {
		if (areaValue == null || thread != null) {
			return false;
		} else {
			if (status == 3)
				clear();
			status = 1;
			thread = new Thread(new Runnable() {
				public void run() {
					for (Integer pageValue = 1; status == 1; pageValue++) {
						Document document = Client.get(String.format("%s?%s=%s&%s=%s", url, areaKey, areaValue, pageKey, pageValue), data);
						final AttributeCatcher billAttributeCatcher = new AttributeCatcher(document, billAttributeSelectors);
						Elements containers = document.select(billContainerSelector);
						if (containers.size() <= 0) {
							finish();
							break;
						}
						for (Element element : containers) {
							final Map<String, String> billAttributes = new HashMap<String, String>();
							element.traverse(new NodeVisitor() {
								public void head(Node node, int depth) {
									if (node instanceof Element) {
										billAttributes.putAll(billAttributeCatcher.attempt((Element) node));
									}
								}

								public void tail(Node node, int depth) {

								}
							});

							Bill bill = toBill(billAttributes);
							if (bill != null && status == 1) {
								if (collector.getDate().getTime() - bill.getDate().getTime() < 24 * 60 * 60 * 1000 * 2) {
									collector.saveBill(bill);
								} else {
									finish();
									break;
								}
							}
						}
					}
				}
			});
			thread.start();
		}
		return true;
	}

	public Boolean pause() {
		status = 2;
		thread = null;
		return true;
	}

	public Boolean finish() {
		status = 3;
		thread = null;
		return true;
	}

	public Boolean stop() {
		status = 0;
		thread = null;
		clear();
		return true;
	}

	public Boolean clear() {
		return collector.clearBill();
	}

	public Integer getStatus() {
		return status;
	}

	private Bill toBill(Map<String, String> map) {
		String dateString = map.get("date");
		Date date = dateString != null ? parseDate(dateString) : null;
		String postUrl = map.get("postUrl");
		String postName = map.get("postName");
		String enterpriseUrl = map.get("enterpriseUrl");
		String enterpriseName = map.get("enterpriseName");

		if (date == null || StringUtils.isBlank(postUrl) || StringUtils.isBlank(postName) || StringUtils.isBlank(enterpriseUrl) || StringUtils.isBlank(enterpriseName)) {
			return null;
		} else {
			Bill bill = new Bill();
			bill.setDate(date);
			bill.setPostUrl(postUrl);
			bill.setPostName(postName);
			bill.setEnterpriseUrl(enterpriseUrl);
			bill.setEnterpriseName(enterpriseName);
			return bill;
		}
	}

	private static Date parseDate(String dateString) {
		try {
			Calendar calendar = Calendar.getInstance();
			String[] strs = dateString.split("-|/");
			if (strs.length == 2) {
				calendar.set(calendar.get(Calendar.YEAR), Integer.parseInt(strs[0]) - 1, Integer.parseInt(strs[1]), 0, 0, 0);
			} else if (strs.length == 3) {
				calendar.set(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]) - 1, Integer.parseInt(strs[2]), 0, 0, 0);
			}
			return calendar.getTime();
		} catch (Exception e) {
			return null;
		}
	}

}