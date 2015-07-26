package crawler.post;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.LbsClient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

import util.Ver;
import crawler.AttributeCatcher;
import crawler.DocumentClient;
import crawler.post.model.Bill;
import crawler.post.model.Enterprise;
import crawler.post.model.Post;

public class Processor {

	private static Logger logger = Logger.getLogger(Processor.class);

	private Integer status = 0;// 0 停止, 1 启动, 2 暂停, 3 完成
	private Thread thread;

	private Collector collector;

	private Integer lastProcessedBillIndex = -1;

	private Integer postUpdateInterval;
	private Map<String, String> postAttributeSelectors;
	private Map<String, Map<String, String>> postAttributeMappers;

	private Integer enterpriseUpdateInterval;
	private Map<String, String> enterpriseAttributeSelectors;
	private Map<String, Map<String, String>> enterpriseAttributeMappers;

	private List<Post> posts = new ArrayList<Post>();
	private List<Enterprise> enterprises = new ArrayList<Enterprise>();
	private Map<String, Integer> enterpriseUrlIndexes = new HashMap<String, Integer>();

	private DocumentClient documentClient = new DocumentClient();
	private LbsClient lbsClient = new LbsClient();

	public Processor(Collector collector) {
		this.collector = collector;

		Map<String, Object> norm = collector.getNorm();
		Map<String, Object> postNorm = (Map<String, Object>) norm.get("post");
		Map<String, Object> enterpriseNorm = (Map<String, Object>) norm.get("enterprise");

		postUpdateInterval = (Integer) postNorm.get("updateInterval");
		postAttributeSelectors = (Map<String, String>) postNorm.get("attribute");
		postAttributeMappers = (Map<String, Map<String, String>>) postNorm.get("mapper");

		enterpriseUpdateInterval = (Integer) enterpriseNorm.get("updateInterval");
		enterpriseAttributeSelectors = (Map<String, String>) enterpriseNorm.get("attribute");
		enterpriseAttributeMappers = (Map<String, Map<String, String>>) enterpriseNorm.get("mapper");
	}

	public boolean start() {
		if (thread != null) {
			return false;
		} else {
			if (status == 3) {
				clear();
			}
			status = 1;
			thread = new Thread(new Runnable() {
				public void run() {
					Explorer explorer = collector.getExplorer();
					List<Bill> bills = null;
					boolean lastTime = false;
					while (status == 1) {
						if (explorer.getStatus() == 1) {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							bills = collector.findBill(lastProcessedBillIndex + 1);
						} else {
							bills = collector.findBill(lastProcessedBillIndex + 1);
							lastTime = true;
						}

						for (int i = 0; i < bills.size() && status == 1; i++) {
							Bill bill = bills.get(i);
							bill.setStatus(1);
							String postUrl = bill.getPostUrl();
							String enterpriseUrl = bill.getEnterpriseUrl();

							Document document = documentClient.get(postUrl);
							final AttributeCatcher postAttributeCatcher = new AttributeCatcher(document, postAttributeSelectors);
							final AttributeCatcher enterpriseAttributeCatcher = new AttributeCatcher(document, enterpriseAttributeSelectors);
							final Map<String, String> postAttributes = new HashMap<String, String>();
							final Map<String, String> enterpriseAttributes = new HashMap<String, String>();
							document.traverse(new NodeVisitor() {
								public void head(Node node, int depth) {
									if (node instanceof Element) {
										Element element = (Element) node;
										// postAttributes.putAll(postAttributeCatcher.attempt(element));
										// enterpriseAttributes.putAll(enterpriseAttributeCatcher.attempt(element));
										Map<String, String> postAttrs = postAttributeCatcher.attempt(element);
										if (postAttrs.size() > 0)
											for (String attrName : postAttrs.keySet()) {
												String attrValue = postAttributes.get(attrName);
												if (attrValue != null)
													postAttributes.put(attrName, String.format("%s %s", attrValue, postAttrs.get(attrName)));
												else
													postAttributes.put(attrName, postAttrs.get(attrName));
											}
										Map<String, String> enterpriseAttrs = enterpriseAttributeCatcher.attempt(element);
										if (enterpriseAttrs.size() > 0)
											for (String attrName : enterpriseAttrs.keySet()) {
												String attrValue = enterpriseAttributes.get(attrName);
												if (attrValue != null)
													enterpriseAttributes.put(attrName, String.format("%s %s", attrValue, enterpriseAttrs.get(attrName)));
												else
													enterpriseAttributes.put(attrName, enterpriseAttrs.get(attrName));
											}
									}
								}

								public void tail(Node node, int depth) {
								}
							});

							enterpriseAttributes.put("url", enterpriseUrl);
							enterpriseAttributes.put("date", postAttributes.get("date"));
							postAttributes.put("url", postUrl);
							postAttributes.put("enterpriseUrl", enterpriseUrl);
							postAttributes.put("enterpriseName", enterpriseAttributes.get("name"));

							Enterprise enterprise = toEnterprise(enterpriseAttributes);
							Post post = toPost(postAttributes);

							collector.saveEnterprise(enterprise);
							collector.savePost(post);

							if (enterprise.getStatus() == -1)
								post.setStatus(-1);

							if (post.getStatus() != -1) {
								if (post.getAreaCode() == null)
									post.setAreaCode(enterprise.getAreaCode());
								if (post.getAddress() == null)
									post.setAddress(enterprise.getAddress());
								if (post.getLbsLon() == null)
									post.setLbsLon(enterprise.getLbsLon());
								if (post.getLbsLat() == null)
									post.setLbsLat(enterprise.getLbsLat());

								if (!enterpriseUrlIndexes.containsKey(enterprise.getDataUrl())) {
									enterprises.add(enterprise);
									enterpriseUrlIndexes.put(enterprise.getDataUrl(), enterprises.size() - 1);
								}

								if (enterpriseUrlIndexes.containsKey(post.getEnterpriseUrl())) {
									posts.add(post);
									bill.setStatus(2);
								}
							}

							lastProcessedBillIndex++;

							if (posts.size() >= 80) {
								commitPosts();
							}
						}

						if (lastTime) {
							commitPosts();
							finish();
						}
					}
				}
			});
			thread.start();
		}
		return true;
	}

