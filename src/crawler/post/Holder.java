package crawler.post;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;

import util.Ver;
import crawler.post.model.Ability;
import crawler.post.model.AbilityParam;
import crawler.post.model.Account;
import crawler.post.model.Enterprise;
import crawler.post.model.Lbs;
import crawler.post.model.Post;
import dao.data.C3P0Store;

public class Holder extends C3P0Store {

	private static Logger logger = Logger.getLogger(Holder.class);

	private static Map<String, String> tags = new ConcurrentHashMap<String, String>();
	private static Map<String, String> tagNameMap = new ConcurrentHashMap<String, String>();
	private static Map<String, String> areaNameMap = new ConcurrentHashMap<String, String>();

	private static Map<String, Map<String, String>> postCategories = new ConcurrentHashMap<String, Map<String, String>>();
	private static Map<String, String> postNatures = new ConcurrentHashMap<String, String>();
	private static Map<String, AbilityParam> postExperiences = new ConcurrentHashMap<String, AbilityParam>();
	private static Map<String, AbilityParam> postEducations = new ConcurrentHashMap<String, AbilityParam>();

	private static Map<String, String> enterpriseCategories = new ConcurrentHashMap<String, String>();
	private static Map<String, String> enterpriseNatures = new ConcurrentHashMap<String, String>();
	private static Map<String, String> enterpriseScales = new ConcurrentHashMap<String, String>();

	private static Map<String, Post> posts = new ConcurrentHashMap<String, Post>();
	private static Map<String, Enterprise> enterprises = new ConcurrentHashMap<String, Enterprise>();
	private static Map<String, Enterprise> enterpriseNameMap = new ConcurrentHashMap<String, Enterprise>();

	private static MessageDigest messageDigest;

	static {
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init() {
		logger.info("------ init Holder(Post) ------");
		selectResultSet("select tag_name, tag_code from zcdh_tag where is_delete=1 or is_delete is null", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int i) throws Exception {
				String code = resultSet.getString("tag_code");
				String name = resultSet.getString("tag_name");
				tags.put(code, name);
				tagNameMap.put(name, code);
				return true;
			}
		});

