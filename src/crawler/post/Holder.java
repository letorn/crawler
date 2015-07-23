package crawler.post;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import map.Cluster;

import org.apache.log4j.Logger;

import util.Ver;
import crawler.post.model.Enterprise;
import crawler.post.model.Post;
import dao.data.C3P0Store;

public class Holder extends C3P0Store {

	private static Logger logger = Logger.getLogger(Holder.class);

	private static Map<String, String> areaNameMap = new ConcurrentHashMap<String, String>();

	private static Map<String, String> tagCodeMap = new ConcurrentHashMap<String, String>();
	private static Map<String, String> tagNameMap = new ConcurrentHashMap<String, String>();

	private static Map<String, Map<String, Object>> technologyCodeMap = new ConcurrentHashMap<String, Map<String, Object>>();
	private static Map<String, Map<String, Object>> abilityParamCodeMap = new ConcurrentHashMap<String, Map<String, Object>>();
	
	private static Map<String, Map<String, String>> postCategoryCodeMap = new ConcurrentHashMap<String, Map<String, String>>();
	private static Map<String, String> postNatureCodeMap = new ConcurrentHashMap<String, String>();
	private static Map<String, String> postExperienceCodeMap = new ConcurrentHashMap<String, String>();
	private static Map<String, String> postEducationCodeMap = new ConcurrentHashMap<String, String>();

	private static Map<String, String> enterpriseCategoryCodeMap = new ConcurrentHashMap<String, String>();
	private static Map<String, String> enterpriseNatureCodeMap = new ConcurrentHashMap<String, String>();
	private static Map<String, String> enterpriseScaleCodeMap = new ConcurrentHashMap<String, String>();

	private static DecimalFormat accountNumFormat = new DecimalFormat("0000000");
	private static int lastAccountNum = -1;

	private static Cluster<Post> postCluster = new Cluster<Post>();
	private static Map<String, Integer> postUrlIndexes = new ConcurrentHashMap<String, Integer>();
	private static List<Post> postList = new ArrayList<Post>();
	private static ThreadLocal<Integer> localPostNameCount = new ThreadLocal<Integer>();
	private static Map<String, Map<String, Post>> postEntNameMap = new ConcurrentHashMap<String, Map<String, Post>>();
	private static Map<String, Enterprise> enterpriseUrlMap = new ConcurrentHashMap<String, Enterprise>();
	private static Map<String, Enterprise> enterpriseNameMap = new ConcurrentHashMap<String, Enterprise>();

