package crawler.post;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

import crawler.AttributeCatcher;
import crawler.Client;
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
	private Map<String, Integer> enterpriseURLIndexes = new HashMap<String, Integer>();

	public Processor(Collector collector) {
		this.collector = collector;

		Map<String, Object> postNorm = Heap.getPostNorm(collector.getNorm());
		Map<String, Object> enterpriseNorm = Heap.getEnterpriseNorm(collector.getNorm());

		postUpdateInterval = (Integer) postNorm.get("updateInterval");
		postAttributeSelectors = (Map<String, String>) postNorm.get("attribute");
		postAttributeMappers = (Map<String, Map<String, String>>) postNorm.get("mapper");

		enterpriseUpdateInterval = (Integer) enterpriseNorm.get("updateInterval");
		enterpriseAttributeSelectors = (Map<String, String>) enterpriseNorm.get("attribute");
		enterpriseAttributeMappers = (Map<String, Map<String, String>>) enterpriseNorm.get("mapper");
	}

	public Boolean start() {
		if (null != thread) {
			return false;
		} else {
			if (3 == status) {
				clear();
			}
			status = 1;
			thread = new Thread(new Runnable() {
				public void run() {
					Explorer explorer = collector.getExplorer();
					List<Bill> bills = null;
					Boolean lastTime = false;
					while (1 == status) {
						if (1 == explorer.getStatus()) {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							bills = Heap.findBill(collector.getId(), lastProcessedBillIndex + 1);
						} else {
							bills = Heap.findBill(collector.getId(), lastProcessedBillIndex + 1);
							lastTime = true;
						}

						for (int i = 0; i < bills.size() && 1 == status; i++) {
							Bill bill = bills.get(i);
							bill.setStatus(1);
							String postURL = bill.getPostURL();
							String enterpriseURL = bill.getEnterpriseURL();

							Document document = Client.get(postURL);
							final AttributeCatcher postAttributeCatcher = new AttributeCatcher(document, postAttributeSelectors);
							final AttributeCatcher enterpriseAttributeCatcher = new AttributeCatcher(document, enterpriseAttributeSelectors);
							final Map<String, String> postAttributes = new HashMap<String, String>();
							final Map<String, String> enterpriseAttributes = new HashMap<String, String>();
							document.traverse(new NodeVisitor() {
								public void head(Node node, int depth) {
									if (node instanceof Element) {
										Element element = (Element) node;
										postAttributes.putAll(postAttributeCatcher.attempt(element));
										enterpriseAttributes.putAll(enterpriseAttributeCatcher.attempt(element));
									}
								}

								public void tail(Node node, int depth) {
								}
							});

							postAttributes.put("url", postURL);
							postAttributes.put("enterpriseURL", enterpriseURL);
							enterpriseAttributes.put("url", enterpriseURL);
							enterpriseAttributes.put("date", postAttributes.get("date"));

							Post post = toPost(postAttributes);
							if (null != post) {
								Enterprise enterprise = toEnterprise(enterpriseAttributes);
								if (null != enterprise) {
									if (Holder.existEnterpriseAccount(enterprise.getName())) {
										logger.info(String.format("the enterprise has account, %s [date=%s, name=%s,]", enterprise.getURL(), enterprise.getDate(), enterprise.getName()));
									} else {
										Heap.saveEnterprise(collector.getId(), enterprise);
										Heap.savePost(collector.getId(), post);
										if (!enterpriseURLIndexes.containsKey(enterprise.getURL())) {
											enterprises.add(enterprise);
											enterpriseURLIndexes.put(enterprise.getURL(), enterprises.size() - 1);
										}
										if (enterpriseURLIndexes.containsKey(post.getEnterpriseURL())) {
											posts.add(post);
											bill.setStatus(2);
										}
									}
								}
							}

							lastProcessedBillIndex++;

							if (80 <= posts.size()) {
								commitPosts();
							}
						}

						if (true == lastTime) {
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
		Heap.clearPosts(collector.getId());
		Heap.clearEnterprises(collector.getId());
		lastProcessedBillIndex = -1;
		posts.clear();
		enterprises.clear();
		return true;
	}

	public Integer getStatus() {
		return status;
	}

	private Post toPost(Map<String, String> map) {
		String url = map.get("url");
		String dateString = map.get("date");
		Date date = null;
		if (StringUtils.isNotBlank(dateString)) {
			date = parseDate(dateString);
		}
		String name = map.get("name");
		String category = map.get("category");
		String number = map.get("number");
		String nature = map.get("nature");
		String salary = map.get("salary");
		String experience = map.get("experience");
		String education = map.get("education");
		String welfare = map.get("welfare");
		String address = map.get("address");
		String introduction = map.get("introduction");
		String enterpriseURL = map.get("enterpriseURL");

		if (StringUtils.isBlank(url) || null == date || StringUtils.isBlank(name) || StringUtils.isBlank(category) || StringUtils.isBlank(enterpriseURL)) {
			logger.error(String.format("post: miss base info, %s [date=%s, name=%s, category=%s, enterpriseURL=%s]", url, date, name, category, enterpriseURL));
			return null;
		} else {
			Map<String, String> categoryMapper = postAttributeMappers.get("category");
			Map<String, String> numberMapper = postAttributeMappers.get("number");
			Map<String, String> natureMapper = postAttributeMappers.get("nature");
			Map<String, String> salaryMapper = postAttributeMappers.get("salary");
			Map<String, String> experienceMapper = postAttributeMappers.get("experience");
			Map<String, String> educationMapper = postAttributeMappers.get("education");

			Post post = new Post();

			post.setURL(url);
			post.setDate(date);
			post.setName(name);

			if (category != null && categoryMapper != null) {
				for (String cate : category.split("\\s+")) {
					post.setCategoryCode(categoryMapper.get(cate));
					if (post.getCategoryCode() != null) {
						post.setCategory(Holder.getPostCategory(post.getCategoryCode()));
						break;
					}
				}
			}

			if (StringUtils.isBlank(number) || numberMapper == null) {
				post.setNumber(1);
				post.setNumberText("1");
				post.setIsSeveral(0);
			} else {
				String num = numberMapper.get(number);
				if (num == null) {
					post.setNumber(number.matches("^\\d+$") ? Integer.parseInt(number) : 3);
					post.setNumberText(String.valueOf(number));
					post.setIsSeveral(0);
				} else {
					post.setNumber(number.matches("^\\d+$") ? Integer.parseInt(num) : 10);
					post.setNumberText("若干");
					post.setIsSeveral(1);
				}
			}

			if (StringUtils.isBlank(nature) || natureMapper == null) {
				post.setNatureCode("007.001");
			} else {
				post.setNatureCode(natureMapper.get(nature));
			}
			if (post.getNatureCode() != null) {
				post.setNature(Holder.getPostNature(post.getNatureCode()));
			}

			if (StringUtils.isBlank(salary)) {
				post.setSalary("0");
				post.setSalaryText("面议");
			} else {
				post.setSalary(salary);
				post.setSalaryText(salary);
				if (salaryMapper != null) {
					String num = salaryMapper.get(salary);
					if (num != null) {
						post.setSalary(num);
						post.setSalaryText(num);
					}
				}
			}

			if (StringUtils.isBlank(experience) || null == experienceMapper) {
				post.setExperienceCode("004.011");
			} else {
				post.setExperienceCode(experienceMapper.get(experience));
			}
			if (null != post.getExperienceCode()) {
				post.setExperienceAbility(Holder.getPostExperience(post.getExperienceCode()));
				if (null != post.getExperienceAbility()) {
					post.setExperience(post.getExperienceAbility().get("paramName"));
				}
			}

			if (StringUtils.isBlank(education) || null == educationMapper) {
				post.setEducationCode("005.009");
			} else {
				post.setEducationCode(educationMapper.get(education));
			}
			if (null != post.getEducationCode()) {
				post.setEducationAbility(Holder.getPostEducation(post.getEducationCode()));
				if (null != post.getEducationAbility()) {
					post.setEducation(post.getEducationAbility().get("paramName"));
				}
			}

			if (StringUtils.isNotBlank(welfare)) {
				String[] wels = welfare.split("\\s+");
				String[] welNames = new String[wels.length];
				String[] welCodes = new String[wels.length];
				for (int i = 0; i < wels.length; i++) {
					String code = Holder.getTagCode(wels[i]);
					welNames[i] = wels[i];
					if (null == code) {
						welCodes[i] = String.format("self&%s", wels[i]);
					} else {
						welCodes[i] = String.format("system&%s&%s", code, wels[i]);
					}
				}
				post.setWelfare(StringUtils.join(welNames, " "));
				post.setWelfareCode(StringUtils.join(welCodes, "&&"));
			}

			if (StringUtils.isNotBlank(address)) {
				post.setAddress(address);
			}

			if (StringUtils.isNotBlank(introduction)) {
				post.setIntroduction(introduction);
			}

			post.setEnterpriseURL(enterpriseURL);

			return post;
		}
	}

	private Enterprise toEnterprise(Map<String, String> map) {
		String url = map.get("url");
		String dateString = map.get("date");
		Date date = null;
		if (null != dateString) {
			date = parseDate(dateString);
		}
		String name = map.get("name");
		String category = map.get("category");
		String nature = map.get("nature");
		String scale = map.get("scale");
		String website = map.get("website");
		String address = map.get("address");
		String introduction = map.get("introduction");

		if (StringUtils.isBlank(url) || null == date || StringUtils.isBlank(name) || StringUtils.isBlank(category) || StringUtils.isBlank(address)) {
			logger.error(String.format("enterprise: miss base info, %s [date=%s, name=%s, category=%s, address=%s]", url, date, name, category, address));
			return null;
		} else {
			Map<String, String> categoryMapper = enterpriseAttributeMappers.get("category");
			Map<String, String> natureMapper = enterpriseAttributeMappers.get("nature");
			Map<String, String> scaleMapper = enterpriseAttributeMappers.get("scale");

			Enterprise enterprise = new Enterprise();

			enterprise.setURL(url);
			enterprise.setDate(date);
			enterprise.setName(name);

			String[] cates = category.split("\\s+");
			if (null != categoryMapper) {
				for (String cate : cates) {
					enterprise.setCategoryCode(categoryMapper.get(cate));
					if (null != enterprise.getCategoryCode()) {
						enterprise.setCategory(Holder.getEnterpriseCategory(enterprise.getCategoryCode()));
						break;
					}
				}
			}
			if (null == enterprise.getCategoryCode()) {
				logger.error(String.format("enterprise: miss category code, %s [date=%s, name=%s, category=%s]", url, date, name, category));
				return null;
			}

			if (StringUtils.isBlank(nature) || null == natureMapper) {
				enterprise.setNatureCode("010.006");
			} else {
				enterprise.setNatureCode(natureMapper.get(nature));
			}
			if (null != enterprise.getNatureCode()) {
				enterprise.setNature(Holder.getEnterpriseNature(enterprise.getNatureCode()));
			}

			if (StringUtils.isBlank(scale) || null == scaleMapper) {
				enterprise.setScaleCode("011.001");
			} else {
				enterprise.setScaleCode(scaleMapper.get(scale));
			}
			if (null != enterprise.getScaleCode()) {
				enterprise.setScale(Holder.getEnterpriseScale(enterprise.getScaleCode()));
			}

			if (StringUtils.isNotBlank(website)) {
				enterprise.setWebsite(website);
			}

			address = address.replaceAll("^地 址：", "");
			Integer provinceIndex = address.indexOf("省");
			Integer cityIndex = address.indexOf("市");
			if (-1 != cityIndex) {
				String area = null;
				if (-1 == provinceIndex || provinceIndex > cityIndex) {
					area = address.substring(0, cityIndex + 1);
				} else {
					area = address.substring(provinceIndex + 1, cityIndex + 1);
				}
				enterprise.setAreaCode(Holder.getAreaCode(area));
			}
			Double[] point = Client.getPoint(address);
			if (null != point) {
				enterprise.setLBSLon(point[0]);
				enterprise.setLBSLat(point[1]);
			} else {
				logger.error(String.format("enterprise: miss lonlat info, %s [date=%s, name=%s, category=%s, address=%s]", url, date, name, category, address));
				return null;
			}
			enterprise.setAddress(address);

			if (StringUtils.isNotBlank(introduction)) {
				enterprise.setIntroduction(introduction);
			}

			return enterprise;
		}
	}

	private void commitPosts() {
		Holder.saveOrUpdateEnterprises(enterprises, enterpriseUpdateInterval);
		enterprises.clear();
		enterpriseURLIndexes.clear();

		Holder.saveOrUpdatePosts(posts, postUpdateInterval);
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