	public boolean pause() {
		status = 2;
		thread = null;
		return true;
	}

	public boolean finish() {
		status = 3;
		thread = null;
		return true;
	}

	public boolean stop() {
		status = 0;
		thread = null;
		clear();
		return true;
	}

	public boolean clear() {
		collector.clearPost();
		collector.clearEnterprise();
		lastProcessedBillIndex = -1;
		posts.clear();
		enterprises.clear();
		return true;
	}

	public Integer getStatus() {
		return status;
	}

	private Post toPost(Map<String, String> map) {
		String dateString = map.get("date");
		Date date = Ver.nb(dateString) ? date = parseDate(dateString) : null;
		String url = map.get("url");
		String name = map.get("name");
		String category = map.get("category");
		String number = map.get("number");
		String nature = map.get("nature");
		String salary = map.get("salary");
		String experience = map.get("experience");
		String education = map.get("education");
		String welfare = map.get("welfare");
		String area = map.get("area");
		String address = map.get("address");
		String introduction = map.get("introduction");
		String enterpriseUrl = map.get("enterpriseUrl");
		String enterpriseName = map.get("enterpriseName");

		Map<String, String> categoryMapper = postAttributeMappers.get("category");
		Map<String, String> natureMapper = postAttributeMappers.get("nature");
		Map<String, String> experienceMapper = postAttributeMappers.get("experience");
		Map<String, String> educationMapper = postAttributeMappers.get("education");

		Post post = new Post();

		if (Ver.nb(name))
			post.setName(name);

		if (Ver.nb(category) && categoryMapper != null) {
			for (String cate : category.split("\\s+")) {
				String categoryCode = categoryMapper.get(cate);
				if (Ver.nb(categoryCode)) {
					Map<String, String> categoryMap = Holder.getPostCategoryCode(categoryCode);
					if (categoryMap != null) {
						post.setCategoryCode(categoryCode);
						post.setCategory(categoryMap.get("name"));
						break;
					}
				}
			}
		}

		if (Ver.bl(number)) {
			post.setNumber(1);
			post.setNumberText("1");
			post.setIsSeveral(0);
		} else if (number.contains("若干")) {
			post.setNumber(10);
			post.setNumberText("若干");
			post.setIsSeveral(1);
		} else {
			number = number.replaceAll("[^\\d]", "");
			if (number.matches("^\\d+$")) {
				post.setNumber(Integer.parseInt(number));
				post.setNumberText(number);
				post.setIsSeveral(0);
			} else {
				post.setNumber(1);
				post.setNumberText("1");
				post.setIsSeveral(0);
			}
		}

		String natureCode = Ver.bl(nature) || natureMapper == null ? "007.001" : natureMapper.get(nature);
		if (Ver.nb(natureCode)) {
			String natureName = Holder.getPostNatureCode(natureCode);
			if (Ver.nb(natureName)) {
				post.setNatureCode(natureCode);
				post.setNature(natureName);
			}
		}

		if (Ver.bl(salary) || salary.contains("面议")) {
			post.setSalary("0");
			post.setSalaryText("面议");
		} else {
			boolean unit = salary.contains("万");
			boolean year = salary.contains("年");
			salary = salary.replaceAll("[^\\d-]", "");
			String[] strs = salary.split("-");
			List<Integer> sals = new ArrayList<Integer>();
			for (String str : strs)
				if (Ver.nb(str))
					sals.add(Integer.valueOf(str));
			if (unit)
				for (int i = 0; i < sals.size(); i++)
					sals.set(i, sals.get(i) * 10000);
			if (year)
				for (int i = 0; i < sals.size(); i++)
					sals.set(i, new Double(Math.rint(sals.get(i) / 1200.0)).intValue() * 100);
			salary = StringUtils.join(sals, "-");
			post.setSalary(salary);
			post.setSalaryText(salary);
		}

		String experienceCode = Ver.bl(experience) || experienceMapper == null ? "005.009" : experienceMapper.get(experience);
		if (Ver.nb(experienceCode)) {
			String experienceName = Holder.getPostExperienceCode(experienceCode);
			if (Ver.nb(experienceName)) {
				post.setExperience(experienceName);
				post.setExperienceCode(experienceCode);
			}
		}

		String educationCode = Ver.bl(education) || educationMapper == null ? "004.011" : educationMapper.get(education);
		if (Ver.nb(educationCode)) {
			String educationName = Holder.getPostEducationCode(educationCode);
			if (Ver.nb(educationName)) {
				post.setEducation(educationName);
				post.setEducationCode(educationCode);
			}
		}

		if (Ver.nb(welfare)) {
			String[] welNames = welfare.split("\\s+");
			String[] welCodes = new String[welNames.length];
			for (int i = 0; i < welNames.length; i++) {
				String code = Holder.getTagCode(welNames[i]);
				welCodes[i] = Ver.nb(code) ? String.format("system&%s&%s", code, welNames[i]) : String.format("self&%s", welNames[i]);
			}
			post.setWelfare(StringUtils.join(welNames, " "));
			post.setWelfareCode(StringUtils.join(welCodes, "&&"));
		}

		if (Ver.nb(area) && Ver.nb(address)) {
			boolean done = false;
			boolean include = false;
			String[] strs = area.split("\\s*-\\s*");
			for (int i = strs.length - 1; i >= 0; i--) {
				if (address.startsWith(strs[i])) {
					address = StringUtils.join(strs, "", 0, i) + address;
					done = true;
					break;
				} else {
					if (address.contains(strs[i]))
						include = true;
				}
			}
			if (!done && !include)
				address = StringUtils.join(strs, "") + address;
		}

		if (Ver.nb(address)) {
			address = address.replaceAll("\\s+", "");
			post.setAddress(address);
			String areaCode = Holder.getAreaCode(address);
			if (Ver.nb(areaCode)) {
				post.setAreaCode(areaCode);
				Double[] point = lbsClient.getPoint(address);
				if (point != null) {
					post.setLbsLon(point[0]);
					post.setLbsLat(point[1]);
				}
			}
		}

		if (Ver.nb(introduction))
			post.setIntroduction(introduction);

		if (Ver.nb(collector.getNid()))
			post.setDataSrc(collector.getNid());

		if (Ver.nb(url))
			post.setDataUrl(url);

		if (date != null) {
			post.setUpdateDate(date);
			post.setPublishDate(date);
		} else {
			Date now = new Date();
			post.setUpdateDate(now);
			post.setPublishDate(now);
		}

		if (Ver.nb(enterpriseUrl))
			post.setEnterpriseUrl(enterpriseUrl);

		if (Ver.nb(enterpriseName))
			post.setEnterpriseName(enterpriseName);

		Holder.mergePost(post);

		if (Ver.bl(post.getName()) || Ver.bl(post.getCategoryCode()) || Ver.bl(post.getNatureCode()) || Ver.bl(post.getExperienceCode()) || Ver.bl(post.getEducationCode()) || Ver.bl(post.getDataSrc()) || Ver.bl(post.getDataUrl()) || post.getUpdateDate() == null || post.getPublishDate() == null || Ver.bl(post.getEnterpriseUrl()) || Ver.bl(post.getEnterpriseName()))
			post.setStatus(-1);

		return post;
	}

