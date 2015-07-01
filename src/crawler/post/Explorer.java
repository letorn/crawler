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

		Map<String, Object> billNorm = Heap.getBillNorm(collector.getNorm());

		url = (String) billNorm.get("url");
		data = (Map<String, String>) billNorm.get("data");
		pageKey = (String) billNorm.get("pageKey");
		areaKey = (String) billNorm.get("areaKey");
		Map<String, String> areaCodes = (Map<String, String>) billNorm.get("area");
		if (StringUtils.isBlank(collector.getArea())) {
			areaValue = areaCodes.get(collector.getRegion());
		} else {
			areaValue = areaCodes.get(collector.getArea());
		}

		billContainerSelector = (String) billNorm.get("container");
		billAttributeSelectors = (Map<String, String>) billNorm.get("attribute");
	}

	public Boolean start() {
		if (null == areaValue || null != thread) {
			return false;
		} else {
			if (3 == status) {
				clear();
			}
			status = 1;
			thread = new Thread(new Runnable() {
				public void run() {
					for (Integer pageValue = 1; 1 == status; pageValue++) {
						Document document = Client.get(String.format("%s?%s=%s&%s=%s", url, areaKey, areaValue, pageKey, pageValue), data);
						final AttributeCatcher billAttributeCatcher = new AttributeCatcher(document, billAttributeSelectors);
						Elements containers = document.select(billContainerSelector);
						if (0 >= containers.size()) {
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
							if (null != bill && 1 == status) {
								if (collector.getDate().getTime() - bill.getDate().getTime() < 24 * 60 * 60 * 1000) {
									Heap.saveBill(collector.getId(), bill);
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
		Heap.clearBills(collector.getId());
		return true;
	}

	public Integer getStatus() {
		return status;
	}

	private Bill toBill(Map<String, String> map) {
		String dateString = map.get("date");
		Date date = null;
		if (null != dateString) {
			date = parseDate(dateString);
		}

		String postURL = map.get("postURL");
		String postName = map.get("postName");
		String enterpriseURL = map.get("enterpriseURL");
		String enterpriseName = map.get("enterpriseName");

		if (null == date || null == postURL || null == postName || null == enterpriseURL || null == enterpriseName) {
			return null;
		} else {
			Bill bill = new Bill();
			bill.setDate(date);
			bill.setPostURL(postURL);
			bill.setPostName(postName);
			bill.setEnterpriseURL(enterpriseURL);
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