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
		Date date = StringUtils.isNotBlank(dateString) ? date = parseDate(dateString) : null;
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
		Map<String, String> numberMapper = postAttributeMappers.get("number");
		Map<String, String> natureMapper = postAttributeMappers.get("nature");
		Map<String, String> salaryMapper = postAttributeMappers.get("salary");
		Map<String, String> experienceMapper = postAttributeMappers.get("experience");
		Map<String, String> educationMapper = postAttributeMappers.get("education");

		Post post = new Post();

		if (StringUtils.isNotBlank(name))
			post.setName(name);

		if (StringUtils.isNotBlank(category) && categoryMapper != null) {
			for (String cate : category.split("\\s+")) {
				String categoryCode = categoryMapper.get(cate);
				if (StringUtils.isNotBlank(categoryCode)) {
					Map<String, String> categoryMap = Holder.getPostCategoryCode(categoryCode);
					if (categoryMap != null) {
						post.setCategoryCode(categoryCode);
						post.setCategory(categoryMap.get("name"));
						break;
					}
				}
			}
		}

		if (StringUtils.isBlank(number) || numberMapper == null) {
			post.setNumber(1);
			post.setNumberText("1");
			post.setIsSeveral(0);
		} else {
			String num = numberMapper.get(number);
			if (StringUtils.isBlank(num)) {
				post.setNumber(number.matches("^\\d+$") ? Integer.parseInt(number) : 3);
				post.setNumberText(String.valueOf(number));
				post.setIsSeveral(0);
			} else {
				post.setNumber(number.matches("^\\d+$") ? Integer.parseInt(num) : 10);
				post.setNumberText("若干");
				post.setIsSeveral(1);
			}
		}

		String natureCode = StringUtils.isBlank(nature) || natureMapper == null ? "007.001" : natureMapper.get(nature);
		if (StringUtils.isNotBlank(natureCode)) {
			String natureName = Holder.getPostNatureCode(natureCode);
			if (natureName != null) {
				post.setNatureCode(natureCode);
				post.setNature(natureName);
			}
		}

		if (StringUtils.isBlank(salary)) {
			post.setSalary("0");
			post.setSalaryText("面议");
		} else {
			salary = salary.replaceAll("[^\\d-+]", "");
			post.setSalary(salary);
			post.setSalaryText(salary);
			if (salaryMapper != null) {
				String num = salaryMapper.get(salary);
				if (StringUtils.isNotBlank(num)) {
					post.setSalary(num);
					post.setSalaryText(num);
				}
			}
		}

		String experienceCode = StringUtils.isBlank(experience) || experienceMapper == null ? "005.009" : experienceMapper.get(experience);
		if (StringUtils.isNotBlank(experienceCode)) {
			String experienceName = Holder.getPostExperienceCode(experienceCode);
			if (experienceName != null) {
				post.setExperience(experienceName);
				post.setExperienceCode(experienceCode);
			}
		}

		String educationCode = StringUtils.isBlank(education) || educationMapper == null ? "004.011" : educationMapper.get(education);
		if (StringUtils.isNotBlank(educationCode)) {
			String educationName = Holder.getPostEducationCode(educationCode);
			if (educationName != null) {
				post.setEducation(educationName);
				post.setEducationCode(educationCode);
			}
		}

		if (StringUtils.isNotBlank(welfare)) {
			String[] wels = welfare.split("\\s+");
			String[] welNames = new String[wels.length];
			String[] welCodes = new String[wels.length];
			for (int i = 0; i < wels.length; i++) {
				String code = Holder.getTagCode(wels[i]);
				welNames[i] = wels[i];
				if (code == null) {
					welCodes[i] = String.format("self&%s", wels[i]);
				} else {
					welCodes[i] = String.format("system&%s&%s", code, wels[i]);
				}
			}
			post.setWelfare(StringUtils.join(welNames, " "));
			post.setWelfareCode(StringUtils.join(welCodes, "&&"));
		}

		if (Ver.nb(area) && Ver.nb(address)) {
			area = area.replaceAll("\\s+|-", "");
			address = area + address;
			post.setAddress(address);
			String areaCode = Holder.getAreaCode(address);
			if (areaCode != null) {
				post.setAreaCode(areaCode);
				Double[] point = lbsClient.getPoint(address);
				if (point != null) {
					post.setLbsLon(point[0]);
					post.setLbsLat(point[1]);
				}
			}
		}

		if (StringUtils.isNotBlank(introduction))
			post.setIntroduction(introduction);

		if (StringUtils.isNotBlank(collector.getNid()))
			post.setDataSrc(collector.getNid());

		if (StringUtils.isNotBlank(url))
			post.setDataUrl(url);

		if (date != null) {
			post.setUpdateDate(date);
			post.setPublishDate(date);
		} else {
			Date now = new Date();
			post.setUpdateDate(now);
			post.setPublishDate(now);
		}

		if (StringUtils.isNotBlank(enterpriseUrl))
			post.setEnterpriseUrl(enterpriseUrl);

		if (StringUtils.isNotBlank(enterpriseName))
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

		if (StringUtils.isNotBlank(name))
			enterprise.setName(name);

		if (StringUtils.isNotBlank(category) && categoryMapper != null) {
			for (String cate : category.split("\\s+")) {
				String categoryCode = categoryMapper.get(cate);
				if (StringUtils.isNotBlank(categoryCode)) {
					String categoryName = Holder.getEnterpriseCategoryCode(categoryCode);
					if (categoryName != null) {
						enterprise.setCategoryCode(categoryCode);
						enterprise.setCategory(categoryName);
						break;
					}
				}
			}
		}

		String natureCode = StringUtils.isBlank(nature) || natureMapper == null ? "010.006" : natureMapper.get(nature);
		if (StringUtils.isNotBlank(natureCode)) {
			String natureName = Holder.getEnterpriseNatureCode(natureCode);
			if (natureName != null) {
				enterprise.setNatureCode(natureCode);
				enterprise.setNature(natureName);
			}
		}

		String scaleCode = StringUtils.isBlank(scale) || scaleMapper == null ? "011.001" : scaleMapper.get(scale);
		if (StringUtils.isNotBlank(scaleCode)) {
			String scaleName = Holder.getEnterpriseScaleCode(scaleCode);
			if (scaleName != null) {
				enterprise.setScaleCode(scaleCode);
				enterprise.setScale(scaleName);
			}
		}

		if (StringUtils.isNotBlank(introduction))
			enterprise.setIntroduction(introduction);

		if (StringUtils.isNotBlank(website))
			enterprise.setWebsite(website);

		if (StringUtils.isNotBlank(address)) {
			address = address.replaceAll("^地 址：", "");
			enterprise.setAddress(address);
			String areaCode = Holder.getAreaCode(address);
			if (areaCode != null) {
				enterprise.setAreaCode(areaCode);
				Double[] point = lbsClient.getPoint(address);
				if (point != null) {
					enterprise.setLbsLon(point[0]);
					enterprise.setLbsLat(point[1]);
				}
			}
		}

		if (StringUtils.isNotBlank(collector.getNid()))
			enterprise.setDataSrc(collector.getNid());

		if (StringUtils.isNotBlank(url))
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