	private Enterprise toEnterprise(Map<String, String> map) {
		String dateString = map.get("date");
		Date date = Ver.nb(dateString) ? date = parseDate(dateString) : null;
		String url = map.get("url");
		String name = map.get("name");
		String category = map.get("category");
		String nature = map.get("nature");
		String scale = map.get("scale");
		String introduction = map.get("introduction");
		String website = map.get("website");
		String address = map.get("address");

		Map<String, String> categoryMapper = enterpriseAttributeMappers.get("category");
		Map<String, String> natureMapper = enterpriseAttributeMappers.get("nature");
		Map<String, String> scaleMapper = enterpriseAttributeMappers.get("scale");

		Enterprise enterprise = new Enterprise();

		if (Ver.nb(name))
			enterprise.setName(name);

		if (Ver.nb(category) && categoryMapper != null) {
			for (String cate : category.split("\\s+")) {
				String categoryCode = categoryMapper.get(cate);
				if (Ver.nb(categoryCode)) {
					String categoryName = Holder.getEnterpriseCategoryCode(categoryCode);
					if (Ver.nb(categoryName)) {
						enterprise.setCategoryCode(categoryCode);
						enterprise.setCategory(categoryName);
						break;
					}
				}
			}
		}

		String natureCode = Ver.bl(nature) || natureMapper == null ? "010.006" : natureMapper.get(nature);
		if (Ver.nb(natureCode)) {
			String natureName = Holder.getEnterpriseNatureCode(natureCode);
			if (Ver.nb(natureName)) {
				enterprise.setNatureCode(natureCode);
				enterprise.setNature(natureName);
			}
		}

		String scaleCode = Ver.bl(scale) || scaleMapper == null ? "011.001" : scaleMapper.get(scale);
		if (Ver.nb(scaleCode)) {
			String scaleName = Holder.getEnterpriseScaleCode(scaleCode);
			if (Ver.nb(scaleName)) {
				enterprise.setScaleCode(scaleCode);
				enterprise.setScale(scaleName);
			}
		}

		if (Ver.nb(introduction))
			enterprise.setIntroduction(introduction);

		if (Ver.nb(website))
			enterprise.setWebsite(website);

		if (Ver.nb(address)) {
			address = address.replaceAll("\\s+", "").replaceAll("^地址：", "");
			enterprise.setAddress(address);
			String areaCode = Holder.getAreaCode(address);
			if (Ver.nb(areaCode)) {
				enterprise.setAreaCode(areaCode);
				Double[] point = lbsClient.getPoint(address);
				if (point != null) {
					enterprise.setLbsLon(point[0]);
					enterprise.setLbsLat(point[1]);
				}
			}
		}

		if (Ver.nb(collector.getNid()))
			enterprise.setDataSrc(collector.getNid());

		if (Ver.nb(url))
			enterprise.setDataUrl(url);

		if (date != null)
			enterprise.setCreateDate(date);
		else
			enterprise.setCreateDate(new Date());

		Holder.mergeEnterprise(enterprise);

		if (Ver.bl(enterprise.getName()) || Ver.bl(enterprise.getCategoryCode()) || Ver.bl(enterprise.getNatureCode()) || Ver.bl(enterprise.getScaleCode()) || Ver.bl(enterprise.getAreaCode()) || Ver.bl(enterprise.getAddress()) || enterprise.getLbsLon() == null || enterprise.getLbsLat() == null || Ver.bl(enterprise.getDataSrc()) || Ver.bl(enterprise.getDataUrl()) || enterprise.getCreateDate() == null)
			enterprise.setStatus(-1);

		return enterprise;
	}

	private void commitPosts() {
		Holder.saveEnterprise(enterprises);
		enterprises.clear();
		enterpriseUrlIndexes.clear();

		Holder.savePost(posts);
		collector.getPostCluster().saveAll(posts);
		posts.clear();
	}

	private static Date parseDate(String dateString) {
		try {
			String[] strs = dateString.split("-|/");
			if (strs.length == 2) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(calendar.get(Calendar.YEAR), Integer.parseInt(strs[0]) - 1, Integer.parseInt(strs[1]), 0, 0, 0);
				return calendar.getTime();
			} else if (strs.length == 3) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]) - 1, Integer.parseInt(strs[2]), 0, 0, 0);
				return calendar.getTime();
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

}