		selectResultSet("select area_name, area_code from zcdh_area where (is_delete=1 OR is_delete) and area_code regexp '^[0-9]{3}\\.[0-9]{3}$' and area_name not regexp '行政'", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int index) throws Exception {
				areaNameMap.put(resultSet.getString("area_name").replaceAll("\\s+|市$|盟$|地区$|族$|自治州$|族自治州$", ""), resultSet.getString("area_code"));
				return true;
			}
		});

		selectResultSet("select p.post_name, p.post_code, p.post_description, cp.post_category_name from zcdh_post p join zcdh_category_post cp on p.post_category_code=cp.post_category_code where p.is_delete=1 or p.is_delete is null", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int i) throws Exception {
				Map<String, String> postCategorie = new HashMap<String, String>();
				postCategorie.put("code", resultSet.getString("post_code"));
				postCategorie.put("name", resultSet.getString("post_name"));
				postCategorie.put("group", resultSet.getString("post_category_name"));
				postCategories.put(resultSet.getString("post_code"), postCategorie);
				return true;
			}
		});

		selectResultSet("select p.param_name, p.param_code, p.param_value, p.param_category_code, t.technical_code, t.techonlogy_gategory_code, t.match_type, g.percent from zcdh_param p left join zcdh_technology t on p.param_category_code=t.param_category_code left join zcdh_technology_gategory g on t.techonlogy_gategory_code=g.technology_gategory_code where (p.is_delete=1 or p.is_delete is null) and (t.is_delete=1 or t.is_delete is null) and (g.is_delete=1 or g.is_delete is null) and p.param_category_code in('007', '005', '004', '010', '011')", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int i) throws Exception {
				String category = resultSet.getString("param_category_code");
				if ("007".equals(category)) {
					postNatures.put(resultSet.getString("param_code"), resultSet.getString("param_name"));
				} else if ("005".equals(category)) {
					AbilityParam abilityParam = new AbilityParam();
					abilityParam.setName(resultSet.getString("param_name"));
					abilityParam.setCode(resultSet.getString("param_code"));
					abilityParam.setGrade(resultSet.getInt("param_value"));
					abilityParam.setCategory(resultSet.getString("param_category_code"));
					abilityParam.setTechnical(resultSet.getString("technical_code"));
					abilityParam.setTechnicalCategory(resultSet.getString("techonlogy_gategory_code"));
					abilityParam.setMatchType(resultSet.getInt("match_type"));
					abilityParam.setPercent(resultSet.getInt("percent"));
					postExperiences.put(abilityParam.getCode(), abilityParam);
				} else if ("004".equals(category)) {
					AbilityParam abilityParam = new AbilityParam();
					abilityParam.setName(resultSet.getString("param_name"));
					abilityParam.setCode(resultSet.getString("param_code"));
					abilityParam.setGrade(resultSet.getInt("param_value"));
					abilityParam.setCategory(resultSet.getString("param_category_code"));
					abilityParam.setTechnical(resultSet.getString("technical_code"));
					abilityParam.setTechnicalCategory(resultSet.getString("techonlogy_gategory_code"));
					abilityParam.setMatchType(resultSet.getInt("match_type"));
					abilityParam.setPercent(resultSet.getInt("percent"));
					postEducations.put(abilityParam.getCode(), abilityParam);
				} else if ("010".equals(category)) {
					enterpriseNatures.put(resultSet.getString("param_code"), resultSet.getString("param_name"));
				} else if ("011".equals(category)) {
					enterpriseScales.put(resultSet.getString("param_code"), resultSet.getString("param_name"));
				}
				return true;
			}
		});

		selectResultSet("select industry_name, industry_code from zcdh_industry where is_delete=1 or is_delete is null", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int i) throws Exception {
				enterpriseCategories.put(resultSet.getString("industry_code"), resultSet.getString("industry_name"));
				return true;
			}
		});

		selectResultSet("select a.account_id, a.account, a.create_mode, a.create_date account_date, e.create_date, e.ent_id, e.ent_name, e.industry, e.property, e.employ_num, e.ent_web, e.address, e.parea, e.lbs_id, e.introduction, e.data_src, e.data_url, l.latitude, l.longitude from zcdh_ent_account a left join zcdh_ent_enterprise e on a.ent_id=e.ent_id left join zcdh_ent_lbs l on e.lbs_id=l.lbs_id", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int i) throws Exception {
				Enterprise enterprise = new Enterprise();
				enterprise.setId(resultSet.getLong("ent_id"));
				Account account = new Account();
				account.setId(resultSet.getLong("account_id"));
				account.setEnterpriseId(enterprise.getId());
				account.setAccount(resultSet.getString("account"));
				account.setCreateMode(resultSet.getInt("create_mode"));
				account.setCreateDate(resultSet.getDate("account_date"));
				enterprise.setAccount(account);
				enterprise.setName(resultSet.getString("ent_name"));
				enterprise.setCategoryCode(resultSet.getString("industry"));
				if (Ver.isNotBlank(enterprise.getCategoryCode()))
					enterprise.setCategory(enterpriseCategories.get(enterprise.getCategoryCode()));
				enterprise.setNatureCode(resultSet.getString("property"));
				if (Ver.isNotBlank(enterprise.getNatureCode()))
					enterprise.setNature(enterpriseNatures.get(enterprise.getNatureCode()));
				enterprise.setScaleCode(resultSet.getString("employ_num"));
				if (Ver.isNotBlank(enterprise.getScaleCode()))
					enterprise.setScale(enterpriseScales.get(enterprise.getScaleCode()));
				enterprise.setIntroduction(resultSet.getString("introduction"));
				enterprise.setWebsite(resultSet.getString("ent_web"));
				enterprise.setAreaCode(resultSet.getString("parea"));
				enterprise.setAddress(resultSet.getString("address"));
				Long lbsId = resultSet.getLong("lbs_id");
				if (lbsId != null) {
					Lbs lbs = new Lbs();
					lbs.setId(lbsId);
					lbs.setLon(resultSet.getDouble("longitude"));
					lbs.setLat(resultSet.getDouble("latitude"));
					enterprise.setLbs(lbs);
				}
				enterprise.setDataSrc(resultSet.getString("data_src"));
				enterprise.setDataUrl(resultSet.getString("data_url"));
				enterprise.setCreateDate(resultSet.getDate("create_date"));

				if (Ver.isNotBlank(enterprise.getDataUrl()))
					enterprises.put(enterprise.getDataUrl(), enterprise);
				if (Ver.isNotBlank(enterprise.getName()))
					enterpriseNameMap.put(enterprise.getName(), enterprise);
				return true;
			}
		});

		selectResultSet("select id, update_date, data_url from zcdh_ent_post where data_src is not null and update_date is not null", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int i) throws Exception {
				Post post = new Post();
				post.setId(resultSet.getLong("id"));
				post.setName(resultSet.getString("post_aliases"));
				post.setCategory(resultSet.getString("post_name"));
				post.setCategoryCode(resultSet.getString("post_code"));
				post.setNumber(resultSet.getInt("headcounts"));
				post.setIsSeveral(resultSet.getInt("is_several"));
				post.setNumberText(post.getIsSeveral() == 1 ? "若干" : (post.getNumber() == null ? null : String.valueOf(post.getNumber())));
				post.setNatureCode(resultSet.getString("pjob_category"));
				if (Ver.isNotBlank(post.getNatureCode()))
					post.setNature(postNatures.get(post.getNatureCode()));
				post.setSalary(resultSet.getString("psalary"));
				if (Ver.isNotBlank(post.getSalary()))
					post.setSalaryText("0".equals(post.getSalary()) ? "面议" : post.getSalary());
				post.setSalaryType(resultSet.getInt("salary_type"));

				Long enterpriseId = resultSet.getLong("ent_id");
				post.setExperienceCode(resultSet.getString("ex_code"));
				if (Ver.isNotBlank(post.getExperienceCode())) {
					AbilityParam abilityParam = postExperiences.get(post.getExperienceCode());
					if (abilityParam != null) {
						post.setExperience(abilityParam.getName());
						Ability ability = new Ability();
						ability.setId(resultSet.getLong("ex_id"));
						ability.setPostId(post.getId());
						ability.setPostCode(post.getCategoryCode());
						ability.setEnterpriseId(enterpriseId);
						ability.setName(abilityParam.getName());
						ability.setCode(abilityParam.getCode());
						post.setExperienceAbility(ability);
					}
				}

				post.setEducationCode(resultSet.getString("ed_code"));
				if (Ver.isNotBlank(post.getEducationCode())) {
					AbilityParam abilityParam = postEducations.get(post.getEducationCode());
					if (abilityParam != null) {
						post.setEducation(abilityParam.getName());
						Ability ability = new Ability();
						ability.setId(resultSet.getLong("ed_id"));
						ability.setPostId(post.getId());
						ability.setPostCode(post.getCategoryCode());
						ability.setEnterpriseId(enterpriseId);
						ability.setName(abilityParam.getName());
						ability.setCode(abilityParam.getCode());
						post.setExperienceAbility(ability);
					}
				}

				post.setWelfareCode(resultSet.getString("tag_selected"));
				if (Ver.isNotBlank(post.getWelfareCode()))
					post.setWelfare(post.getWelfareCode().replaceAll("\\d|system|self|&", "").replaceAll("\\$\\$", " "));
				post.setIntroduction(resultSet.getString("post_remark"));
				post.setAreaCode(resultSet.getString("parea"));
				post.setAddress(resultSet.getString("post_address"));
				Long lbsId = resultSet.getLong("lbs_id");
				if (lbsId != null) {
					Lbs lbs = new Lbs();
					lbs.setId(lbsId);
					lbs.setLon(resultSet.getDouble("longitude"));
					lbs.setLat(resultSet.getDouble("latitude"));
					post.setLbs(lbs);
				}
				post.setDataSrc(resultSet.getString("data_src"));
				post.setDataUrl(resultSet.getString("data_url"));
				post.setUpdateDate(resultSet.getDate("update_date"));
				post.setPublishDate(resultSet.getDate("publish_date"));
				post.setEnterpriseUrl(resultSet.getString("ent_url"));
				post.setEnterpriseName(resultSet.getString("ent_name"));

				posts.put(resultSet.getString("data_url"), post);
				return true;
			}
		});
		logger.info("-------------------------------");
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

	public static Map<String, Map<String, String>> getPostCategories() {
		return postCategories;
	}

	public static Map<String, String> getPostCategory(String category) {
		return postCategories.get(category);
	}

	public static String getPostNature(String paramName) {
		return postNatures.get(paramName);
	}

	public static Map<String, AbilityParam> getPostExperiences() {
		return postExperiences;
	}

	public static AbilityParam getPostExperience(String paramName) {
		return postExperiences.get(paramName);
	}

	public static Map<String, AbilityParam> getPostEducations() {
		return postEducations;
	}

	public static AbilityParam getPostEducation(String paramName) {
		return postEducations.get(paramName);
	}

	public static Map<String, String> getEnterpriseCategories() {
		return enterpriseCategories;
	}

	public static String getEnterpriseCategory(String category) {
		return enterpriseCategories.get(category);
	}

	public static Map<String, String> getEnterpriseNatures() {
		return enterpriseNatures;
	}

	public static String getEnterpriseNature(String paramName) {
		return enterpriseNatures.get(paramName);
	}

	public static Map<String, String> getEnterpriseScales() {
		return enterpriseScales;
	}

	public static String getEnterpriseScale(String paramName) {
		return enterpriseScales.get(paramName);
	}

	public static Boolean existEnterprise(String enterpriseName) {
		return enterpriseNameMap.containsKey(enterpriseName);
	}

	public static Enterprise getEnterprise(String enterpriseName) {
		return enterpriseNameMap.get(enterpriseName);
	}

	public static void savePost(List<Post> list, Integer updateInterval) {
		if (updateInterval == null)
			updateInterval = 3;
		String postMiniUpdateSQL = "update zcdh_ent_post set update_date=? where id=?";
		String lbsUpdateSQL = "update zcdh_ent_lbs set longitude=?, latitude=? where lbs_id=?";
		String postUpdateSQL = "update zcdh_ent_post set ent_id=?, post_aliases=?, post_name=?, post_code=?, headcounts=?, is_several=?, pjob_category=?, psalary=?, salary_type=?, tag_selected=?, post_remark=?, parea=?, post_address=?, lbs_id=?, data_src=?, data_url=?, update_date=?, publish_date=? where id=?";
		String postStatusUpdateSQL = "update zcdh_ent_post_status set employ_total=?, un_employ=employ_total-employed where post_id=?";
		String abilityUpdateSQL = "update zcdh_ent_ability_require set post_code=?, param_code=?, grade=?, weight_point=total_point/grade where post_id=? and technology_code=?";
		String postViewUpdateSQL = "update zcdh_view_ent_post set ent_name=?, industry=?, property=?, employ_num=?, post_aliases=?, post_code=?, salary_code=?, min_salary=?, max_salary=?, salary_type=?, post_property_code=? where post_id=?";
		String lbsInsertSQL = "insert into zcdh_ent_lbs(longitude, latitude) values(?, ?)";
		String postInsertSQL = "insert into zcdh_ent_post(ent_id, post_aliases, post_name, post_code, headcounts, is_several, pjob_category, psalary, salary_type, tag_selected, post_remark, parea, post_address, lbs_id, data_src, data_url, update_date, publish_date) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String postStatusInsertSQL = "insert into zcdh_ent_post_status(post_id, post_status, employ, employed, employ_total, un_employ, skim_count) values(?, ?, ?, ?, ?, ?, ?)";
		String abilityInsertSQL = "insert into zcdh_ent_ability_require(post_id, ent_id, post_code, param_code, grade, match_type, technology_code, technology_cate_code, total_point, weight_point) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String postViewInsertSQL = "insert into zcdh_view_ent_post(post_id, ent_name, industry, property, employ_num, post_aliases, post_code, salary_code, min_salary, max_salary, salary_type, post_property_code, publish_date) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String promotionInsertSQL = "insert into zcdh_ent_promotion(ent_post_id, ent_id, promotion_value) values(?, ?, ?)";
		Connection connection = openConnection();
		PreparedStatement postMiniUpdateStatement = null;
		PreparedStatement lbsUpdateStatement = null;
		PreparedStatement postUpdateStatement = null;
		PreparedStatement postStatusUpdateStatement = null;
		PreparedStatement abilityUpdateStatement = null;
		PreparedStatement postViewUpdateStatement = null;
		PreparedStatement lbsInsertStatement = null;
		PreparedStatement postInsertStatement = null;
		PreparedStatement postStatusInsertStatement = null;
		PreparedStatement abilityInsertStatement = null;
		PreparedStatement postViewInsertStatement = null;
		PreparedStatement promotionInsertStatement = null;
		ResultSet lbsInsertedKeyResultSet = null;
		ResultSet postInsertedKeyResultSet = null;
		ResultSet postStatusInsertedKeyResultSet = null;
		ResultSet abilityInsertedKeyResultSet = null;
		ResultSet postViewInsertedKeyResultSet = null;
		ResultSet promotionInsertedKeyResultSet = null;
		List<Post> miniUpdatedPosts = new ArrayList<Post>();
		List<Lbs> updatedLbses = new ArrayList<Lbs>();
		List<Post> updatedPosts = new ArrayList<Post>();
		List<Post> updatedPostStatuses = new ArrayList<Post>();
		List<Ability> updatedAbilities = new ArrayList<Ability>();
		List<Post> updatedPostViews = new ArrayList<Post>();
		List<Lbs> insertedLbses = new ArrayList<Lbs>();
		List<Post> insertedPosts = new ArrayList<Post>();
		List<Post> insertedPostStatuses = new ArrayList<Post>();
		List<Ability> insertedAbilities = new ArrayList<Ability>();
		List<Post> insertedPostViews = new ArrayList<Post>();
		List<Post> insertedPromotions = new ArrayList<Post>();
		try {
			postMiniUpdateStatement = connection.prepareStatement(postMiniUpdateSQL);
			lbsUpdateStatement = connection.prepareStatement(lbsUpdateSQL);
			postUpdateStatement = connection.prepareStatement(postUpdateSQL);
			postStatusUpdateStatement = connection.prepareStatement(postStatusUpdateSQL);
			abilityUpdateStatement = connection.prepareStatement(abilityUpdateSQL);
			postViewUpdateStatement = connection.prepareStatement(postViewUpdateSQL);
			lbsInsertStatement = connection.prepareStatement(lbsInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			postInsertStatement = connection.prepareStatement(postInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			postStatusInsertStatement = connection.prepareStatement(postStatusInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			abilityInsertStatement = connection.prepareStatement(abilityInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			postViewInsertStatement = connection.prepareStatement(postViewInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			promotionInsertStatement = connection.prepareStatement(promotionInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			for (Post p : list) {

				Lbs lbs = p.getLbs();
				if (lbs.getId() != null) {
					if (lbs.getDirty()) {
						lbsUpdateStatement.setDouble(1, lbs.getLon());
						lbsUpdateStatement.setDouble(2, lbs.getLat());
						lbsUpdateStatement.setLong(3, lbs.getId());
						lbsUpdateStatement.addBatch();

						lbs.setDirty(false);
						updatedLbses.add(lbs);
					}
				} else {
					lbsInsertStatement.setDouble(1, lbs.getLon());
					lbsInsertStatement.setDouble(2, lbs.getLat());
					lbsInsertStatement.addBatch();

					insertedLbses.add(lbs);
				}

				p.setStatus(1);

				if (p.getId() != null) {
					if (p.getDirty()) {
						updatedPosts.add(p);
					} else {
						Long currentTimeMillis = System.currentTimeMillis();
						if (p.getUpdateDate().getTime() - currentTimeMillis >= 24 * 60 * 60 * 1000) {
							postMiniUpdateStatement.setDate(1, new Date(currentTimeMillis));
							postMiniUpdateStatement.setLong(2, p.getId());
							postMiniUpdateStatement.addBatch();
							miniUpdatedPosts.add(p);
						}
					}
				} else {
					insertedPosts.add(p);
				}
			}

			postMiniUpdateStatement.executeBatch();
			lbsUpdateStatement.executeBatch();

			lbsInsertStatement.executeBatch();
			lbsInsertedKeyResultSet = lbsInsertStatement.getGeneratedKeys();
			for (int i = 0; lbsInsertedKeyResultSet.next(); i++)
				insertedLbses.get(i).setId(lbsInsertedKeyResultSet.getLong(1));
			
			for (Post p : updatedPosts){
				Enterprise enterprise = enterpriseNameMap.get(p.getEnterpriseName());
				postUpdateStatement.setLong(1, enterprise.getId());
				postUpdateStatement.setString(2, p.getName());
				postUpdateStatement.setString(3, p.getCategory());
				postUpdateStatement.setString(4, p.getCategoryCode());
				postUpdateStatement.setInt(5, p.getNumber());
				postUpdateStatement.setInt(6, p.getIsSeveral());
				postUpdateStatement.setString(7, p.getNatureCode());
				postUpdateStatement.setString(8, p.getSalary());
				postUpdateStatement.setInt(9, p.getSalaryType());
				postUpdateStatement.setString(10, p.getWelfareCode());
				postUpdateStatement.setString(11, p.getIntroduction());
				postUpdateStatement.setString(12, p.getAreaCode());
				postUpdateStatement.setString(13, p.getAddress());
				postUpdateStatement.setString(14, p.getAddress());
				postUpdateStatement.setString(15, p.getDataSrc());
				postUpdateStatement.setString(16, p.getDataUrl());
				postUpdateStatement.setDate(17, new Date(p.getUpdateDate().getTime()));
				postUpdateStatement.setDate(18, new Date(p.getUpdateDate().getTime()));
				postUpdateStatement.setLong(19, p.getId());
				postUpdateStatement.addBatch();
				
				p.setDirty(false);
				p.setStatus(3);
			}
			
			postUpdateStatement.executeBatch();
			

			entLbsInsertStatement.executeBatch();
			entLbsInsertedKeyResultSet = entLbsInsertStatement.getGeneratedKeys();
			for (int i = 0; entLbsInsertedKeyResultSet.next(); i++) {
				Post p = insertedPosts.get(i);
				p.setLbsId(entLbsInsertedKeyResultSet.getLong(1));

				Enterprise enterprise = enterprises.get(p.getEnterpriseUrl());

				entPostInsertStatement.setDate(1, new java.sql.Date(p.getDate().getTime()));
				entPostInsertStatement.setDate(2, new java.sql.Date(p.getDate().getTime()));
				entPostInsertStatement.setLong(3, enterprise.getId());
				entPostInsertStatement.setString(4, p.getName());
				entPostInsertStatement.setString(5, p.getCategory());
				entPostInsertStatement.setString(6, p.getCategoryCode());
				entPostInsertStatement.setString(7, p.getNatureCode());
				entPostInsertStatement.setInt(8, p.getNumber());
				entPostInsertStatement.setInt(9, p.getIsSeveral());
				entPostInsertStatement.setString(10, p.getSalary());
				entPostInsertStatement.setInt(11, p.getSalaryType());
				entPostInsertStatement.setString(12, p.getWelfareCode());
				entPostInsertStatement.setString(13, p.getAddress());
				entPostInsertStatement.setString(14, p.getAreaCode());
				entPostInsertStatement.setLong(15, p.getLbsId());
				entPostInsertStatement.setString(16, p.getIntroduction());
				entPostInsertStatement.setString(17, p.getSrc());
				entPostInsertStatement.setString(18, p.getUrl());
				entPostInsertStatement.addBatch();
			}
			entPostInsertStatement.executeBatch();
			entPostInsertedKeyResultSet = entPostInsertStatement.getGeneratedKeys();
			for (int i = 0; entPostInsertedKeyResultSet.next(); i++) {
				Post p = insertedPosts.get(i);
				p.setId(entPostInsertedKeyResultSet.getLong(1));
				p.setStatus(2);
				posts.put(p.getUrl(), p);

				Enterprise enterprise = enterprises.get(p.getEnterpriseUrl());

				entPostStatusInsertStatement.setLong(1, p.getId());
				entPostStatusInsertStatement.setInt(2, 1);
				entPostStatusInsertStatement.setInt(3, 0);
				entPostStatusInsertStatement.setInt(4, 0);
				entPostStatusInsertStatement.setInt(5, p.getNumber());
				entPostStatusInsertStatement.setInt(6, p.getNumber());
				entPostStatusInsertStatement.setInt(7, 0);
				entPostStatusInsertStatement.addBatch();

				entPromotionInsertStatement.setLong(1, p.getId());
				entPromotionInsertStatement.setLong(2, enterprise.getId());
				entPromotionInsertStatement.setString(3, "");
				entPromotionInsertStatement.addBatch();

				Map<String, String> experience = p.getExperienceAbility();
				Integer experienceParamValue = Integer.parseInt(experience.get("paramValue"));
				Integer experienceMatchType = Integer.parseInt(experience.get("matchType"));
				String experienceTechnicalCode = experience.get("technicalCode");
				String experienceTechonlogyGategoryCode = experience.get("techonlogyGategoryCode");
				Double experiencePercent = Double.parseDouble(experience.get("percent"));
				Double experienceTotalPoint = experiencePercent / 2;
				entAbilityRequireInsertStatement.setLong(1, p.getId());
				entAbilityRequireInsertStatement.setLong(2, enterprise.getId());
				entAbilityRequireInsertStatement.setString(3, p.getCategoryCode());
				entAbilityRequireInsertStatement.setString(4, p.getExperienceCode());
				entAbilityRequireInsertStatement.setInt(5, experienceParamValue);
				entAbilityRequireInsertStatement.setInt(6, experienceMatchType);
				entAbilityRequireInsertStatement.setString(7, experienceTechnicalCode);
				entAbilityRequireInsertStatement.setString(8, experienceTechonlogyGategoryCode);
				entAbilityRequireInsertStatement.setDouble(9, experienceTotalPoint);
				entAbilityRequireInsertStatement.setDouble(10, experienceTotalPoint / experienceParamValue);
				entAbilityRequireInsertStatement.addBatch();
				Map<String, String> education = p.getEducationAbility();
				Integer educationParamValue = Integer.parseInt(education.get("paramValue"));
				Integer educationMatchType = Integer.parseInt(education.get("matchType"));
				String educationTechnicalCode = education.get("technicalCode");
				String educationTechonlogyGategoryCode = education.get("techonlogyGategoryCode");
				Double educationPercent = Double.parseDouble(education.get("percent"));
				Double educationTotalPoint = educationPercent / 2;
				entAbilityRequireInsertStatement.setLong(1, p.getId());
				entAbilityRequireInsertStatement.setLong(2, enterprise.getId());
				entAbilityRequireInsertStatement.setString(3, p.getCategoryCode());
				entAbilityRequireInsertStatement.setString(4, p.getEducationCode());
				entAbilityRequireInsertStatement.setInt(5, educationParamValue);
				entAbilityRequireInsertStatement.setInt(6, educationMatchType);
				entAbilityRequireInsertStatement.setString(7, educationTechnicalCode);
				entAbilityRequireInsertStatement.setString(8, educationTechonlogyGategoryCode);
				entAbilityRequireInsertStatement.setDouble(9, educationTotalPoint);
				entAbilityRequireInsertStatement.setDouble(10, educationTotalPoint / educationParamValue);
				entAbilityRequireInsertStatement.addBatch();

				viewEntPostInsertStatement.setLong(1, p.getId());
				viewEntPostInsertStatement.setString(2, enterprise.getName());
				viewEntPostInsertStatement.setString(3, enterprise.getCategoryCode());
				viewEntPostInsertStatement.setString(4, enterprise.getNatureCode());
				viewEntPostInsertStatement.setString(5, enterprise.getScaleCode());
				viewEntPostInsertStatement.setString(6, p.getName());
				viewEntPostInsertStatement.setString(7, p.getCategoryCode());
				viewEntPostInsertStatement.setString(8, p.getSalary());
				Integer minSalary = null;
				Integer maxSalary = null;
				if (p.getSalary() != null && p.getSalary().contains("-")) {
					String[] salaries = p.getSalary().split("-", 2);
					if (salaries[0].matches("^\\d+$"))
						minSalary = Integer.parseInt(salaries[0]);
					if (salaries[1].matches("^\\d+$"))
						maxSalary = Integer.parseInt(salaries[1]);
				}
				if (minSalary == null || maxSalary == null) {
					viewEntPostInsertStatement.setNull(9, Types.INTEGER);
					viewEntPostInsertStatement.setNull(10, Types.INTEGER);
				} else {
					viewEntPostInsertStatement.setInt(9, minSalary);
					viewEntPostInsertStatement.setInt(10, maxSalary);
				}
				viewEntPostInsertStatement.setInt(11, p.getSalaryType());
				viewEntPostInsertStatement.setString(12, p.getNatureCode());
				viewEntPostInsertStatement.setDate(13, new java.sql.Date(p.getDate().getTime()));
				viewEntPostInsertStatement.addBatch();
			}
			entPostStatusInsertStatement.executeBatch();
			entPromotionInsertStatement.executeBatch();
			entAbilityRequireInsertStatement.executeBatch();
			viewEntPostInsertStatement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (entLbsInsertedKeyResultSet != null && !entLbsInsertedKeyResultSet.isClosed())
					entLbsInsertedKeyResultSet.close();
				if (entPostInsertedKeyResultSet != null && !entPostInsertedKeyResultSet.isClosed())
					entPostInsertedKeyResultSet.close();
				if (entPostUpdateStatement != null && !entPostUpdateStatement.isClosed())
					entPostUpdateStatement.close();
				if (entLbsInsertStatement != null && !entLbsInsertStatement.isClosed())
					entLbsInsertStatement.close();
				if (entPostInsertStatement != null && !entPostInsertStatement.isClosed())
					entPostInsertStatement.close();
				if (entPostStatusInsertStatement != null && !entPostStatusInsertStatement.isClosed())
					entPostStatusInsertStatement.close();
				if (entPromotionInsertStatement != null && !entPromotionInsertStatement.isClosed())
					entPromotionInsertStatement.close();
				if (entAbilityRequireInsertStatement != null && !entAbilityRequireInsertStatement.isClosed())
					entAbilityRequireInsertStatement.close();
				if (viewEntPostInsertStatement != null && !viewEntPostInsertStatement.isClosed())
					viewEntPostInsertStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static Boolean savePost(Post post) {
		post.setStatus(1);
		if (post.getId() == null) {
			String entLbsInsertSQL = "insert into zcdh_ent_lbs(longitude, latitude) values(?, ?)";
			String entPostInsertSQL = "insert into zcdh_ent_post(publish_date, update_date, ent_id, post_aliases, post_name, post_code, pjob_category, headcounts, is_several, psalary, salary_type, tag_selected, post_address, parea, lbs_id, post_remark, data_src, data_url) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			String entPostStatusInsertSQL = "insert into zcdh_ent_post_status(post_id, post_status, employ, employed, employ_total, un_employ, skim_count) values(?, ?, ?, ?, ?, ?, ?)";
			String entPromotionInsertSQL = "insert into zcdh_ent_promotion(ent_post_id, ent_id, promotion_value) values(?, ?, ?)";
			String entAbilityRequireInsertSQL = "insert into zcdh_ent_ability_require(post_id, ent_id, post_code, param_code, grade, match_type, technology_code, technology_cate_code, total_point, weight_point) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			String viewEntPostInsertSQL = "insert into zcdh_view_ent_post(post_id, ent_name, industry, property, employ_num, post_aliases, post_code, salary_code, min_salary, max_salary, salary_type, post_property_code, publish_date) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			Connection connection = openConnection();
			PreparedStatement entLbsInsertStatement = null;
			PreparedStatement entPostInsertStatement = null;
			PreparedStatement entPostStatusInsertStatement = null;
			PreparedStatement entPromotionInsertStatement = null;
			PreparedStatement entAbilityRequireInsertStatement = null;
			PreparedStatement viewEntPostInsertStatement = null;
			ResultSet entLbsInsertedKeyResultSet = null;
			ResultSet entPostInsertedKeyResultSet = null;
			try {
				entLbsInsertStatement = connection.prepareStatement(entLbsInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
				entPostInsertStatement = connection.prepareStatement(entPostInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
				entPostStatusInsertStatement = connection.prepareStatement(entPostStatusInsertSQL);
				entPromotionInsertStatement = connection.prepareStatement(entPromotionInsertSQL);
				entAbilityRequireInsertStatement = connection.prepareStatement(entAbilityRequireInsertSQL);
				viewEntPostInsertStatement = connection.prepareStatement(viewEntPostInsertSQL);

				entLbsInsertStatement.setDouble(1, post.getLbsLon());
				entLbsInsertStatement.setDouble(2, post.getLbsLat());
				entLbsInsertStatement.executeUpdate();

				entLbsInsertedKeyResultSet = entLbsInsertStatement.getGeneratedKeys();
				if (entLbsInsertedKeyResultSet.next()) {
					post.setLbsId(entLbsInsertedKeyResultSet.getLong(1));

					Enterprise enterprise = enterprises.get(post.getEnterpriseUrl());

					entPostInsertStatement.setDate(1, new java.sql.Date(post.getDate().getTime()));
					entPostInsertStatement.setDate(2, new java.sql.Date(post.getDate().getTime()));
					entPostInsertStatement.setLong(3, enterprise.getId());
					entPostInsertStatement.setString(4, post.getName());
					entPostInsertStatement.setString(5, post.getCategory());
					entPostInsertStatement.setString(6, post.getCategoryCode());
					entPostInsertStatement.setString(7, post.getNatureCode());
					entPostInsertStatement.setInt(8, post.getNumber());
					entPostInsertStatement.setInt(9, post.getIsSeveral());
					entPostInsertStatement.setString(10, post.getSalary());
					entPostInsertStatement.setInt(11, post.getSalaryType());
					entPostInsertStatement.setString(12, post.getWelfareCode());
					entPostInsertStatement.setString(13, post.getAddress());
					entPostInsertStatement.setString(14, post.getAreaCode());
					entPostInsertStatement.setLong(15, post.getLbsId());
					entPostInsertStatement.setString(16, post.getIntroduction());
					entPostInsertStatement.setString(17, post.getSrc());
					entPostInsertStatement.setString(18, post.getUrl());
					entPostInsertStatement.executeUpdate();

					entPostInsertedKeyResultSet = entPostInsertStatement.getGeneratedKeys();
					if (entPostInsertedKeyResultSet.next()) {
						post.setId(entPostInsertedKeyResultSet.getLong(1));
						post.setStatus(2);
						posts.put(post.getUrl(), post);

						entPostStatusInsertStatement.setLong(1, post.getId());
						entPostStatusInsertStatement.setInt(2, 1);
						entPostStatusInsertStatement.setInt(3, 0);
						entPostStatusInsertStatement.setInt(4, 0);
						entPostStatusInsertStatement.setInt(5, post.getNumber());
						entPostStatusInsertStatement.setInt(6, post.getNumber());
						entPostStatusInsertStatement.setInt(7, 0);
						entPostStatusInsertStatement.executeUpdate();

						entPromotionInsertStatement.setLong(1, post.getId());
						entPromotionInsertStatement.setLong(2, enterprise.getId());
						entPromotionInsertStatement.setString(3, "");
						entPromotionInsertStatement.executeUpdate();

						Map<String, String> experience = post.getExperienceAbility();
						Integer experienceParamValue = Integer.parseInt(experience.get("paramValue"));
						Integer experienceMatchType = Integer.parseInt(experience.get("matchType"));
						String experienceTechnicalCode = experience.get("technicalCode");
						String experienceTechonlogyGategoryCode = experience.get("techonlogyGategoryCode");
						Double experiencePercent = Double.parseDouble(experience.get("percent"));
						Double experienceTotalPoint = experiencePercent / 2;
						entAbilityRequireInsertStatement.setLong(1, post.getId());
						entAbilityRequireInsertStatement.setLong(2, enterprise.getId());
						entAbilityRequireInsertStatement.setString(3, post.getCategoryCode());
						entAbilityRequireInsertStatement.setString(4, post.getExperienceCode());
						entAbilityRequireInsertStatement.setInt(5, experienceParamValue);
						entAbilityRequireInsertStatement.setInt(6, experienceMatchType);
						entAbilityRequireInsertStatement.setString(7, experienceTechnicalCode);
						entAbilityRequireInsertStatement.setString(8, experienceTechonlogyGategoryCode);
						entAbilityRequireInsertStatement.setDouble(9, experienceTotalPoint);
						entAbilityRequireInsertStatement.setDouble(10, experienceTotalPoint / experienceParamValue);
						entAbilityRequireInsertStatement.addBatch();
						Map<String, String> education = post.getEducationAbility();
						Integer educationParamValue = Integer.parseInt(education.get("paramValue"));
						Integer educationMatchType = Integer.parseInt(education.get("matchType"));
						String educationTechnicalCode = education.get("technicalCode");
						String educationTechonlogyGategoryCode = education.get("techonlogyGategoryCode");
						Double educationPercent = Double.parseDouble(education.get("percent"));
						Double educationTotalPoint = educationPercent / 2;
						entAbilityRequireInsertStatement.setLong(1, post.getId());
						entAbilityRequireInsertStatement.setLong(2, enterprise.getId());
						entAbilityRequireInsertStatement.setString(3, post.getCategoryCode());
						entAbilityRequireInsertStatement.setString(4, post.getEducationCode());
						entAbilityRequireInsertStatement.setInt(5, educationParamValue);
						entAbilityRequireInsertStatement.setInt(6, educationMatchType);
						entAbilityRequireInsertStatement.setString(7, educationTechnicalCode);
						entAbilityRequireInsertStatement.setString(8, educationTechonlogyGategoryCode);
						entAbilityRequireInsertStatement.setDouble(9, educationTotalPoint);
						entAbilityRequireInsertStatement.setDouble(10, educationTotalPoint / educationParamValue);
						entAbilityRequireInsertStatement.addBatch();
						entAbilityRequireInsertStatement.executeBatch();

						viewEntPostInsertStatement.setLong(1, post.getId());
						viewEntPostInsertStatement.setString(2, enterprise.getName());
						viewEntPostInsertStatement.setString(3, enterprise.getCategoryCode());
						viewEntPostInsertStatement.setString(4, enterprise.getNatureCode());
						viewEntPostInsertStatement.setString(5, enterprise.getScaleCode());
						viewEntPostInsertStatement.setString(6, post.getName());
						viewEntPostInsertStatement.setString(7, post.getCategoryCode());
						viewEntPostInsertStatement.setString(8, post.getSalary());
						Integer minSalary = null;
						Integer maxSalary = null;
						if (post.getSalary() != null && post.getSalary().contains("-")) {
							String[] salaries = post.getSalary().split("-", 2);
							if (salaries[0].matches("^\\d+$"))
								minSalary = Integer.parseInt(salaries[0]);
							if (salaries[1].matches("^\\d+$"))
								maxSalary = Integer.parseInt(salaries[1]);
						}
						if (minSalary == null || maxSalary == null) {
							viewEntPostInsertStatement.setNull(9, Types.INTEGER);
							viewEntPostInsertStatement.setNull(10, Types.INTEGER);
						} else {
							viewEntPostInsertStatement.setInt(9, minSalary);
							viewEntPostInsertStatement.setInt(10, maxSalary);
						}
						viewEntPostInsertStatement.setInt(11, post.getSalaryType());
						viewEntPostInsertStatement.setString(12, post.getNatureCode());
						viewEntPostInsertStatement.setDate(13, new java.sql.Date(post.getDate().getTime()));
						viewEntPostInsertStatement.executeUpdate();

						return true;
					}
				}
				return false;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					if (entLbsInsertedKeyResultSet != null && !entLbsInsertedKeyResultSet.isClosed())
						entLbsInsertedKeyResultSet.close();
					if (entPostInsertedKeyResultSet != null && !entPostInsertedKeyResultSet.isClosed())
						entPostInsertedKeyResultSet.close();
					if (entLbsInsertStatement != null && !entLbsInsertStatement.isClosed())
						entLbsInsertStatement.close();
					if (entPostInsertStatement != null && !entPostInsertStatement.isClosed())
						entPostInsertStatement.close();
					if (entPostStatusInsertStatement != null && !entPostStatusInsertStatement.isClosed())
						entPostStatusInsertStatement.close();
					if (entPromotionInsertStatement != null && !entPromotionInsertStatement.isClosed())
						entPromotionInsertStatement.close();
					if (entAbilityRequireInsertStatement != null && !entAbilityRequireInsertStatement.isClosed())
						entAbilityRequireInsertStatement.close();
					if (viewEntPostInsertStatement != null && !viewEntPostInsertStatement.isClosed())
						viewEntPostInsertStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			String entLbsUpdateSQL = "update zcdh_ent_lbs set longitude=?, latitude=? where lbs_id=?";
			String entPostUpdateSQL = "update zcdh_ent_post set update_date=?, post_aliases=?, post_name=?, post_code=?, pjob_category=?, headcounts=?, is_several=?, psalary=?, salary_type=?, tag_selected=?, post_address=?, parea=?, post_remark=? where id=?";
			String entPostStatusUpdateSQL = "update zcdh_ent_post_status set employ_total=?, un_employ=employ_total-employed where post_id=?";
			String entAbilityRequireUpdateSQL = "update zcdh_ent_ability_require set post_code=?, param_code=?, grade=?, weight_point=total_point/grade where post_id=? and technology_code=?";
			String viewEntPostUpdateSQL = "update zcdh_view_ent_post set ent_name=?, industry=?, property=?, employ_num=?, post_aliases=?, post_code=?, salary_code=?, min_salary=?, max_salary=?, salary_type=?, post_property_code=? where post_id=?";
			Connection connection = openConnection();
			PreparedStatement entLbsUpdateStatement = null;
			PreparedStatement entPostUpdateStatement = null;
			PreparedStatement entPostStatusUpdateStatement = null;
			PreparedStatement entAbilityRequireUpdateStatement = null;
			PreparedStatement viewEntPostUpdateStatement = null;
			try {
				entLbsUpdateStatement = connection.prepareStatement(entLbsUpdateSQL);
				entPostUpdateStatement = connection.prepareStatement(entPostUpdateSQL);
				entPostStatusUpdateStatement = connection.prepareStatement(entPostStatusUpdateSQL);
				entAbilityRequireUpdateStatement = connection.prepareStatement(entAbilityRequireUpdateSQL);
				viewEntPostUpdateStatement = connection.prepareStatement(viewEntPostUpdateSQL);

				Enterprise enterprise = enterprises.get(post.getEnterpriseUrl());
				entLbsUpdateStatement.setDouble(1, enterprise.getLbsLon());
				entLbsUpdateStatement.setDouble(2, enterprise.getLbsLat());
				entLbsUpdateStatement.setDouble(3, post.getLbsId());
				entLbsUpdateStatement.executeUpdate();

				entPostUpdateStatement.setDate(1, new java.sql.Date(System.currentTimeMillis()));
				entPostUpdateStatement.setString(2, post.getName());
				entPostUpdateStatement.setString(3, post.getCategory());
				entPostUpdateStatement.setString(4, post.getCategoryCode());
				entPostUpdateStatement.setString(5, post.getNatureCode());
				entPostUpdateStatement.setInt(6, post.getNumber());
				entPostUpdateStatement.setInt(7, post.getIsSeveral());
				entPostUpdateStatement.setString(8, post.getSalary());
				entPostUpdateStatement.setInt(9, post.getSalaryType());
				entPostUpdateStatement.setString(10, post.getWelfareCode());
				entPostUpdateStatement.setString(11, enterprise.getAddress());
				entPostUpdateStatement.setString(12, enterprise.getAreaCode());
				entPostUpdateStatement.setString(13, post.getIntroduction());
				entPostUpdateStatement.setLong(14, post.getId());
				entPostUpdateStatement.executeUpdate();

				post.setStatus(3);
				posts.put(post.getUrl(), post);

				entPostStatusUpdateStatement.setInt(1, post.getNumber());
				entPostStatusUpdateStatement.setLong(2, post.getId());
				entPostStatusUpdateStatement.executeUpdate();

				Map<String, String> experience = post.getExperienceAbility();
				Integer experienceParamValue = Integer.parseInt(experience.get("paramValue"));
				String experienceTechnicalCode = experience.get("technicalCode");
				entAbilityRequireUpdateStatement.setString(1, post.getCategoryCode());
				entAbilityRequireUpdateStatement.setString(2, post.getExperienceCode());
				entAbilityRequireUpdateStatement.setInt(3, experienceParamValue);
				entAbilityRequireUpdateStatement.setLong(4, post.getId());
				entAbilityRequireUpdateStatement.setString(5, experienceTechnicalCode);
				entAbilityRequireUpdateStatement.addBatch();
				Map<String, String> education = post.getEducationAbility();
				Integer educationParamValue = Integer.parseInt(education.get("paramValue"));
				String educationTechnicalCode = education.get("technicalCode");
				entAbilityRequireUpdateStatement.setString(1, post.getCategoryCode());
				entAbilityRequireUpdateStatement.setString(2, post.getExperienceCode());
				entAbilityRequireUpdateStatement.setInt(3, educationParamValue);
				entAbilityRequireUpdateStatement.setLong(4, post.getId());
				entAbilityRequireUpdateStatement.setString(5, educationTechnicalCode);
				entAbilityRequireUpdateStatement.addBatch();
				entAbilityRequireUpdateStatement.executeBatch();

				viewEntPostUpdateStatement.setString(1, enterprise.getName());
				viewEntPostUpdateStatement.setString(2, enterprise.getCategoryCode());
				viewEntPostUpdateStatement.setString(3, enterprise.getNatureCode());
				viewEntPostUpdateStatement.setString(4, enterprise.getScaleCode());
				viewEntPostUpdateStatement.setString(5, post.getName());
				viewEntPostUpdateStatement.setString(6, post.getCategoryCode());
				viewEntPostUpdateStatement.setString(7, post.getSalary());
				Integer minSalary = null;
				Integer maxSalary = null;
				if (post.getSalary() != null && post.getSalary().contains("-")) {
					String[] salaries = post.getSalary().split("-", 2);
					if (salaries[0].matches("^\\d+$"))
						minSalary = Integer.parseInt(salaries[0]);
					if (salaries[1].matches("^\\d+$"))
						maxSalary = Integer.parseInt(salaries[1]);
				}
				if (minSalary == null || maxSalary == null) {
					viewEntPostUpdateStatement.setNull(8, Types.INTEGER);
					viewEntPostUpdateStatement.setNull(9, Types.INTEGER);
				} else {
					viewEntPostUpdateStatement.setInt(8, minSalary);
					viewEntPostUpdateStatement.setInt(9, maxSalary);
				}
				viewEntPostUpdateStatement.setInt(10, post.getSalaryType());
				viewEntPostUpdateStatement.setString(11, post.getNatureCode());
				viewEntPostUpdateStatement.setLong(12, post.getId());
				viewEntPostUpdateStatement.executeUpdate();

				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					if (entLbsUpdateStatement != null && !entLbsUpdateStatement.isClosed())
						entLbsUpdateStatement.close();
					if (entPostUpdateStatement != null && !entPostUpdateStatement.isClosed())
						entPostUpdateStatement.close();
					if (entPostStatusUpdateStatement != null && !entPostStatusUpdateStatement.isClosed())
						entPostStatusUpdateStatement.close();
					if (entAbilityRequireUpdateStatement != null && !entAbilityRequireUpdateStatement.isClosed())
						entAbilityRequireUpdateStatement.close();
					if (viewEntPostUpdateStatement != null && !viewEntPostUpdateStatement.isClosed())
						viewEntPostUpdateStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static boolean saveEnterprise(List<Enterprise> list, Integer updateInterval) {
		if (updateInterval == null)
			updateInterval = 3;
		String lbsUpdateSQL = "update zcdh_ent_lbs set longitude=?, latitude=? where lbs_id=?";
		String enterpriseUpdateSQL = "update zcdh_ent_enterprise set ent_name=?, industry=?, property=?, employ_num=?, introduction=?, ent_web=?, parea=?, address=?, lbs_id=?, data_src=?, data_url=?, create_date=? where ent_id=?";
		String lbsInsertSQL = "insert into zcdh_ent_lbs(longitude, latitude) values(?, ?)";
		String enterpriseInsertSQL = "insert into zcdh_ent_enterprise(ent_name, industry, property, employ_num, introduction, ent_web, parea, address, lbs_id, data_src, data_url, create_date) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String accountInsertSQL = "insert into zcdh_ent_account(ent_id, account, pwd, status, create_mode, create_date) values(?, ?, ?, ?, ?, ?)";
		Connection connection = openConnection();
		PreparedStatement lbsUpdateStatement = null;
		PreparedStatement enterpriseUpdateStatement = null;
		PreparedStatement lbsInsertStatement = null;
		PreparedStatement enterpriseInsertStatement = null;
		PreparedStatement accountInsertStatement = null;
		ResultSet lbsInsertedKeyResultSet = null;
		ResultSet enterpriseInsertedKeyResultSet = null;
		ResultSet accountInsertedKeyResultSet = null;
		List<Lbs> updatedLbses = new ArrayList<Lbs>();
		List<Enterprise> updatedEnterprises = new ArrayList<Enterprise>();
		List<Lbs> insertedLbses = new ArrayList<Lbs>();
		List<Enterprise> insertedEnterprises = new ArrayList<Enterprise>();
		List<Account> insertedAccounts = new ArrayList<Account>();
		try {
			lbsUpdateStatement = connection.prepareStatement(lbsUpdateSQL);
			enterpriseUpdateStatement = connection.prepareStatement(enterpriseUpdateSQL);
			lbsInsertStatement = connection.prepareStatement(lbsInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			enterpriseInsertStatement = connection.prepareStatement(enterpriseInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			accountInsertStatement = connection.prepareStatement(accountInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			for (Enterprise ent : list) {

				Lbs lbs = ent.getLbs();
				if (lbs.getId() != null) {
					if (lbs.getDirty()) {
						lbsUpdateStatement.setDouble(1, lbs.getLon());
						lbsUpdateStatement.setDouble(2, lbs.getLat());
						lbsUpdateStatement.setLong(3, lbs.getId());
						lbsUpdateStatement.addBatch();

						lbs.setDirty(false);
						updatedLbses.add(lbs);
					}
				} else {
					lbsInsertStatement.setDouble(1, lbs.getLon());
					lbsInsertStatement.setDouble(2, lbs.getLat());
					lbsInsertStatement.addBatch();

					insertedLbses.add(lbs);
				}

				ent.setStatus(1);
				if (ent.getId() != null) {
					if (ent.getDirty()) {
						updatedEnterprises.add(ent);
					}
				} else {
					insertedEnterprises.add(ent);
					insertedAccounts.add(ent.getAccount());
				}
			}

			lbsUpdateStatement.executeBatch();

			lbsInsertStatement.executeBatch();
			lbsInsertedKeyResultSet = lbsInsertStatement.getGeneratedKeys();
			for (int i = 0; lbsInsertedKeyResultSet.next(); i++)
				insertedLbses.get(i).setId(lbsInsertedKeyResultSet.getLong(1));

			for (Enterprise ent : updatedEnterprises) {
				enterpriseUpdateStatement.setString(1, ent.getName());
				enterpriseUpdateStatement.setString(2, ent.getCategoryCode());
				enterpriseUpdateStatement.setString(3, ent.getNatureCode());
				enterpriseUpdateStatement.setString(4, ent.getScaleCode());
				enterpriseUpdateStatement.setString(5, ent.getIntroduction());
				enterpriseUpdateStatement.setString(6, ent.getWebsite());
				enterpriseUpdateStatement.setString(7, ent.getAreaCode());
				enterpriseUpdateStatement.setString(8, ent.getAddress());
				enterpriseUpdateStatement.setLong(9, ent.getLbs().getId());
				enterpriseUpdateStatement.setString(10, ent.getDataSrc());
				enterpriseUpdateStatement.setString(11, ent.getDataUrl());
				enterpriseUpdateStatement.setDate(12, new Date(ent.getCreateDate().getTime()));
				enterpriseUpdateStatement.setLong(13, ent.getId());
				enterpriseUpdateStatement.addBatch();

				ent.setDirty(false);
				ent.setStatus(3);
			}
			enterpriseUpdateStatement.executeBatch();

			for (Enterprise ent : insertedEnterprises) {
				enterpriseInsertStatement.setString(1, ent.getName());
				enterpriseInsertStatement.setString(2, ent.getCategoryCode());
				enterpriseInsertStatement.setString(3, ent.getNatureCode());
				enterpriseInsertStatement.setString(4, ent.getScaleCode());
				enterpriseInsertStatement.setString(5, ent.getIntroduction());
				enterpriseInsertStatement.setString(6, ent.getWebsite());
				enterpriseInsertStatement.setString(7, ent.getAreaCode());
				enterpriseInsertStatement.setString(8, ent.getAddress());
				enterpriseInsertStatement.setLong(9, ent.getLbs().getId());
				enterpriseInsertStatement.setString(10, ent.getDataSrc());
				enterpriseInsertStatement.setString(11, ent.getDataUrl());
				enterpriseInsertStatement.setDate(12, new Date(ent.getCreateDate().getTime()));
				enterpriseInsertStatement.addBatch();

				ent.setStatus(2);
			}
			enterpriseInsertStatement.executeBatch();

			enterpriseInsertedKeyResultSet = enterpriseInsertStatement.getGeneratedKeys();
			for (int i = 0; enterpriseInsertedKeyResultSet.next(); i++) {
				Enterprise ent = insertedEnterprises.get(i);
				ent.setId(enterpriseInsertedKeyResultSet.getLong(1));
				enterprises.put(ent.getDataUrl(), ent);
				enterpriseNameMap.put(ent.getName(), ent);

				ent.getAccount().setEnterpriseId(ent.getId());
			}

			for (Account account : insertedAccounts) {
				accountInsertStatement.setLong(1, account.getEnterpriseId());
				accountInsertStatement.setString(2, account.getAccount());
				accountInsertStatement.setString(3, "oSBrriGEzW8KHAL6b9J63w==");// zcdhjob.com
				accountInsertStatement.setInt(4, 1);
				accountInsertStatement.setInt(5, 2);// 0 企业录入, 1 客服录入, 2 自动采集
				accountInsertStatement.setDate(6, new Date(account.getCreateDate().getTime()));
				accountInsertStatement.addBatch();
			}
			accountInsertStatement.executeBatch();

			accountInsertedKeyResultSet = accountInsertStatement.getGeneratedKeys();
			for (int i = 0; accountInsertedKeyResultSet.next(); i++)
				insertedAccounts.get(i).setId(accountInsertedKeyResultSet.getLong(1));
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (lbsUpdateStatement != null && !lbsUpdateStatement.isClosed())
					lbsUpdateStatement.close();
				if (enterpriseUpdateStatement != null && !enterpriseUpdateStatement.isClosed())
					enterpriseUpdateStatement.close();
				if (lbsInsertStatement != null && !lbsInsertStatement.isClosed())
					lbsInsertStatement.close();
				if (enterpriseInsertStatement != null && !enterpriseInsertStatement.isClosed())
					enterpriseInsertStatement.close();
				if (accountInsertStatement != null && !accountInsertStatement.isClosed())
					accountInsertStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean saveEnterprise(Enterprise ent) {
		String lbsUpdateSQL = "update zcdh_ent_lbs set longitude=?, latitude=? where lbs_id=?";
		String enterpriseUpdateSQL = "update zcdh_ent_enterprise set ent_name=?, industry=?, property=?, employ_num=?, introduction=?, ent_web=?, parea=?, address=?, lbs_id=?, data_src=?, data_url=?, create_date=? where ent_id=?";
		String lbsInsertSQL = "insert into zcdh_ent_lbs(longitude, latitude) values(?, ?)";
		String enterpriseInsertSQL = "insert into zcdh_ent_enterprise(ent_name, industry, property, employ_num, introduction, ent_web, parea, address, lbs_id, data_src, data_url, create_date) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String accountInsertSQL = "insert into zcdh_ent_account(ent_id, account, pwd, status, create_mode, create_date) values(?, ?, ?, ?, ?, ?)";
		Connection connection = openConnection();
		PreparedStatement lbsUpdateStatement = null;
		PreparedStatement enterpriseUpdateStatement = null;
		PreparedStatement lbsInsertStatement = null;
		PreparedStatement enterpriseInsertStatement = null;
		PreparedStatement accountInsertStatement = null;
		ResultSet lbsInsertedKeyResultSet = null;
		ResultSet enterpriseInsertedKeyResultSet = null;
		ResultSet accountInsertedKeyResultSet = null;

		try {
			lbsUpdateStatement = connection.prepareStatement(lbsUpdateSQL);
			enterpriseUpdateStatement = connection.prepareStatement(enterpriseUpdateSQL);
			lbsInsertStatement = connection.prepareStatement(lbsInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			enterpriseInsertStatement = connection.prepareStatement(enterpriseInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			accountInsertStatement = connection.prepareStatement(accountInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);

			Lbs lbs = ent.getLbs();
			if (lbs.getId() != null) {
				if (lbs.getDirty()) {
					lbsUpdateStatement.setDouble(1, lbs.getLon());
					lbsUpdateStatement.setDouble(2, lbs.getLat());
					lbsUpdateStatement.setLong(3, lbs.getId());
					lbsUpdateStatement.executeUpdate();

					lbs.setDirty(false);
				}
			} else {
				lbsInsertStatement.setDouble(1, lbs.getLon());
				lbsInsertStatement.setDouble(2, lbs.getLat());
				lbsInsertStatement.executeUpdate();

				lbsInsertedKeyResultSet = lbsInsertStatement.getGeneratedKeys();
				if (lbsInsertedKeyResultSet.next())
					lbs.setId(lbsInsertedKeyResultSet.getLong(1));
			}

			ent.setStatus(1);

			if (ent.getId() != null) {
				if (ent.getDirty()) {
					enterpriseUpdateStatement.setString(1, ent.getName());
					enterpriseUpdateStatement.setString(2, ent.getCategoryCode());
					enterpriseUpdateStatement.setString(3, ent.getNatureCode());
					enterpriseUpdateStatement.setString(4, ent.getScaleCode());
					enterpriseUpdateStatement.setString(5, ent.getIntroduction());
					enterpriseUpdateStatement.setString(6, ent.getWebsite());
					enterpriseUpdateStatement.setString(7, ent.getAreaCode());
					enterpriseUpdateStatement.setString(8, ent.getAddress());
					enterpriseUpdateStatement.setLong(9, ent.getLbs().getId());
					enterpriseUpdateStatement.setString(10, ent.getDataSrc());
					enterpriseUpdateStatement.setString(11, ent.getDataUrl());
					enterpriseUpdateStatement.setDate(12, new Date(ent.getCreateDate().getTime()));
					enterpriseUpdateStatement.setLong(13, ent.getId());
					enterpriseUpdateStatement.executeUpdate();

					ent.setDirty(false);
					ent.setStatus(3);
				}
			} else {
				enterpriseInsertStatement.setString(1, ent.getName());
				enterpriseInsertStatement.setString(2, ent.getCategoryCode());
				enterpriseInsertStatement.setString(3, ent.getNatureCode());
				enterpriseInsertStatement.setString(4, ent.getScaleCode());
				enterpriseInsertStatement.setString(5, ent.getIntroduction());
				enterpriseInsertStatement.setString(6, ent.getWebsite());
				enterpriseInsertStatement.setString(7, ent.getAreaCode());
				enterpriseInsertStatement.setString(8, ent.getAddress());
				enterpriseInsertStatement.setLong(9, ent.getLbs().getId());
				enterpriseInsertStatement.setString(10, ent.getDataSrc());
				enterpriseInsertStatement.setString(11, ent.getDataUrl());
				enterpriseInsertStatement.setDate(12, new Date(ent.getCreateDate().getTime()));
				enterpriseInsertStatement.executeUpdate();

				ent.setStatus(2);

				enterpriseInsertedKeyResultSet = enterpriseInsertStatement.getGeneratedKeys();
				if (enterpriseInsertedKeyResultSet.next()) {
					ent.setId(enterpriseInsertedKeyResultSet.getLong(1));
					enterprises.put(ent.getDataUrl(), ent);
					enterpriseNameMap.put(ent.getName(), ent);
					ent.getAccount().setEnterpriseId(ent.getId());
				}

				Account account = ent.getAccount();

				accountInsertStatement.setLong(1, account.getEnterpriseId());
				accountInsertStatement.setString(2, account.getAccount());
				accountInsertStatement.setString(3, "oSBrriGEzW8KHAL6b9J63w==");// zcdhjob.com
				accountInsertStatement.setInt(4, 1);
				accountInsertStatement.setInt(5, 2);// 0 企业录入, 1 客服录入, 2 自动采集
				accountInsertStatement.setDate(6, new Date(account.getCreateDate().getTime()));
				accountInsertStatement.executeUpdate();

				accountInsertedKeyResultSet = accountInsertStatement.getGeneratedKeys();
				if (accountInsertedKeyResultSet.next())
					account.setId(accountInsertedKeyResultSet.getLong(1));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (lbsUpdateStatement != null && !lbsUpdateStatement.isClosed())
					lbsUpdateStatement.close();
				if (enterpriseUpdateStatement != null && !enterpriseUpdateStatement.isClosed())
					enterpriseUpdateStatement.close();
				if (lbsInsertStatement != null && !lbsInsertStatement.isClosed())
					lbsInsertStatement.close();
				if (enterpriseInsertStatement != null && !enterpriseInsertStatement.isClosed())
					enterpriseInsertStatement.close();
				if (accountInsertStatement != null && !accountInsertStatement.isClosed())
					accountInsertStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static Enterprise mergeEnterprise(Enterprise ent) {
		Enterprise enterprise = enterpriseNameMap.get(ent.getName());
		if (enterprise != null) {

			ent.setId(enterprise.getId());

			ent.setAccount(enterprise.getAccount());

			ent.setPosts(enterprise.getPosts());

			if (Ver.isBlank(enterprise.getCategory()))
				ent.setDirty(true);
			else
				ent.setCategory(enterprise.getCategory());

			if (Ver.isBlank(enterprise.getCategoryCode()))
				ent.setDirty(true);
			else
				ent.setCategoryCode(enterprise.getCategoryCode());

			if (Ver.isBlank(enterprise.getNature()))
				ent.setDirty(true);
			else
				ent.setNature(enterprise.getNature());

			if (Ver.isBlank(enterprise.getNatureCode()))
				ent.setDirty(true);
			else
				ent.setNatureCode(enterprise.getNatureCode());

			if (Ver.isBlank(enterprise.getScale()))
				ent.setDirty(true);
			else
				ent.setScale(enterprise.getScale());

			if (Ver.isBlank(enterprise.getScaleCode()))
				ent.setDirty(true);
			else
				ent.setScaleCode(enterprise.getScaleCode());

			if (Ver.isBlank(enterprise.getIntroduction()) && Ver.isNotBlank(ent.getIntroduction()))
				ent.setDirty(true);
			else
				ent.setIntroduction(enterprise.getIntroduction());

			if (Ver.isBlank(enterprise.getWebsite()) && Ver.isNotBlank(ent.getWebsite()))
				ent.setDirty(true);
			else
				ent.setWebsite(enterprise.getWebsite());

			if (Ver.isBlank(enterprise.getAreaCode()))
				ent.setDirty(true);
			else
				ent.setAreaCode(enterprise.getAreaCode());

			if (Ver.isBlank(enterprise.getAddress()))
				ent.setDirty(true);
			else
				ent.setAddress(enterprise.getAddress());

			if (enterprise.getLbs() == null)
				ent.setDirty(true);
			else
				ent.setLbs(enterprise.getLbs());

			if (enterprise.getCreateDate() == null)
				ent.setDirty(true);
			else
				ent.setCreateDate(enterprise.getCreateDate());
		}
		return enterprise;
	}

	public static Post mergePost(Post p) {
		Enterprise enterprise = enterpriseNameMap.get(p.getEnterpriseName());
		if (enterprise != null) {
			Post post = enterprise.getPosts().get(p.getName());
			if (post != null) {

				p.setId(post.getId());

				if (Ver.isBlank(post.getCategory()))
					p.setDirty(true);
				else
					p.setCategory(post.getCategory());

				if (Ver.isBlank(post.getCategoryCode()))
					p.setDirty(true);
				else
					p.setCategoryCode(post.getCategoryCode());

				if (post.getNumber() == null)
					p.setDirty(true);
				else
					p.setNumber(post.getNumber());

				if (Ver.isBlank(post.getNumberText()))
					p.setDirty(true);
				else
					p.setNumberText(post.getNumberText());

				if (post.getIsSeveral() == null)
					p.setDirty(true);
				else
					p.setIsSeveral(post.getIsSeveral());

				if (Ver.isBlank(post.getNature()))
					p.setDirty(true);
				else
					p.setNature(post.getNature());

				if (Ver.isBlank(post.getNatureCode()))
					p.setDirty(true);
				else
					p.setNatureCode(post.getNatureCode());

				if (Ver.isBlank(post.getSalary()))
					p.setDirty(true);
				else
					p.setSalary(post.getSalary());

				if (Ver.isBlank(post.getSalaryText()))
					p.setDirty(true);
				else
					p.setSalaryText(post.getSalaryText());

				if (post.getSalaryType() == null)
					p.setDirty(true);
				else
					p.setSalaryType(post.getSalaryType());

				if (Ver.isBlank(post.getExperience()))
					p.setDirty(true);
				else
					p.setExperience(post.getExperience());

				if (Ver.isBlank(post.getExperienceCode()))
					p.setDirty(true);
				else
					p.setExperienceCode(post.getExperienceCode());

				if (post.getExperienceAbility() == null)
					p.setDirty(true);
				else
					p.setExperienceAbility(post.getExperienceAbility());

				if (Ver.isBlank(post.getEducation()))
					p.setDirty(true);
				else
					p.setEducation(post.getEducation());

				if (Ver.isBlank(post.getEducationCode()))
					p.setDirty(true);
				else
					p.setEducationCode(post.getEducationCode());

				if (post.getEducationAbility() == null)
					p.setDirty(true);
				else
					p.setEducationAbility(post.getEducationAbility());

				if (Ver.isBlank(post.getWelfare()))
					p.setDirty(true);
				else
					p.setWelfare(post.getWelfare());

				if (Ver.isBlank(post.getWelfareCode()))
					p.setDirty(true);
				else
					p.setWelfareCode(post.getWelfareCode());

				if (Ver.isBlank(post.getIntroduction()) && Ver.isNotBlank(p.getIntroduction()))
					p.setDirty(true);
				else
					p.setIntroduction(post.getIntroduction());

				if (Ver.isBlank(post.getAreaCode()))
					p.setDirty(true);
				else
					p.setAreaCode(post.getAreaCode());

				if (Ver.isBlank(post.getAddress()))
					p.setDirty(true);
				else
					p.setAddress(post.getAddress());

				if (post.getLbs() == null)
					p.setDirty(true);
				else
					p.setLbs(post.getLbs());

				if (post.getUpdateDate() == null)
					p.setDirty(true);
				else
					p.setUpdateDate(post.getUpdateDate());

				if (post.getPublishDate() == null)
					p.setDirty(true);
				else
					p.setPublishDate(post.getPublishDate());
			}
		}
		return p;
	}

	private static String md5Encoder(String str) {
		try {
			return Base64.encodeBase64String(messageDigest.digest(str.getBytes("utf-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