	public void init() {
		logger.info("------ init holder ------");

		logger.info("binning tag ...");
		/*for (Tag tag : tagDao.findAll()) {
			tagNameMap.put(tag.getTagName(), tag.getTagCode());
			tagCodeMap.put(tag.getTagCode(), tag.getTagName());
		}*/
		selectResultSet("select tag_name, tag_code from zcdh_tag where is_delete=1 or is_delete is null", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int index) throws Exception {
				tagNameMap.put(resultSet.getString("tag_name"), resultSet.getString("tag_code"));
				tagCodeMap.put(resultSet.getString("tag_code"), resultSet.getString("tag_name"));
				return true;
			}
		});

		logger.info("binning area ...");
		/*for (Area area : areaDao.find(AreaType.CITY))
			if (!area.getAreaName().contains("行政"))
				areaNameMap.put(area.getAreaName().replaceAll("\\s+|市$|盟$|地区$|族$|自治州$|族自治州$", ""), area.getAreaCode());*/
		selectResultSet("select area_name, area_code from zcdh_area where (is_delete=1 or is_delete) and area_code regexp '^[0-9]{3}\\.[0-9]{3}$' and area_name not regexp '行政'", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int index) throws Exception {
				areaNameMap.put(resultSet.getString("area_name").replaceAll("\\s+|市$|盟$|地区$|族$|自治州$|族自治州$", ""), resultSet.getString("area_code"));
				return true;
			}
		});
		
		
		logger.info("binning post category ...");
		/*for (model.Post post : postDao.findAll()) {
			CategoryPost categoryPost = categoryPostDao.get(post.getPostCategoryCode());
			if (categoryPost != null) {
				Map<String, String> postCategorie = new HashMap<String, String>();
				postCategorie.put("name", post.getPostName());
				postCategorie.put("code", post.getPostCode());
				postCategorie.put("group", categoryPost.getPostCategoryName());
				postCategoryCodeMap.put(post.getPostCode(), postCategorie);
			}
		}*/
		selectResultSet("select p.post_name, p.post_code, cp.post_category_name from zcdh_post p join zcdh_category_post cp on p.post_category_code=cp.post_category_code where p.is_delete=1 or p.is_delete is null", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int index) throws Exception {
				Map<String, String> postCategorie = new HashMap<String, String>();
				postCategorie.put("name", resultSet.getString("post_name"));
				postCategorie.put("code", resultSet.getString("post_code"));
				postCategorie.put("group", resultSet.getString("post_category_name"));
				postCategoryCodeMap.put(resultSet.getString("post_code"), postCategorie);
				return true;
			}
		});
		
		logger.info("binning technology ...");
		selectResultSet("select t.technical_code, t.match_type, c.technology_gategory_code, c.percent from zcdh_technology t join zcdh_technology_gategory c on c.technology_gategory_code=t.techonlogy_gategory_code where (t.is_delete=1 or t.is_delete is null) and t.technical_code in('-0000000000003', '-0000000000004')", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int index) throws Exception {
				Map<String, Object> technology = new HashMap<String, Object>();
				technology.put("code", resultSet.getString("technical_code"));
				technology.put("matchType", resultSet.getInt("match_type"));
				technology.put("categoryCode", resultSet.getString("technology_gategory_code"));
				technology.put("percent", resultSet.getInt("percent"));
				technologyCodeMap.put(resultSet.getString("technical_code"), technology);
				return true;
			}
		});
		
		logger.info("binning param ...");
		/*for (Param param : paramDao.findAll()) {
			String categoryCode = param.getParamCategoryCode();
			if ("007".equals(categoryCode)) {
				postNatureCodeMap.put(param.getParamCode(), param.getParamName());
			} else if ("005".equals(categoryCode)) {
				postExperienceCodeMap.put(param.getParamCode(), param.getParamName());
			} else if ("004".equals(categoryCode)) {
				postEducationCodeMap.put(param.getParamCode(), param.getParamName());
			} else if ("010".equals(categoryCode)) {
				enterpriseNatureCodeMap.put(param.getParamCode(), param.getParamName());
			} else if ("011".equals(categoryCode)) {
				enterpriseScaleCodeMap.put(param.getParamCode(), param.getParamName());
			}
		}*/
		selectResultSet("select param_name, param_code, param_value, param_category_code from zcdh_param where (is_delete=1 or is_delete is null) and param_category_code in('007', '005', '004', '010', '011')", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int index) throws Exception {
				String categoryCode = resultSet.getString("param_category_code");
				if ("007".equals(categoryCode)) {
					postNatureCodeMap.put(resultSet.getString("param_code"), resultSet.getString("param_name"));
				} else if ("005".equals(categoryCode)) {
					postExperienceCodeMap.put(resultSet.getString("param_code"), resultSet.getString("param_name"));
					Map<String, Object> technology = technologyCodeMap.get("-0000000000003");
					Map<String, Object> abilityParam = new HashMap<String, Object>();
					abilityParam.put("value", resultSet.getInt("param_value"));
					abilityParam.put("technologyCode", technology.get("code"));
					abilityParam.put("matchType", technology.get("matchType"));
					abilityParam.put("technologyCategoryCode", technology.get("categoryCode"));
					abilityParam.put("percent", technology.get("percent"));
					abilityParamCodeMap.put(resultSet.getString("param_code"), abilityParam);
				} else if ("004".equals(categoryCode)) {
					postEducationCodeMap.put(resultSet.getString("param_code"), resultSet.getString("param_name"));
					Map<String, Object> technology = technologyCodeMap.get("-0000000000004");
					Map<String, Object> abilityParam = new HashMap<String, Object>();
					abilityParam.put("value", resultSet.getInt("param_value"));
					abilityParam.put("technologyCode", technology.get("code"));
					abilityParam.put("matchType", technology.get("matchType"));
					abilityParam.put("technologyCategoryCode", technology.get("categoryCode"));
					abilityParam.put("percent", technology.get("percent"));
					abilityParamCodeMap.put(resultSet.getString("param_code"), abilityParam);
				} else if ("010".equals(categoryCode)) {
					enterpriseNatureCodeMap.put(resultSet.getString("param_code"), resultSet.getString("param_name"));
				} else if ("011".equals(categoryCode)) {
					enterpriseScaleCodeMap.put(resultSet.getString("param_code"), resultSet.getString("param_name"));
				}
				return true;
			}
		});
		
		logger.info("binning enterprise category ...");
		/*for (Industry industry : industryDao.findAll())
			enterpriseCategoryCodeMap.put(industry.getIndustryCode(), industry.getIndustryName());*/
		selectResultSet("select industry_name, industry_code from zcdh_industry where (is_delete=1 or is_delete is null) and industry_code regexp '^[0-9]{3}\\.[0-9]{3}$'", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int index) throws Exception {
				enterpriseCategoryCodeMap.put(resultSet.getString("industry_code"), resultSet.getString("industry_name"));
				return true;
			}
		});
		
		logger.info("binning last account num ...");
		// lastAccountNum = entAccountDao.getLastAutoAccountNum();
		String lastAccount = selectString("select account from zcdh_ent_account where account regexp '^zcdh[0-9]{7}$' order by create_date desc limit 0,1");
		if (lastAccount != null) 
			lastAccountNum = Integer.valueOf(lastAccount.replaceAll("zcdh", ""));

		logger.info("binning enterprise ...");
		/*for (EntEnterprise entEnterprise : entEnterpriseDao.findAll()) {
			Enterprise enterprise = new Enterprise();
			enterprise.setId(entEnterprise.getEntId());
			enterprise.setName(entEnterprise.getEntName());
			enterprise.setCategoryCode(entEnterprise.getIndustry());
			if (enterprise.getCategoryCode() != null)
				enterprise.setCategory(enterpriseCategoryCodeMap.get(enterprise.getCategoryCode()));
			enterprise.setNatureCode(entEnterprise.getProperty());
			if (enterprise.getNatureCode() != null)
				enterprise.setNature(enterpriseNatureCodeMap.get(enterprise.getNatureCode()));
			enterprise.setScaleCode(entEnterprise.getEmployNum());
			if (enterprise.getScaleCode() != null)
				enterprise.setScale(enterpriseScaleCodeMap.get(enterprise.getScaleCode()));
			enterprise.setIntroduction(entEnterprise.getIntroduction());
			enterprise.setWebsite(entEnterprise.getEntWeb());
			enterprise.setAreaCode(entEnterprise.getParea());
			enterprise.setAddress(entEnterprise.getAddress());
			if (entEnterprise.getLbsId() != null) {
				EntLbs entLbs = entLbsDao.get(entEnterprise.getLbsId());
				if (entLbs != null) {
					enterprise.setLbsId(entLbs.getLbsId());
					enterprise.setLbsLon(entLbs.getLongitude());
					enterprise.setLbsLat(entLbs.getLatitude());
				}
			}
			enterprise.setDataSrc(entEnterprise.getDataSrc());
			enterprise.setDataUrl(entEnterprise.getDataUrl());
			enterprise.setCreateDate(entEnterprise.getCreateDate());

			holdEnterprise(enterprise);
		}*/
		selectResultSet("select e.ent_id, e.ent_name, e.industry, e.property, e.employ_num, e.introduction, e.ent_web, e.parea, e.address, e.data_src, e.data_url, e.create_date, l.lbs_id, l.longitude, l.latitude from zcdh_ent_enterprise e join zcdh_ent_lbs l on l.lbs_id=e.lbs_id", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int index) throws Exception {
				Enterprise enterprise = new Enterprise();
				enterprise.setId(resultSet.getLong("ent_id"));
				enterprise.setName(resultSet.getString("ent_name"));
				enterprise.setCategoryCode(resultSet.getString("industry"));
				if (enterprise.getCategoryCode() != null)
					enterprise.setCategory(enterpriseCategoryCodeMap.get(enterprise.getCategoryCode()));
				enterprise.setNatureCode(resultSet.getString("property"));
				if (enterprise.getNatureCode() != null)
					enterprise.setNature(enterpriseNatureCodeMap.get(enterprise.getNatureCode()));
				enterprise.setScaleCode(resultSet.getString("employ_num"));
				if (enterprise.getScaleCode() != null)
					enterprise.setScale(enterpriseScaleCodeMap.get(enterprise.getScaleCode()));
				enterprise.setIntroduction(resultSet.getString("introduction"));
				enterprise.setWebsite(resultSet.getString("ent_web"));
				enterprise.setAreaCode(resultSet.getString("parea"));
				enterprise.setAddress(resultSet.getString("address"));
				enterprise.setDataSrc(resultSet.getString("data_src"));
				enterprise.setDataUrl(resultSet.getString("data_url"));
				enterprise.setCreateDate(resultSet.getDate("create_date"));
				if (resultSet.getObject("lbs_id") != null) {
					enterprise.setLbsId(resultSet.getLong("lbs_id"));
					enterprise.setLbsLon(resultSet.getDouble("longitude"));
					enterprise.setLbsLat(resultSet.getDouble("latitude"));
				}
				
				holdEnterprise(enterprise);
				return true;
			}
		});

		logger.info("binning post ...");
		/*for (EntPost entPost : entPostDao.findAll()) {
			if (entPost.getEntId() != null) {
				EntEnterprise entEnterprise = entEnterpriseDao.get(entPost.getEntId());
				if (entEnterprise != null) {
					Post post = new Post();
					post.setId(entPost.getId());
					post.setName(entPost.getPostAliases());
					post.setCategory(entPost.getPostName());
					post.setCategoryCode(entPost.getPostCode());
					post.setNumber(entPost.getHeadcounts());
					post.setIsSeveral(entPost.getIsSeveral());
					post.setNumberText(post.getIsSeveral() != null && post.getIsSeveral() == 1 ? "若干" : (post.getNumber() == null ? null : String.valueOf(post.getNumber())));
					post.setNatureCode(entPost.getPjobCategory());
					if (post.getNatureCode() != null)
						post.setNature(postNatureCodeMap.get(post.getNatureCode()));
					post.setSalary(entPost.getPsalary());
					if (post.getSalary() != null)
						post.setSalaryText("0".equals(post.getSalary()) ? "面议" : post.getSalary());
					post.setSalaryType(entPost.getSalaryType());
					EntAbilityRequire experienceEntAbilityRequire = entAbilityRequireDao.getExperience(post.getId());
					if (experienceEntAbilityRequire != null) {
						post.setExperienceCode(experienceEntAbilityRequire.getParamCode());
						if (post.getExperienceCode() != null)
							post.setExperience(postExperienceCodeMap.get(post.getExperienceCode()));
					}
					EntAbilityRequire educationEntAbilityRequire = entAbilityRequireDao.getEducation(post.getId());
					if (educationEntAbilityRequire != null) {
						post.setEducationCode(educationEntAbilityRequire.getParamCode());
						if (post.getEducationCode() != null)
							post.setEducation(postEducationCodeMap.get(post.getEducationCode()));
					}
					post.setWelfareCode(entPost.getTagSelected());
					if (post.getWelfareCode() != null)
						post.setWelfare(post.getWelfareCode().replaceAll("\\d|system|self|&", "").replaceAll("\\$\\$", " "));
					post.setIntroduction(entPost.getPostRemark());
					post.setAreaCode(entPost.getParea());
					post.setAddress(entPost.getPostAddress());
					if (entPost.getLbsId() != null) {
						EntLbs entLbs = entLbsDao.get(entPost.getLbsId());
						if (entLbs != null) {
							post.setLbsId(entLbs.getLbsId());
							post.setLbsLon(entLbs.getLongitude());
							post.setLbsLat(entLbs.getLatitude());
						}
					}
					post.setDataSrc(entPost.getDataSrc());
					post.setDataUrl(entPost.getDataUrl());
					post.setUpdateDate(entPost.getUpdateDate());
					post.setPublishDate(entPost.getPublishDate());
					post.setEnterpriseUrl(entEnterprise.getDataUrl());
					post.setEnterpriseName(entEnterprise.getEntName());

					holdPost(post);
				}
			}
		}*/
		selectResultSet("select distinct p.id, p.post_aliases, p.post_name, p.post_code, p.headcounts, p.is_several, p.pjob_category, p.psalary, p.salary_type, p.tag_selected, p.post_remark, p.parea, p.post_address, p.data_src, p.data_url, p.update_date, p.publish_date, l.lbs_id, l.longitude, l.latitude, ex.ent_ability_id ex_id, ex.param_code ex_code, ed.ent_ability_id ed_id, ed.param_code ed_code, ps.ps_id status_id, v.id view_id, e.ent_name, e.data_url ent_url from zcdh_ent_post p left join zcdh_ent_lbs l on l.lbs_id=p.lbs_id left join zcdh_ent_ability_require ex on ex.post_id=p.id and ex.technology_code='-0000000000003' left join zcdh_ent_ability_require ed on ed.post_id=p.id and ed.technology_code='-0000000000004' left join zcdh_ent_post_status ps on ps.post_id=p.id join zcdh_view_ent_post v on v.post_id=p.id join zcdh_ent_enterprise e on e.ent_id=p.ent_id where p.data_src is not null and p.data_url is not null", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int index) throws Exception {
				Post post = new Post();
				post.setId(resultSet.getLong("id"));
				post.setName(resultSet.getString("post_aliases"));
				post.setCategory(resultSet.getString("post_name"));
				post.setCategoryCode(resultSet.getString("post_code"));
				post.setNumber(resultSet.getInt("headcounts"));
				post.setIsSeveral(resultSet.getInt("is_several"));
				post.setNumberText(post.getIsSeveral() != null && post.getIsSeveral() == 1 ? "若干" : (post.getNumber() == null ? null : String.valueOf(post.getNumber())));
				post.setNatureCode(resultSet.getString("pjob_category"));
				if (post.getNatureCode() != null)
					post.setNature(postNatureCodeMap.get(post.getNatureCode()));
				post.setSalary(resultSet.getString("psalary"));
				if (post.getSalary() != null)
					post.setSalaryText("0".equals(post.getSalary()) ? "面议" : post.getSalary());
				post.setSalaryType(resultSet.getInt("salary_type"));
				post.setExperienceId(resultSet.getLong("ex_id"));
				post.setExperienceCode(resultSet.getString("ex_code"));
				if (post.getExperienceCode() != null)
					post.setExperience(postExperienceCodeMap.get(post.getExperienceCode()));
				post.setEducationId(resultSet.getLong("ed_id"));
				post.setEducationCode(resultSet.getString("ed_code"));
				if (post.getEducationCode() != null)
					post.setEducation(postEducationCodeMap.get(post.getEducationCode()));
				post.setWelfareCode(resultSet.getString("tag_selected"));
				if (post.getWelfareCode() != null)
					post.setWelfare(post.getWelfareCode().replaceAll("\\d|system|self|&", "").replaceAll("\\$\\$", " "));
				post.setIntroduction(resultSet.getString("post_remark"));
				post.setAreaCode(resultSet.getString("parea"));
				post.setAddress(resultSet.getString("post_address"));
				if (resultSet.getObject("lbs_id") != null) {
					post.setLbsId(resultSet.getLong("lbs_id"));
					post.setLbsLon(resultSet.getDouble("longitude"));
					post.setLbsLat(resultSet.getDouble("latitude"));
				}
				post.setDataSrc(resultSet.getString("data_src"));
				post.setDataUrl(resultSet.getString("data_url"));
				post.setUpdateDate(resultSet.getDate("update_date"));
				post.setPublishDate(resultSet.getDate("publish_date"));
				post.setPostStatusId(resultSet.getLong("status_id"));
				post.setPostViewId(resultSet.getLong("view_id"));
				post.setEnterpriseUrl(resultSet.getString("ent_url"));
				post.setEnterpriseName(resultSet.getString("ent_name"));

				holdPost(post);
				return true;
			}
		});
		logger.info("-------------------------");
	}

	public static String getTagCode(String tagName) {
		return tagNameMap.get(tagName);
	}

	public static String getAreaCode(String address) {
		for (String areaName : areaNameMap.keySet())
			if (address.contains(areaName))
				return areaNameMap.get(areaName);
		return null;
	}

	public static Map<String, Map<String, String>> getPostCategoryCodeMap() {
		return postCategoryCodeMap;
	}

	public static Map<String, String> getPostCategoryCode(String category) {
		return postCategoryCodeMap.get(category);
	}

	public static String getPostNatureCode(String paramName) {
		return postNatureCodeMap.get(paramName);
	}

	public static Map<String, String> getPostExperienceCodeMap() {
		return postExperienceCodeMap;
	}

	public static String getPostExperienceCode(String paramName) {
		return postExperienceCodeMap.get(paramName);
	}

	public static Map<String, String> getPostEducationCodeMap() {
		return postEducationCodeMap;
	}

	public static String getPostEducationCode(String paramName) {
		return postEducationCodeMap.get(paramName);
	}

	public static Map<String, String> getEnterpriseCategoryCodeMap() {
		return enterpriseCategoryCodeMap;
	}

	public static String getEnterpriseCategoryCode(String category) {
		return enterpriseCategoryCodeMap.get(category);
	}

	public static Map<String, String> getEnterpriseNatureCodeMap() {
		return enterpriseNatureCodeMap;
	}

	public static String getEnterpriseNatureCode(String paramName) {
		return enterpriseNatureCodeMap.get(paramName);
	}

	public static Map<String, String> getEnterpriseScaleCodeMap() {
		return enterpriseScaleCodeMap;
	}

	public static String getEnterpriseScaleCode(String paramName) {
		return enterpriseScaleCodeMap.get(paramName);
	}

	public static boolean existEnterprise(String enterpriseName) {
		return enterpriseNameMap.containsKey(enterpriseName);
	}

	public static Post getPost(String url) {
		Integer postUrlIndex = postUrlIndexes.get(url);
		if (postUrlIndex != null)
			return postList.get(postUrlIndex);
		return null;
	}

	public static List<Post> find(String name, Integer start, Integer limit) {
		List<Post> list = new ArrayList<Post>();
		if (Ver.bl(name)) {
			for (int i = start, counter = 0; i < postList.size() && counter < limit; i++)
				list.add(counter++, postList.get(i));
		} else {
			Integer localCount = 0;
			for (int i = start, counter = 0; i < postList.size(); i++) {
				Post post = postList.get(i);
				if ((Ver.nb(post.getName()) && post.getName().contains(name)) || (Ver.nb(post.getEnterpriseName()) && post.getEnterpriseName().contains(name))) {
					if (counter < limit)
						list.add(counter++, postList.get(i));
					localCount++;
				}
			}
			localPostNameCount.set(localCount);
		}
		return list;
	}

	public static int getPostSize(String name) {
		Integer localCount = localPostNameCount.get();
		if (localCount != null) {
			return localCount;
		} else if (Ver.bl(name)) {
			return postList.size();
		} else {
			int counter = 0;
			for (int i = 0; i < postList.size(); i++) {
				Post post = postList.get(i);
				if ((Ver.nb(post.getName()) && post.getName().contains(name)) || (Ver.nb(post.getEnterpriseName()) && post.getEnterpriseName().contains(name)))
					counter++;
			}
			return counter;
		}
	}

	public static Enterprise getEnterprise(String url) {
		return enterpriseUrlMap.get(url);
	}

	public static Cluster<Post> getPostCluster() {
		return postCluster;
	}

	public static boolean savePost(List<Post> list) {
		Connection connection = openConnection();
		try {
			connection.setAutoCommit(false);
			saveAllPostLbs(list, connection);
			saveAllPost(list, connection);
			saveAllPostExperience(list, connection);
			saveAllPostEducation(list, connection);
			saveAllPostStatus(list, connection);
			saveAllPostView(list, connection);
			connection.commit();
			holdAllPost(list);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		} finally {
			closeConnection(connection);
		}
	}

	public static boolean savePost(Post post) {
		Connection connection = openConnection();
		try {
			connection.setAutoCommit(false);
			savePostLbs(post, connection);
			savePost(post, connection);
			savePostExperience(post, connection);
			savePostEducation(post, connection);
			savePostStatus(post, connection);
			savePostView(post, connection);
			connection.commit();
			holdPost(post);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		} finally {
			closeConnection(connection);
		}
	}

	public static boolean saveEnterprise(List<Enterprise> list) {
		Connection connection = openConnection();
		try {
			connection.setAutoCommit(false);
			saveAllEnterpriseLbs(list, connection);
			saveAllEnterprise(list, connection);
			connection.commit();
			holdAllEnterprise(list);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		} finally {
			closeConnection(connection);
		}
	}

	public static boolean saveEnterprise(Enterprise enterprise) {
		Connection connection = openConnection();
		try {
			connection.setAutoCommit(false);
			saveEnterpriseLbs(enterprise, connection);
			saveEnterprise(enterprise, connection);
			connection.commit();
			holdEnterprise(enterprise);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		} finally {
			closeConnection(connection);
		}
	}

	public static Enterprise mergeEnterprise(Enterprise ent) {
		if (ent.getName() != null) {
			Enterprise enterprise = enterpriseNameMap.get(ent.getName());
			if (enterprise != null) {

				ent.setId(enterprise.getId());

				if (Ver.nb(enterprise.getCategory()))
					ent.setCategory(enterprise.getCategory());

				if (Ver.nb(enterprise.getCategoryCode()))
					ent.setCategoryCode(enterprise.getCategoryCode());

				if (Ver.nb(enterprise.getNature()))
					ent.setNature(enterprise.getNature());

				if (Ver.nb(enterprise.getNatureCode()))
					ent.setNatureCode(enterprise.getNatureCode());

				if (Ver.nb(enterprise.getScale()))
					ent.setScale(enterprise.getScale());

				if (Ver.nb(enterprise.getScaleCode()))
					ent.setScaleCode(enterprise.getScaleCode());

				if (Ver.nb(enterprise.getIntroduction()))
					ent.setIntroduction(enterprise.getIntroduction());

				if (Ver.nb(enterprise.getWebsite()))
					ent.setWebsite(enterprise.getWebsite());

				if (Ver.nb(enterprise.getAreaCode()))
					ent.setAreaCode(enterprise.getAreaCode());

				if (Ver.nb(enterprise.getAddress()))
					ent.setAddress(enterprise.getAddress());

				if (enterprise.getLbsId() != null)
					ent.setLbsId(enterprise.getLbsId());

				if (enterprise.getCreateDate() != null)
					ent.setCreateDate(enterprise.getCreateDate());
			}
		}
		return ent;
	}

	public static Post mergePost(Post p) {
		if (p.getEnterpriseName() != null) {
			Map<String, Post> postNameMap = postEntNameMap.get(p.getEnterpriseName());
			if (postNameMap != null && p.getName() != null) {
				Post post = postNameMap.get(p.getName());
				if (post != null) {
					p.setId(post.getId());

					if (Ver.nb(post.getCategory()))
						p.setCategory(post.getCategory());

					if (Ver.nb(post.getCategoryCode()))
						p.setCategoryCode(post.getCategoryCode());

					if (post.getNumber() != null)
						p.setNumber(post.getNumber());

					if (Ver.nb(post.getNumberText()))
						p.setNumberText(post.getNumberText());

					if (post.getIsSeveral() != null)
						p.setIsSeveral(post.getIsSeveral());

					if (Ver.nb(post.getNature()))
						p.setNature(post.getNature());

					if (Ver.nb(post.getNatureCode()))
						p.setNatureCode(post.getNatureCode());

					if (Ver.nb(post.getSalary()))
						p.setSalary(post.getSalary());

					if (Ver.nb(post.getSalaryText()))
						p.setSalaryText(post.getSalaryText());

					if (post.getSalaryType() != null)
						p.setSalaryType(post.getSalaryType());

					if (Ver.nb(post.getExperience()))
						p.setExperience(post.getExperience());

					if (Ver.nb(post.getExperienceCode()))
						p.setExperienceCode(post.getExperienceCode());

					if (Ver.nb(post.getEducation()))
						p.setEducation(post.getEducation());

					if (Ver.nb(post.getEducationCode()))
						p.setEducationCode(post.getEducationCode());

					if (Ver.nb(post.getWelfare()))
						p.setWelfare(post.getWelfare());

					if (Ver.nb(post.getWelfareCode()))
						p.setWelfareCode(post.getWelfareCode());

					if (Ver.nb(post.getIntroduction()))
						p.setIntroduction(post.getIntroduction());

					if (Ver.nb(post.getAreaCode()))
						p.setAreaCode(post.getAreaCode());

					if (Ver.nb(post.getAddress()))
						p.setAddress(post.getAddress());

					if (post.getLbsId() != null)
						p.setLbsId(post.getLbsId());

					if (post.getUpdateDate() != null)
						p.setUpdateDate(post.getUpdateDate());

					if (post.getPublishDate() != null)
						p.setPublishDate(post.getPublishDate());
				}
			}
		}
		return p;
	}

	private static boolean holdPost(Post post) {
		if (post.getLbsLon() != null && post.getLbsLat() != null)
			postCluster.save(post);
		if (Ver.nb(post.getDataUrl())) {
			Integer postIndex = postUrlIndexes.get(post.getDataUrl());
			if (postIndex != null) {
				postList.set(postIndex, post);
			} else {
				postList.add(post);
				postUrlIndexes.put(post.getDataUrl(), postList.size() - 1);
			}
		}
		if (Ver.nb(post.getEnterpriseName()) && Ver.nb(post.getName())) {
			Map<String, Post> postNameMap = postEntNameMap.get(post.getEnterpriseName());
			if (postNameMap == null) {
				postNameMap = new HashMap<String, Post>();
				postEntNameMap.put(post.getEnterpriseName(), postNameMap);
			}
			postNameMap.put(post.getName(), post);
		}
		return true;
	}

	private static boolean holdAllPost(List<Post> list) {
		for (Post post : list)
			holdPost(post);
		return true;
	}

	private static boolean holdEnterprise(Enterprise enterprise) {
		if (Ver.nb(enterprise.getDataUrl()))
			enterpriseUrlMap.put(enterprise.getDataUrl(), enterprise);
		if (Ver.nb(enterprise.getName()))
			enterpriseNameMap.put(enterprise.getName(), enterprise);
		return true;
	}

	private static boolean holdAllEnterprise(List<Enterprise> list) {
		for (Enterprise enterprise : list)
			holdEnterprise(enterprise);
		return true;
	}

	private static Post savePostLbs(Post post, Connection connection) throws Exception {
		if (post.getLbsId() != null) {
			PreparedStatement updateStatement = connection.prepareStatement("update zcdh_ent_lbs set longitude=?, latitude=? where lbs_id=?");
			updateStatement.setDouble(1, post.getLbsLon());
			updateStatement.setDouble(2, post.getLbsLat());
			updateStatement.setLong(3, post.getLbsId());
			updateStatement.executeUpdate();
		} else {
			PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_lbs(longitude, latitude) values(?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
			insertStatement.setDouble(1, post.getLbsLon());
			insertStatement.setDouble(2, post.getLbsLat());
			insertStatement.executeUpdate();
			ResultSet generatedSet = insertStatement.getGeneratedKeys();
			if (generatedSet.next())
				post.setLbsId(generatedSet.getLong(1));
		}
		return post;
	}

	private static List<Post> saveAllPostLbs(List<Post> list, Connection connection) throws Exception {
		PreparedStatement updateStatement = connection.prepareStatement("update zcdh_ent_lbs set longitude=?, latitude=? where lbs_id=?");
		PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_lbs(longitude, latitude) values(?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
		List<Post> inserted = new ArrayList<Post>();
		for (Post post : list)
			if (post.getLbsId() != null) {
				updateStatement.setDouble(1, post.getLbsLon());
				updateStatement.setDouble(2, post.getLbsLat());
				updateStatement.setLong(3, post.getLbsId());
				updateStatement.addBatch();
			} else {
				insertStatement.setDouble(1, post.getLbsLon());
				insertStatement.setDouble(2, post.getLbsLat());
				insertStatement.addBatch();
				inserted.add(post);
			}
		updateStatement.executeBatch();
		insertStatement.executeBatch();
		ResultSet generatedSet = insertStatement.getGeneratedKeys();
		for (int i = 0; generatedSet.next(); i++)
			inserted.get(i).setLbsId(generatedSet.getLong(1));
		return list;
	}

	private static Post savePostExperience(Post post, Connection connection) throws Exception {
		Map<String, Object> abilityParam = abilityParamCodeMap.get(post.getExperienceCode());
		Integer paramValue = (Integer) abilityParam.get("value");
		if (post.getExperienceId() != null) {
			PreparedStatement updateStatement = connection.prepareStatement("update zcdh_ent_ability_require set post_code=?, param_code=?, grade=?, weight_point=total_point/grade where ent_ability_id=?");
			updateStatement.setString(1, post.getCategoryCode());
			updateStatement.setString(2, post.getExperienceCode());
			updateStatement.setInt(3, paramValue);
			updateStatement.setLong(4, post.getExperienceId());
			updateStatement.executeUpdate();
		} else {
			PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_ability_require(post_id, ent_id, post_code, param_code, grade, match_type, technology_code, technology_cate_code, total_point, weight_point) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
			Enterprise enterprise = enterpriseNameMap.get(post.getEnterpriseName());
			Integer paramPercent = (Integer) abilityParam.get("percent");
			Double totalPoint = paramPercent / 2.0;
			insertStatement.setLong(1, post.getId());
			insertStatement.setLong(2, enterprise.getId());
			insertStatement.setString(3, post.getCategoryCode());
			insertStatement.setString(4, post.getExperienceCode());
			insertStatement.setInt(5, paramValue);
			insertStatement.setInt(6, (Integer) abilityParam.get("matchType"));
			insertStatement.setString(7, (String) abilityParam.get("technologyCode"));
			insertStatement.setString(8, (String) abilityParam.get("technologyCategoryCode"));
			insertStatement.setDouble(9, totalPoint);
			insertStatement.setDouble(10, totalPoint / paramValue);
			insertStatement.executeUpdate();
			ResultSet generatedSet = insertStatement.getGeneratedKeys();
			if (generatedSet.next())
				post.setExperienceId(generatedSet.getLong(1));
		}
		return post;
	}

	private static List<Post> saveAllPostExperience(List<Post> list, Connection connection) throws Exception {
		PreparedStatement updateStatement = connection.prepareStatement("update zcdh_ent_ability_require set post_code=?, param_code=?, grade=?, weight_point=total_point/grade where ent_ability_id=?");
		PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_ability_require(post_id, ent_id, post_code, param_code, grade, match_type, technology_code, technology_cate_code, total_point, weight_point) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
		List<Post> inserted = new ArrayList<Post>();
		for (Post post : list) {
			Map<String, Object> abilityParam = abilityParamCodeMap.get(post.getExperienceCode());
			Integer paramValue = (Integer) abilityParam.get("value");
			if (post.getExperienceId() != null) {
				updateStatement.setString(1, post.getCategoryCode());
				updateStatement.setString(2, post.getExperienceCode());
				updateStatement.setInt(3, paramValue);
				updateStatement.setLong(4, post.getExperienceId());
				updateStatement.addBatch();
			} else {
				Enterprise enterprise = enterpriseNameMap.get(post.getEnterpriseName());
				Integer paramPercent = (Integer) abilityParam.get("percent");
				Double totalPoint = paramPercent / 2.0;
				insertStatement.setLong(1, post.getId());
				insertStatement.setLong(2, enterprise.getId());
				insertStatement.setString(3, post.getCategoryCode());
				insertStatement.setString(4, post.getExperienceCode());
				insertStatement.setInt(5, paramValue);
				insertStatement.setInt(6, (Integer) abilityParam.get("matchType"));
				insertStatement.setString(7, (String) abilityParam.get("technologyCode"));
				insertStatement.setString(8, (String) abilityParam.get("technologyCategoryCode"));
				insertStatement.setDouble(9, totalPoint);
				insertStatement.setDouble(10, totalPoint / paramValue);
				insertStatement.addBatch();
				inserted.add(post);
			}
		}
		updateStatement.executeBatch();
		insertStatement.executeBatch();
		ResultSet generatedSet = insertStatement.getGeneratedKeys();
		for (int i = 0; generatedSet.next(); i++)
			inserted.get(i).setExperienceId(generatedSet.getLong(1));
		return list;
	}

	private static Post savePostEducation(Post post, Connection connection) throws Exception {
		Map<String, Object> abilityParam = abilityParamCodeMap.get(post.getEducationCode());
		Integer paramValue = (Integer) abilityParam.get("value");
		if (post.getEducationId() != null) {
			PreparedStatement updateStatement = connection.prepareStatement("update zcdh_ent_ability_require set post_code=?, param_code=?, grade=?, weight_point=total_point/grade where ent_ability_id=?");
			updateStatement.setString(1, post.getCategoryCode());
			updateStatement.setString(2, post.getEducationCode());
			updateStatement.setInt(3, paramValue);
			updateStatement.setLong(4, post.getEducationId());
			updateStatement.executeUpdate();
		} else {
			PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_ability_require(post_id, ent_id, post_code, param_code, grade, match_type, technology_code, technology_cate_code, total_point, weight_point) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
			Enterprise enterprise = enterpriseNameMap.get(post.getEnterpriseName());
			Integer paramPercent = (Integer) abilityParam.get("percent");
			Double totalPoint = paramPercent / 2.0;
			insertStatement.setLong(1, post.getId());
			insertStatement.setLong(2, enterprise.getId());
			insertStatement.setString(3, post.getCategoryCode());
			insertStatement.setString(4, post.getEducationCode());
			insertStatement.setInt(5, paramValue);
			insertStatement.setInt(6, (Integer) abilityParam.get("matchType"));
			insertStatement.setString(7, (String) abilityParam.get("technologyCode"));
			insertStatement.setString(8, (String) abilityParam.get("technologyCategoryCode"));
			insertStatement.setDouble(9, totalPoint);
			insertStatement.setDouble(10, totalPoint / paramValue);
			insertStatement.executeUpdate();
			ResultSet generatedSet = insertStatement.getGeneratedKeys();
			if (generatedSet.next())
				post.setEducationId(generatedSet.getLong(1));
		}
		return post;
	}

	private static List<Post> saveAllPostEducation(List<Post> list, Connection connection) throws Exception {
		PreparedStatement updateStatement = connection.prepareStatement("update zcdh_ent_ability_require set post_code=?, param_code=?, grade=?, weight_point=total_point/grade where ent_ability_id=?");
		PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_ability_require(post_id, ent_id, post_code, param_code, grade, match_type, technology_code, technology_cate_code, total_point, weight_point) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
		List<Post> inserted = new ArrayList<Post>();
		for (Post post : list) {
			Map<String, Object> abilityParam = abilityParamCodeMap.get(post.getEducationCode());
			Integer paramValue = (Integer) abilityParam.get("value");
			if (post.getEducationId() != null) {
				updateStatement.setString(1, post.getCategoryCode());
				updateStatement.setString(2, post.getEducationCode());
				updateStatement.setInt(3, paramValue);
				updateStatement.setLong(4, post.getEducationId());
				updateStatement.addBatch();
			} else {
				Enterprise enterprise = enterpriseNameMap.get(post.getEnterpriseName());
				Integer paramPercent = (Integer) abilityParam.get("percent");
				Double totalPoint = paramPercent / 2.0;
				insertStatement.setLong(1, post.getId());
				insertStatement.setLong(2, enterprise.getId());
				insertStatement.setString(3, post.getCategoryCode());
				insertStatement.setString(4, post.getEducationCode());
				insertStatement.setInt(5, paramValue);
				insertStatement.setInt(6, (Integer) abilityParam.get("matchType"));
				insertStatement.setString(7, (String) abilityParam.get("technologyCode"));
				insertStatement.setString(8, (String) abilityParam.get("technologyCategoryCode"));
				insertStatement.setDouble(9, totalPoint);
				insertStatement.setDouble(10, totalPoint / paramValue);
				insertStatement.addBatch();
				inserted.add(post);
			}
		}
		updateStatement.executeBatch();
		insertStatement.executeBatch();
		ResultSet generatedSet = insertStatement.getGeneratedKeys();
		for (int i = 0; generatedSet.next(); i++)
			inserted.get(i).setEducationId(generatedSet.getLong(1));
		return list;
	}

	private static Post savePost(Post post, Connection connection) throws Exception {
		if (post.getId() != null) {
			PreparedStatement updateStatement = connection.prepareStatement("update zcdh_ent_post set post_aliases=?, post_name=?, post_code=?, headcounts=?, is_several=?, pjob_category=?, psalary=?, salary_type=?, tag_selected=?, post_remark=?, parea=?, post_address=?, lbs_id=?, data_src=?, data_url=?, update_date=? where id=?");
			updateStatement.setString(1, post.getName());
			updateStatement.setString(2, post.getCategory());
			updateStatement.setString(3, post.getCategoryCode());
			updateStatement.setInt(4, post.getNumber());
			updateStatement.setInt(5, post.getIsSeveral());
			updateStatement.setString(6, post.getNatureCode());
			updateStatement.setString(7, post.getSalary());
			updateStatement.setInt(8, post.getSalaryType());
			updateStatement.setString(9, post.getWelfareCode());
			updateStatement.setString(10, post.getIntroduction());
			updateStatement.setString(11, post.getAreaCode());
			updateStatement.setString(12, post.getAddress());
			updateStatement.setLong(13, post.getLbsId());
			updateStatement.setString(14, post.getDataSrc());
			updateStatement.setString(15, post.getDataUrl());
			updateStatement.setDate(16, new Date(post.getUpdateDate().getTime()));
			updateStatement.setLong(17, post.getId());
			updateStatement.executeUpdate();
			post.setStatus(3);
		} else {
			PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_post(post_aliases, post_name, post_code, headcounts, is_several, pjob_category, psalary, salary_type, tag_selected, post_remark, parea, post_address, lbs_id, data_src, data_url, update_date, publish_date, ent_id) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
			Enterprise enterprise = enterpriseNameMap.get(post.getEnterpriseName());
			insertStatement.setString(1, post.getName());
			insertStatement.setString(2, post.getCategory());
			insertStatement.setString(3, post.getCategoryCode());
			insertStatement.setInt(4, post.getNumber());
			insertStatement.setInt(5, post.getIsSeveral());
			insertStatement.setString(6, post.getNatureCode());
			insertStatement.setString(7, post.getSalary());
			insertStatement.setInt(8, post.getSalaryType());
			insertStatement.setString(9, post.getWelfareCode());
			insertStatement.setString(10, post.getIntroduction());
			insertStatement.setString(11, post.getAreaCode());
			insertStatement.setString(12, post.getAddress());
			insertStatement.setLong(13, post.getLbsId());
			insertStatement.setString(14, post.getDataSrc());
			insertStatement.setString(15, post.getDataUrl());
			insertStatement.setDate(16, new Date(post.getUpdateDate().getTime()));
			insertStatement.setDate(17, new Date(post.getPublishDate().getTime()));
			insertStatement.setLong(18, enterprise.getId());
			insertStatement.executeUpdate();
			post.setStatus(2);
			ResultSet generatedSet = insertStatement.getGeneratedKeys();
			if (generatedSet.next())
				post.setId(generatedSet.getLong(1));
			savePostPromotion(post, connection);
		}
		return post;
	}

	private static List<Post> saveAllPost(List<Post> list, Connection connection) throws Exception {
		PreparedStatement updateStatement = connection.prepareStatement("update zcdh_ent_post set post_aliases=?, post_name=?, post_code=?, headcounts=?, is_several=?, pjob_category=?, psalary=?, salary_type=?, tag_selected=?, post_remark=?, parea=?, post_address=?, lbs_id=?, data_src=?, data_url=?, update_date=? where id=?");
		PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_post(post_aliases, post_name, post_code, headcounts, is_several, pjob_category, psalary, salary_type, tag_selected, post_remark, parea, post_address, lbs_id, data_src, data_url, update_date, publish_date, ent_id) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
		List<Post> inserted = new ArrayList<Post>();
		for (Post post : list)
			if (post.getId() != null) {
				updateStatement.setString(1, post.getName());
				updateStatement.setString(2, post.getCategory());
				updateStatement.setString(3, post.getCategoryCode());
				updateStatement.setInt(4, post.getNumber());
				updateStatement.setInt(5, post.getIsSeveral());
				updateStatement.setString(6, post.getNatureCode());
				updateStatement.setString(7, post.getSalary());
				updateStatement.setInt(8, post.getSalaryType());
				updateStatement.setString(9, post.getWelfareCode());
				updateStatement.setString(10, post.getIntroduction());
				updateStatement.setString(11, post.getAreaCode());
				updateStatement.setString(12, post.getAddress());
				updateStatement.setLong(13, post.getLbsId());
				updateStatement.setString(14, post.getDataSrc());
				updateStatement.setString(15, post.getDataUrl());
				updateStatement.setDate(16, new Date(post.getUpdateDate().getTime()));
				updateStatement.setLong(17, post.getId());
				updateStatement.addBatch();
				post.setStatus(3);
			} else {
				Enterprise enterprise = enterpriseNameMap.get(post.getEnterpriseName());
				insertStatement.setString(1, post.getName());
				insertStatement.setString(2, post.getCategory());
				insertStatement.setString(3, post.getCategoryCode());
				insertStatement.setInt(4, post.getNumber());
				insertStatement.setInt(5, post.getIsSeveral());
				insertStatement.setString(6, post.getNatureCode());
				insertStatement.setString(7, post.getSalary());
				insertStatement.setInt(8, post.getSalaryType());
				insertStatement.setString(9, post.getWelfareCode());
				insertStatement.setString(10, post.getIntroduction());
				insertStatement.setString(11, post.getAreaCode());
				insertStatement.setString(12, post.getAddress());
				insertStatement.setLong(13, post.getLbsId());
				insertStatement.setString(14, post.getDataSrc());
				insertStatement.setString(15, post.getDataUrl());
				insertStatement.setDate(16, new Date(post.getUpdateDate().getTime()));
				insertStatement.setDate(17, new Date(post.getPublishDate().getTime()));
				insertStatement.setLong(18, enterprise.getId());
				insertStatement.addBatch();
				post.setStatus(2);
				inserted.add(post);
			}
		updateStatement.executeBatch();
		insertStatement.executeBatch();
		ResultSet generatedSet = insertStatement.getGeneratedKeys();
		for (int i = 0; generatedSet.next(); i++)
			inserted.get(i).setId(generatedSet.getLong(1));
		saveAllPostPromotion(inserted, connection);
		return list;
	}

	private static Post savePostStatus(Post post, Connection connection) throws Exception {
		if (post.getPostStatusId() != null) {
			PreparedStatement updateStatement = connection.prepareStatement("update zcdh_ent_post_status set employ_total=?, un_employ=if(employ_total>employed,employ_total-employed,0) where ps_id=?");
			updateStatement.setInt(1, post.getNumber());
			updateStatement.setLong(2, post.getPostStatusId());
			updateStatement.executeUpdate();
		} else {
			PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_post_status(post_id, post_status, employ, employed, employ_total, un_employ, skim_count) values(?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
			insertStatement.setLong(1, post.getId());
			insertStatement.setInt(2, 1);
			insertStatement.setInt(3, 0);
			insertStatement.setInt(4, 0);
			insertStatement.setInt(5, post.getNumber());
			insertStatement.setInt(6, post.getNumber());
			insertStatement.setInt(7, 0);
			insertStatement.executeUpdate();
			ResultSet generatedSet = insertStatement.getGeneratedKeys();
			if (generatedSet.next())
				post.setPostStatusId(generatedSet.getLong(1));
		}
		return post;
	}

	private static List<Post> saveAllPostStatus(List<Post> list, Connection connection) throws Exception {
		PreparedStatement updateStatement = connection.prepareStatement("update zcdh_ent_post_status set employ_total=?, un_employ=if(employ_total>employed,employ_total-employed,0) where ps_id=?");
		PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_post_status(post_id, post_status, employ, employed, employ_total, un_employ, skim_count) values(?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
		List<Post> inserted = new ArrayList<Post>();
		for (Post post : list)
			if (post.getPostStatusId() != null) {
				updateStatement.setInt(1, post.getNumber());
				updateStatement.setLong(2, post.getPostStatusId());
				updateStatement.addBatch();
			} else {
				insertStatement.setLong(1, post.getId());
				insertStatement.setInt(2, 1);
				insertStatement.setInt(3, 0);
				insertStatement.setInt(4, 0);
				insertStatement.setInt(5, post.getNumber());
				insertStatement.setInt(6, post.getNumber());
				insertStatement.setInt(7, 0);
				insertStatement.addBatch();
				inserted.add(post);
			}
		updateStatement.executeBatch();
		insertStatement.executeBatch();
		ResultSet generatedSet = insertStatement.getGeneratedKeys();
		for (int i = 0; generatedSet.next(); i++)
			inserted.get(i).setPostStatusId(generatedSet.getLong(1));
		return list;
	}

	private static Post savePostPromotion(Post post, Connection connection) throws Exception {
		if (post.getPostPromotionId() == null) {
			PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_promotion(ent_post_id, ent_id, promotion_value) values(?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
			Enterprise enterprise = enterpriseNameMap.get(post.getEnterpriseName());
			insertStatement.setLong(1, post.getId());
			insertStatement.setLong(2, enterprise.getId());
			insertStatement.setString(3, "");
			insertStatement.executeUpdate();
			ResultSet generatedSet = insertStatement.getGeneratedKeys();
			if (generatedSet.next())
				post.setPostPromotionId(generatedSet.getLong(1));
		}
		return post;
	}

	private static List<Post> saveAllPostPromotion(List<Post> list, Connection connection) throws Exception {
		PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_promotion(ent_post_id, ent_id, promotion_value) values(?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
		List<Post> inserted = new ArrayList<Post>();
		for (Post post : list)
			if (post.getPostPromotionId() == null) {
				Enterprise enterprise = enterpriseNameMap.get(post.getEnterpriseName());
				insertStatement.setLong(1, post.getId());
				insertStatement.setLong(2, enterprise.getId());
				insertStatement.setString(3, "");
				insertStatement.addBatch();
				inserted.add(post);
			}
		insertStatement.executeBatch();
		ResultSet generatedSet = insertStatement.getGeneratedKeys();
		for (int i = 0; generatedSet.next(); i++)
			inserted.get(i).setPostPromotionId(generatedSet.getLong(1));
		return list;
	}

	private static Post savePostView(Post post, Connection connection) throws Exception {
		Enterprise enterprise = enterpriseNameMap.get(post.getEnterpriseName());
		if (post.getPostViewId() != null) {
			PreparedStatement updateStatement = connection.prepareStatement("update zcdh_view_ent_post set post_aliases=?, post_code=?, post_property_code=?, salary_code=?, min_salary=?, max_salary=?, salary_type=?, ent_name=?, industry=?, property=?, employ_num=? where id=?");
			updateStatement.setString(1, post.getName());
			updateStatement.setString(2, post.getCategoryCode());
			updateStatement.setString(3, post.getNatureCode());
			updateStatement.setString(4, post.getSalary());
			Integer minSalary = null;
			Integer maxSalary = null;
			if (post.getSalary() != null && post.getSalary().contains("-")) {
				String[] salaries = post.getSalary().split("-", 2);
				if (salaries[0].matches("^\\d+$"))
					minSalary = Integer.parseInt(salaries[0]);
				if (salaries[1].matches("^\\d+$"))
					maxSalary = Integer.parseInt(salaries[1]);
			}
			if (minSalary != null)
				updateStatement.setInt(5, minSalary);
			else
				updateStatement.setNull(5, Types.INTEGER);
			if (maxSalary != null)
				updateStatement.setInt(6, maxSalary);
			else
				updateStatement.setNull(6, Types.INTEGER);
			updateStatement.setInt(7, post.getSalaryType());
			updateStatement.setString(8, enterprise.getName());
			updateStatement.setString(9, enterprise.getCategoryCode());
			updateStatement.setString(10, enterprise.getNatureCode());
			updateStatement.setString(11, enterprise.getScaleCode());
			updateStatement.setLong(12, post.getPostViewId());
			updateStatement.executeUpdate();
		} else {
			PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_view_ent_post(post_id, post_aliases, post_code, post_property_code, salary_code, min_salary, max_salary, salary_type, ent_name, industry, property, employ_num, publish_date) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
			insertStatement.setLong(1, post.getId());
			insertStatement.setString(2, post.getName());
			insertStatement.setString(3, post.getCategoryCode());
			insertStatement.setString(4, post.getNatureCode());
			insertStatement.setString(5, post.getSalary());
			Integer minSalary = null;
			Integer maxSalary = null;
			if (post.getSalary() != null && post.getSalary().contains("-")) {
				String[] salaries = post.getSalary().split("-", 2);
				if (salaries[0].matches("^\\d+$"))
					minSalary = Integer.parseInt(salaries[0]);
				if (salaries[1].matches("^\\d+$"))
					maxSalary = Integer.parseInt(salaries[1]);
			}
			if (minSalary != null)
				insertStatement.setInt(6, minSalary);
			else
				insertStatement.setNull(6, Types.INTEGER);
			if (maxSalary != null)
				insertStatement.setInt(7, maxSalary);
			else
				insertStatement.setNull(7, Types.INTEGER);
			insertStatement.setInt(8, post.getSalaryType());
			insertStatement.setString(9, enterprise.getName());
			insertStatement.setString(10, enterprise.getCategoryCode());
			insertStatement.setString(11, enterprise.getNatureCode());
			insertStatement.setString(12, enterprise.getScaleCode());
			insertStatement.setDate(13, new Date(post.getPublishDate().getTime()));
			insertStatement.executeUpdate();
			ResultSet generatedSet = insertStatement.getGeneratedKeys();
			if (generatedSet.next())
				post.setPostViewId(generatedSet.getLong(1));
		}
		return post;
	}

	private static List<Post> saveAllPostView(List<Post> list, Connection connection) throws Exception {
		PreparedStatement updateStatement = connection.prepareStatement("update zcdh_view_ent_post set post_aliases=?, post_code=?, post_property_code=?, salary_code=?, min_salary=?, max_salary=?, salary_type=?, ent_name=?, industry=?, property=?, employ_num=? where id=?");
		PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_view_ent_post(post_id, post_aliases, post_code, post_property_code, salary_code, min_salary, max_salary, salary_type, ent_name, industry, property, employ_num, publish_date) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
		List<Post> inserted = new ArrayList<Post>();
		for (Post post : list) {
			Enterprise enterprise = enterpriseNameMap.get(post.getEnterpriseName());
			if (post.getPostViewId() != null) {
				updateStatement.setString(1, post.getName());
				updateStatement.setString(2, post.getCategoryCode());
				updateStatement.setString(3, post.getNatureCode());
				updateStatement.setString(4, post.getSalary());
				Integer minSalary = null;
				Integer maxSalary = null;
				if (post.getSalary() != null && post.getSalary().contains("-")) {
					String[] salaries = post.getSalary().split("-", 2);
					if (salaries[0].matches("^\\d+$"))
						minSalary = Integer.parseInt(salaries[0]);
					if (salaries[1].matches("^\\d+$"))
						maxSalary = Integer.parseInt(salaries[1]);
				}
				if (minSalary != null)
					updateStatement.setInt(5, minSalary);
				else
					updateStatement.setNull(5, Types.INTEGER);
				if (maxSalary != null)
					updateStatement.setInt(6, maxSalary);
				else
					updateStatement.setNull(6, Types.INTEGER);
				updateStatement.setInt(7, post.getSalaryType());
				updateStatement.setString(8, enterprise.getName());
				updateStatement.setString(9, enterprise.getCategoryCode());
				updateStatement.setString(10, enterprise.getNatureCode());
				updateStatement.setString(11, enterprise.getScaleCode());
				updateStatement.setLong(12, post.getPostViewId());
				updateStatement.addBatch();
			} else {
				insertStatement.setLong(1, post.getId());
				insertStatement.setString(2, post.getName());
				insertStatement.setString(3, post.getCategoryCode());
				insertStatement.setString(4, post.getNatureCode());
				insertStatement.setString(5, post.getSalary());
				Integer minSalary = null;
				Integer maxSalary = null;
				if (post.getSalary() != null && post.getSalary().contains("-")) {
					String[] salaries = post.getSalary().split("-", 2);
					if (salaries[0].matches("^\\d+$"))
						minSalary = Integer.parseInt(salaries[0]);
					if (salaries[1].matches("^\\d+$"))
						maxSalary = Integer.parseInt(salaries[1]);
				}
				if (minSalary != null)
					insertStatement.setInt(6, minSalary);
				else
					insertStatement.setNull(6, Types.INTEGER);
				if (maxSalary != null)
					insertStatement.setInt(7, maxSalary);
				else
					insertStatement.setNull(7, Types.INTEGER);
				insertStatement.setInt(8, post.getSalaryType());
				insertStatement.setString(9, enterprise.getName());
				insertStatement.setString(10, enterprise.getCategoryCode());
				insertStatement.setString(11, enterprise.getNatureCode());
				insertStatement.setString(12, enterprise.getScaleCode());
				insertStatement.setDate(13, new Date(post.getPublishDate().getTime()));
				insertStatement.addBatch();
				inserted.add(post);
			}
		}
		updateStatement.executeBatch();
		insertStatement.executeBatch();
		ResultSet generatedSet = insertStatement.getGeneratedKeys();
		for (int i = 0; generatedSet.next(); i++)
			inserted.get(i).setPostViewId(generatedSet.getLong(1));
		return list;
	}

	private static Enterprise saveEnterpriseLbs(Enterprise enterprise, Connection connection) throws Exception {
		if (enterprise.getLbsId() != null) {
			PreparedStatement updateStatement = connection.prepareStatement("update zcdh_ent_lbs set longitude=?, latitude=? where lbs_id=?");
			updateStatement.setDouble(1, enterprise.getLbsLon());
			updateStatement.setDouble(2, enterprise.getLbsLat());
			updateStatement.setLong(3, enterprise.getLbsId());
			updateStatement.executeUpdate();
		} else {
			PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_lbs(longitude, latitude) values(?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
			insertStatement.setDouble(1, enterprise.getLbsLon());
			insertStatement.setDouble(2, enterprise.getLbsLat());
			insertStatement.executeUpdate();
			ResultSet generatedSet = insertStatement.getGeneratedKeys();
			if (generatedSet.next())
				enterprise.setLbsId(generatedSet.getLong(1));
		}
		return enterprise;
	}

	private static List<Enterprise> saveAllEnterpriseLbs(List<Enterprise> list, Connection connection) throws Exception {
		PreparedStatement updateStatement = connection.prepareStatement("update zcdh_ent_lbs set longitude=?, latitude=? where lbs_id=?");
		PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_lbs(longitude, latitude) values(?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
		List<Enterprise> inserted = new ArrayList<Enterprise>();
		for (Enterprise enterprise : list)
			if (enterprise.getLbsId() != null) {
				updateStatement.setDouble(1, enterprise.getLbsLon());
				updateStatement.setDouble(2, enterprise.getLbsLat());
				updateStatement.setLong(3, enterprise.getLbsId());
				updateStatement.addBatch();
			} else {
				insertStatement.setDouble(1, enterprise.getLbsLon());
				insertStatement.setDouble(2, enterprise.getLbsLat());
				insertStatement.addBatch();
				inserted.add(enterprise);
			}
		updateStatement.executeBatch();
		insertStatement.executeBatch();
		ResultSet generatedSet = insertStatement.getGeneratedKeys();
		for (int i = 0; generatedSet.next(); i++)
			inserted.get(i).setLbsId(generatedSet.getLong(1));
		return list;
	}

	private static Enterprise saveEnterprise(Enterprise enterprise, Connection connection) throws Exception {
		if (enterprise.getId() != null) {
			PreparedStatement updateStatement = connection.prepareStatement("update zcdh_ent_enterprise set ent_name=?, industry=?, property=?, employ_num=?, introduction=?, ent_web=?, parea=?, address=?, lbs_id=?, data_src=?, data_url=? where ent_id=?");
			updateStatement.setString(1, enterprise.getName());
			updateStatement.setString(2, enterprise.getCategoryCode());
			updateStatement.setString(3, enterprise.getNatureCode());
			updateStatement.setString(4, enterprise.getScaleCode());
			updateStatement.setString(5, enterprise.getIntroduction());
			updateStatement.setString(6, enterprise.getWebsite());
			updateStatement.setString(7, enterprise.getAreaCode());
			updateStatement.setString(8, enterprise.getAddress());
			updateStatement.setLong(9, enterprise.getLbsId());
			updateStatement.setString(10, enterprise.getDataSrc());
			updateStatement.setString(11, enterprise.getDataUrl());
			updateStatement.setLong(12, enterprise.getId());
			updateStatement.executeUpdate();
			enterprise.setStatus(3);
		} else {
			PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_enterprise(ent_name, industry, property, employ_num, introduction, ent_web, parea, address, lbs_id, data_src, data_url, create_date) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
			insertStatement.setString(1, enterprise.getName());
			insertStatement.setString(2, enterprise.getCategoryCode());
			insertStatement.setString(3, enterprise.getNatureCode());
			insertStatement.setString(4, enterprise.getScaleCode());
			insertStatement.setString(5, enterprise.getIntroduction());
			insertStatement.setString(6, enterprise.getWebsite());
			insertStatement.setString(7, enterprise.getAreaCode());
			insertStatement.setString(8, enterprise.getAddress());
			insertStatement.setLong(9, enterprise.getLbsId());
			insertStatement.setString(10, enterprise.getDataSrc());
			insertStatement.setString(11, enterprise.getDataUrl());
			insertStatement.setDate(12, new Date(enterprise.getCreateDate().getTime()));
			insertStatement.executeUpdate();
			enterprise.setStatus(2);
			ResultSet generatedSet = insertStatement.getGeneratedKeys();
			if (generatedSet.next())
				enterprise.setId(generatedSet.getLong(1));
			saveEnterpriseAccount(enterprise, connection);
		}
		return enterprise;
	}

	private static List<Enterprise> saveAllEnterprise(List<Enterprise> list, Connection connection) throws Exception {
		PreparedStatement updateStatement = connection.prepareStatement("update zcdh_ent_enterprise set ent_name=?, industry=?, property=?, employ_num=?, introduction=?, ent_web=?, parea=?, address=?, lbs_id=?, data_src=?, data_url=? where ent_id=?");
		PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_enterprise(ent_name, industry, property, employ_num, introduction, ent_web, parea, address, lbs_id, data_src, data_url, create_date) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
		List<Enterprise> inserted = new ArrayList<Enterprise>();
		for (Enterprise enterprise : list)
			if (enterprise.getId() != null) {
				updateStatement.setString(1, enterprise.getName());
				updateStatement.setString(2, enterprise.getCategoryCode());
				updateStatement.setString(3, enterprise.getNatureCode());
				updateStatement.setString(4, enterprise.getScaleCode());
				updateStatement.setString(5, enterprise.getIntroduction());
				updateStatement.setString(6, enterprise.getWebsite());
				updateStatement.setString(7, enterprise.getAreaCode());
				updateStatement.setString(8, enterprise.getAddress());
				updateStatement.setLong(9, enterprise.getLbsId());
				updateStatement.setString(10, enterprise.getDataSrc());
				updateStatement.setString(11, enterprise.getDataUrl());
				updateStatement.setLong(12, enterprise.getId());
				updateStatement.addBatch();
				enterprise.setStatus(3);
			} else {
				insertStatement.setString(1, enterprise.getName());
				insertStatement.setString(2, enterprise.getCategoryCode());
				insertStatement.setString(3, enterprise.getNatureCode());
				insertStatement.setString(4, enterprise.getScaleCode());
				insertStatement.setString(5, enterprise.getIntroduction());
				insertStatement.setString(6, enterprise.getWebsite());
				insertStatement.setString(7, enterprise.getAreaCode());
				insertStatement.setString(8, enterprise.getAddress());
				insertStatement.setLong(9, enterprise.getLbsId());
				insertStatement.setString(10, enterprise.getDataSrc());
				insertStatement.setString(11, enterprise.getDataUrl());
				insertStatement.setDate(12, new Date(enterprise.getCreateDate().getTime()));
				insertStatement.addBatch();
				enterprise.setStatus(2);
				inserted.add(enterprise);
			}
		updateStatement.executeBatch();
		insertStatement.executeBatch();
		ResultSet generatedSet = insertStatement.getGeneratedKeys();
		for (int i = 0; generatedSet.next(); i++)
			inserted.get(i).setId(generatedSet.getLong(1));
		saveAllEnterpriseAccount(inserted, connection);
		return list;
	}

	private static Enterprise saveEnterpriseAccount(Enterprise enterprise, Connection connection) throws Exception {
		if (enterprise.getEnterpriseAccountId() == null) {
			PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_account(ent_id, account, pwd, create_mode, create_date, status) values(?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
			insertStatement.setLong(1, enterprise.getId());
			insertStatement.setString(2, String.format("zcdh%s", accountNumFormat.format(++lastAccountNum)));
			insertStatement.setString(3, "oSBrriGEzW8KHAL6b9J63w==");
			insertStatement.setInt(4, 2);
			insertStatement.setDate(5, new Date(enterprise.getCreateDate().getTime()));
			insertStatement.setDouble(6, 1);
			insertStatement.executeUpdate();
			ResultSet generatedSet = insertStatement.getGeneratedKeys();
			if (generatedSet.next())
				enterprise.setEnterpriseAccountId(generatedSet.getLong(1));
		}
		return enterprise;
	}

	private static List<Enterprise> saveAllEnterpriseAccount(List<Enterprise> list, Connection connection) throws Exception {
		PreparedStatement insertStatement = connection.prepareStatement("insert into zcdh_ent_account(ent_id, account, pwd, create_mode, create_date, status) values(?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
		List<Enterprise> inserted = new ArrayList<Enterprise>();
		for (Enterprise enterprise : list)
			if (enterprise.getEnterpriseAccountId() == null) {
				insertStatement.setLong(1, enterprise.getId());
				insertStatement.setString(2, String.format("zcdh%s", accountNumFormat.format(++lastAccountNum)));
				insertStatement.setString(3, "oSBrriGEzW8KHAL6b9J63w==");
				insertStatement.setInt(4, 2);
				insertStatement.setDate(5, new Date(enterprise.getCreateDate().getTime()));
				insertStatement.setDouble(6, 1);
				insertStatement.addBatch();
				inserted.add(enterprise);
			}
		insertStatement.executeBatch();
		ResultSet generatedSet = insertStatement.getGeneratedKeys();
		for (int i = 0; generatedSet.next(); i++)
			inserted.get(i).setEnterpriseAccountId(generatedSet.getLong(1));
		return list;
	}
	
}
