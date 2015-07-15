package crawler.post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.log4j.Logger;

import crawler.post.model.Enterprise;
import crawler.post.model.Post;
import dao.data.C3P0Store;

public class Holder extends C3P0Store {

	private static Logger logger = Logger.getLogger(Holder.class);

	private static Map<String, String> tagCodes = new ConcurrentHashMap<String, String>();
	private static Map<String, String> areaCodes = new ConcurrentHashMap<String, String>();

	private static Map<String, Map<String, String>> postCategories = new ConcurrentHashMap<String, Map<String, String>>();
	private static Map<String, String> postNatures = new ConcurrentHashMap<String, String>();
	private static Map<String, Map<String, String>> postExperiences = new ConcurrentHashMap<String, Map<String, String>>();
	private static Map<String, Map<String, String>> postEducations = new ConcurrentHashMap<String, Map<String, String>>();

	private static Map<String, String> enterpriseCategories = new ConcurrentHashMap<String, String>();
	private static Map<String, String> enterpriseNatures = new ConcurrentHashMap<String, String>();
	private static Map<String, String> enterpriseScales = new ConcurrentHashMap<String, String>();

	private static Map<String, Post> posts = new ConcurrentHashMap<String, Post>();
	private static Map<String, Enterprise> enterprises = new ConcurrentHashMap<String, Enterprise>();
	private static Set<String> enterpriseAccounts = new ConcurrentSkipListSet<String>();

	public void init() {
		logger.info("------ init Holder(Post) ------");
		selectResultSet("select tag_name, tag_code from zcdh_tag where is_delete=1 or is_delete is null", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int i) throws Exception {
				tagCodes.put(resultSet.getString("tag_name"), resultSet.getString("tag_code"));
				return true;
			}
		});

		selectResultSet("select area_name, area_code from zcdh_area where (is_delete=1 OR is_delete) and area_code regexp '^[0-9]{3}\\.[0-9]{3}$' and area_name not regexp '行政'", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int index) throws Exception {
				areaCodes.put(resultSet.getString("area_name").replaceAll("\\s+|市$|盟$|地区$|族$|自治州$|族自治州$", ""), resultSet.getString("area_code"));
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
					Map<String, String> experience = new HashMap<String, String>();
					experience.put("paramCode", resultSet.getString("param_code"));
					experience.put("paramName", resultSet.getString("param_name"));
					experience.put("paramValue", resultSet.getString("param_value"));
					experience.put("technicalCode", resultSet.getString("technical_code"));
					experience.put("techonlogyGategoryCode", resultSet.getString("techonlogy_gategory_code"));
					experience.put("matchType", resultSet.getString("match_type"));
					experience.put("percent", resultSet.getString("percent"));
					postExperiences.put(resultSet.getString("param_code"), experience);
				} else if ("004".equals(category)) {
					Map<String, String> education = new HashMap<String, String>();
					education.put("paramCode", resultSet.getString("param_code"));
					education.put("paramName", resultSet.getString("param_name"));
					education.put("paramValue", resultSet.getString("param_value"));
					education.put("technicalCode", resultSet.getString("technical_code"));
					education.put("techonlogyGategoryCode", resultSet.getString("techonlogy_gategory_code"));
					education.put("matchType", resultSet.getString("match_type"));
					education.put("percent", resultSet.getString("percent"));
					postEducations.put(resultSet.getString("param_code"), education);
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

		selectResultSet("select id, update_date, data_url from zcdh_ent_post where data_src is not null and update_date is not null", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int i) throws Exception {
				Post post = new Post();
				post.setId(resultSet.getLong("id"));
				post.setDate(resultSet.getDate("update_date"));
				posts.put(resultSet.getString("data_url"), post);
				return true;
			}
		});

		selectResultSet("select e.ent_id, e.parea, l.latitude lbs_lat, l.longitude lbs_lon, e.create_date, e.data_url from zcdh_ent_enterprise e left join zcdh_ent_lbs l on e.lbs_id=l.lbs_id where e.data_src is not null and e.create_date is not null and e.lbs_id is not null", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int i) throws Exception {
				Enterprise enterprise = new Enterprise();
				enterprise.setId(resultSet.getLong("ent_id"));
				enterprise.setAreaCode(resultSet.getString("parea"));
				enterprise.setLbsLat(resultSet.getDouble("lbs_lat"));
				enterprise.setLbsLon(resultSet.getDouble("lbs_lon"));
				enterprise.setDate(resultSet.getDate("create_date"));
				enterprises.put(resultSet.getString("data_url"), enterprise);
				return true;
			}
		});

		selectResultSet("select e.ent_name from zcdh_ent_account a join zcdh_ent_enterprise e on a.ent_id=e.ent_id where e.ent_name is not null", new Iterator<ResultSet>() {
			public boolean next(ResultSet resultSet, int i) throws Exception {
				enterpriseAccounts.add(resultSet.getString("ent_name"));
				return true;
			}
		});
		logger.info("-------------------------------");
	}

	public static String getTagCode(String tagName) {
		return tagCodes.get(tagName);
	}

	public static String getAreaCode(String address) {
		for (String areaName : areaCodes.keySet())
			if (address.contains(areaName))
				return areaCodes.get(areaName);
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

	public static Map<String, Map<String, String>> getPostExperiences() {
		return postExperiences;
	}

	public static Map<String, String> getPostExperience(String paramName) {
		return postExperiences.get(paramName);
	}

	public static Map<String, Map<String, String>> getPostEducations() {
		return postEducations;
	}

	public static Map<String, String> getPostEducation(String paramName) {
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

	public static Boolean existEnterpriseAccount(String enterpriseName) {
		return enterpriseAccounts.contains(enterpriseName);
	}

	public static void savePost(List<Post> list, Integer updateInterval) {
		if (updateInterval == null)
			updateInterval = 3;
		String entPostUpdateSQL = "update zcdh_ent_post set update_date=? where id=?";
		String entLbsInsertSQL = "insert into zcdh_ent_lbs(longitude, latitude) values(?, ?)";
		String entPostInsertSQL = "insert into zcdh_ent_post(publish_date, update_date, ent_id, post_aliases, post_name, post_code, pjob_category, headcounts, is_several, psalary, salary_type, tag_selected, post_address, parea, lbs_id, post_remark, data_src, data_url) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String entPostStatusInsertSQL = "insert into zcdh_ent_post_status(post_id, post_status, employ, employed, employ_total, un_employ, skim_count) values(?, ?, ?, ?, ?, ?, ?)";
		String entPromotionInsertSQL = "insert into zcdh_ent_promotion(ent_post_id, ent_id, promotion_value) values(?, ?, ?)";
		String entAbilityRequireInsertSQL = "insert into zcdh_ent_ability_require(post_id, ent_id, post_code, param_code, grade, match_type, technology_code, technology_cate_code, total_point, weight_point) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String viewEntPostInsertSQL = "insert into zcdh_view_ent_post(post_id, ent_name, industry, property, employ_num, post_aliases, post_code, salary_code, min_salary, max_salary, salary_type, post_property_code, publish_date) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Connection connection = openConnection();
		PreparedStatement entPostUpdateStatement = null;
		PreparedStatement entLbsInsertStatement = null;
		PreparedStatement entPostInsertStatement = null;
		PreparedStatement entPostStatusInsertStatement = null;
		PreparedStatement entPromotionInsertStatement = null;
		PreparedStatement entAbilityRequireInsertStatement = null;
		PreparedStatement viewEntPostInsertStatement = null;
		ResultSet entLbsInsertedKeyResultSet = null;
		ResultSet entPostInsertedKeyResultSet = null;
		List<Post> updatedPosts = new ArrayList<Post>();
		List<Post> insertedPosts = new ArrayList<Post>();
		try {
			entPostUpdateStatement = connection.prepareStatement(entPostUpdateSQL);
			entLbsInsertStatement = connection.prepareStatement(entLbsInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			entPostInsertStatement = connection.prepareStatement(entPostInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			entPostStatusInsertStatement = connection.prepareStatement(entPostStatusInsertSQL);
			entPromotionInsertStatement = connection.prepareStatement(entPromotionInsertSQL);
			entAbilityRequireInsertStatement = connection.prepareStatement(entAbilityRequireInsertSQL);
			viewEntPostInsertStatement = connection.prepareStatement(viewEntPostInsertSQL);
			for (Post p : list) {
				p.setStatus(1);
				String url = p.getUrl();
				if (posts.containsKey(url)) {
					Post post = posts.get(url);
					p.setId(post.getId());
					if (p.getDate() != null && post.getDate() != null && p.getDate().getTime() - post.getDate().getTime() >= updateInterval * 24 * 60 * 60 * 1000) {
						entPostUpdateStatement.setDate(1, new java.sql.Date(p.getDate().getTime()));
						entPostUpdateStatement.setLong(2, p.getId());
						entPostUpdateStatement.addBatch();
						updatedPosts.add(p);
					}
				} else {
					entLbsInsertStatement.setDouble(1, p.getLbsLon());
					entLbsInsertStatement.setDouble(2, p.getLbsLat());
					entLbsInsertStatement.addBatch();
					insertedPosts.add(p);
				}

			}

			entPostUpdateStatement.executeBatch();
			for (Post p : updatedPosts)
				p.setStatus(3);

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

	public static void saveEnterprise(List<Enterprise> list, Integer updateInterval) {
		if (updateInterval == null)
			updateInterval = 3;
		String entEnterpriseUpdateSQL = "update zcdh_ent_enterprise set create_date=? where ent_id=?";
		String entLbsInsertSQL = "insert into zcdh_ent_lbs(longitude, latitude) values(?, ?)";
		String entEnterpriseInsertSQL = "insert into zcdh_ent_enterprise(create_date, ent_name, industry, property, employ_num, ent_web, address, parea, lbs_id, introduction, data_src, data_url) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String entAccountInsertSQL = "insert into zcdh_ent_account(ent_id, account, pwd, status, create_date, create_mode) values(?, ?, ?, ?, ?, ?)";
		Connection connection = openConnection();
		PreparedStatement entEnterpriseUpdateStatement = null;
		PreparedStatement entLbsInsertStatement = null;
		PreparedStatement entEnterpriseInsertStatement = null;
		PreparedStatement entAccountInsertStatement = null;
		ResultSet entLbsInsertedKeyResultSet = null;
		ResultSet entEnterpriseInsertedKeyResultSet = null;
		List<Enterprise> updatedEnterprises = new ArrayList<Enterprise>();
		List<Enterprise> insertedEnterprises = new ArrayList<Enterprise>();
		try {
			entEnterpriseUpdateStatement = connection.prepareStatement(entEnterpriseUpdateSQL);
			entLbsInsertStatement = connection.prepareStatement(entLbsInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			entEnterpriseInsertStatement = connection.prepareStatement(entEnterpriseInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
			entAccountInsertStatement = connection.prepareStatement(entAccountInsertSQL);
			for (Enterprise ent : list) {
				ent.setStatus(1);
				String url = ent.getUrl();
				if (enterprises.containsKey(url)) {
					Enterprise enterprise = enterprises.get(url);
					ent.setId(enterprise.getId());
					if (ent.getDate() != null && enterprise.getDate() != null && ent.getDate().getTime() - enterprise.getDate().getTime() >= updateInterval * 24 * 60 * 60 * 1000) {
						entEnterpriseUpdateStatement.setDate(1, new java.sql.Date(ent.getDate().getTime()));
						entEnterpriseUpdateStatement.setLong(2, ent.getId());
						entEnterpriseUpdateStatement.addBatch();
						updatedEnterprises.add(ent);
					}
				} else {
					entLbsInsertStatement.setDouble(1, ent.getLbsLon());
					entLbsInsertStatement.setDouble(2, ent.getLbsLat());
					entLbsInsertStatement.addBatch();
					insertedEnterprises.add(ent);
				}
			}

			entEnterpriseUpdateStatement.executeBatch();
			for (Enterprise ent : updatedEnterprises)
				ent.setStatus(3);

			entLbsInsertStatement.executeBatch();
			entLbsInsertedKeyResultSet = entLbsInsertStatement.getGeneratedKeys();
			for (int i = 0; entLbsInsertedKeyResultSet.next(); i++) {
				Enterprise ent = insertedEnterprises.get(i);
				ent.setLbsId(entLbsInsertedKeyResultSet.getLong(1));

				entEnterpriseInsertStatement.setDate(1, new java.sql.Date(ent.getDate().getTime()));
				entEnterpriseInsertStatement.setString(2, ent.getName());
				entEnterpriseInsertStatement.setString(3, ent.getCategoryCode());
				entEnterpriseInsertStatement.setString(4, ent.getNatureCode());
				entEnterpriseInsertStatement.setString(5, ent.getScaleCode());
				entEnterpriseInsertStatement.setString(6, ent.getWebsite());
				entEnterpriseInsertStatement.setString(7, ent.getAddress());
				entEnterpriseInsertStatement.setString(8, ent.getAreaCode());
				entEnterpriseInsertStatement.setLong(9, ent.getLbsId());
				entEnterpriseInsertStatement.setString(10, ent.getIntroduction());
				entEnterpriseInsertStatement.setString(11, ent.getSrc());
				entEnterpriseInsertStatement.setString(12, ent.getUrl());
				entEnterpriseInsertStatement.addBatch();
			}
			entEnterpriseInsertStatement.executeBatch();
			entEnterpriseInsertedKeyResultSet = entEnterpriseInsertStatement.getGeneratedKeys();
			for (int i = 0; entEnterpriseInsertedKeyResultSet.next(); i++) {
				Enterprise ent = insertedEnterprises.get(i);
				ent.setId(entEnterpriseInsertedKeyResultSet.getLong(1));
				ent.setStatus(2);
				enterprises.put(ent.getUrl(), ent);

				entAccountInsertStatement.setLong(1, ent.getId());
				entAccountInsertStatement.setString(2, "zcdh0000000");
				entAccountInsertStatement.setString(3, "pwd");
				entAccountInsertStatement.setInt(4, 1);
				entAccountInsertStatement.setDate(5, new java.sql.Date(ent.getDate().getTime()));
				entAccountInsertStatement.setInt(6, 2);
				entAccountInsertStatement.addBatch();
			}
			entAccountInsertStatement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (entLbsInsertedKeyResultSet != null && !entLbsInsertedKeyResultSet.isClosed())
					entLbsInsertedKeyResultSet.close();
				if (entEnterpriseInsertedKeyResultSet != null && !entEnterpriseInsertedKeyResultSet.isClosed())
					entEnterpriseInsertedKeyResultSet.close();
				if (entLbsInsertStatement != null && !entLbsInsertStatement.isClosed())
					entLbsInsertStatement.close();
				if (entEnterpriseUpdateStatement != null && !entEnterpriseUpdateStatement.isClosed())
					entEnterpriseUpdateStatement.close();
				if (entEnterpriseInsertStatement != null && !entEnterpriseInsertStatement.isClosed())
					entEnterpriseInsertStatement.close();
				if (entAccountInsertStatement != null && !entAccountInsertStatement.isClosed())
					entAccountInsertStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static Boolean saveEnterprise(Enterprise enterprise) {
		enterprise.setStatus(1);
		if (enterprise.getId() == null) {
			String entLbsInsertSQL = "insert into zcdh_ent_lbs(longitude, latitude) values(?, ?)";
			String entEnterpriseInsertSQL = "insert into zcdh_ent_enterprise(create_date, ent_name, industry, property, employ_num, ent_web, address, parea, lbs_id, introduction, data_src, data_url) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			Connection connection = openConnection();
			PreparedStatement entLbsInsertStatement = null;
			PreparedStatement entEnterpriseInsertStatement = null;
			ResultSet entLbsInsertedKeyResultSet = null;
			ResultSet entEnterpriseInsertedKeyResultSet = null;
			try {
				entLbsInsertStatement = connection.prepareStatement(entLbsInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
				entEnterpriseInsertStatement = connection.prepareStatement(entEnterpriseInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS);

				entLbsInsertStatement.setDouble(1, enterprise.getLbsLon());
				entLbsInsertStatement.setDouble(2, enterprise.getLbsLat());
				entLbsInsertStatement.executeUpdate();

				entLbsInsertedKeyResultSet = entLbsInsertStatement.getGeneratedKeys();
				if (entLbsInsertedKeyResultSet.next()) {
					enterprise.setLbsId(entLbsInsertedKeyResultSet.getLong(1));

					entEnterpriseInsertStatement.setDate(1, new java.sql.Date(enterprise.getDate().getTime()));
					entEnterpriseInsertStatement.setString(2, enterprise.getName());
					entEnterpriseInsertStatement.setString(3, enterprise.getCategoryCode());
					entEnterpriseInsertStatement.setString(4, enterprise.getNatureCode());
					entEnterpriseInsertStatement.setString(5, enterprise.getScaleCode());
					entEnterpriseInsertStatement.setString(6, enterprise.getWebsite());
					entEnterpriseInsertStatement.setString(7, enterprise.getAddress());
					entEnterpriseInsertStatement.setString(8, enterprise.getAreaCode());
					entEnterpriseInsertStatement.setLong(9, enterprise.getLbsId());
					entEnterpriseInsertStatement.setString(10, enterprise.getIntroduction());
					entEnterpriseInsertStatement.setString(11, enterprise.getSrc());
					entEnterpriseInsertStatement.setString(12, enterprise.getUrl());
					entEnterpriseInsertStatement.executeUpdate();

					entEnterpriseInsertedKeyResultSet = entEnterpriseInsertStatement.getGeneratedKeys();
					if (entEnterpriseInsertedKeyResultSet.next()) {
						enterprise.setId(entEnterpriseInsertedKeyResultSet.getLong(1));
						enterprise.setStatus(2);
						enterprises.put(enterprise.getUrl(), enterprise);
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
					if (entEnterpriseInsertedKeyResultSet != null && !entEnterpriseInsertedKeyResultSet.isClosed())
						entEnterpriseInsertedKeyResultSet.close();
					if (entLbsInsertStatement != null && !entLbsInsertStatement.isClosed())
						entLbsInsertStatement.close();
					if (entEnterpriseInsertStatement != null && !entEnterpriseInsertStatement.isClosed())
						entEnterpriseInsertStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			String entLbsUpdateSQL = "update zcdh_ent_lbs set longitude=?, latitude=? where lbs_id=?";
			String entEnterpriseUpdateSQL = "update zcdh_ent_enterprise set ent_name=?, industry=?, property=?, employ_num=?, ent_web=?, address=?, parea=?, introduction=? where ent_id=?";
			Connection connection = openConnection();
			PreparedStatement entLbsUpdateStatement = null;
			PreparedStatement entEnterpriseUpdateStatement = null;
			try {
				entLbsUpdateStatement = connection.prepareStatement(entLbsUpdateSQL);
				entEnterpriseUpdateStatement = connection.prepareStatement(entEnterpriseUpdateSQL);

				entLbsUpdateStatement.setDouble(1, enterprise.getLbsLon());
				entLbsUpdateStatement.setDouble(2, enterprise.getLbsLat());
				entLbsUpdateStatement.setLong(3, enterprise.getLbsId());
				entLbsUpdateStatement.executeUpdate();

				entEnterpriseUpdateStatement.setString(1, enterprise.getName());
				entEnterpriseUpdateStatement.setString(2, enterprise.getCategoryCode());
				entEnterpriseUpdateStatement.setString(3, enterprise.getNatureCode());
				entEnterpriseUpdateStatement.setString(4, enterprise.getScaleCode());
				entEnterpriseUpdateStatement.setString(5, enterprise.getWebsite());
				entEnterpriseUpdateStatement.setString(6, enterprise.getAddress());
				entEnterpriseUpdateStatement.setString(7, enterprise.getAreaCode());
				entEnterpriseUpdateStatement.setString(8, enterprise.getIntroduction());
				entEnterpriseUpdateStatement.setLong(9, enterprise.getId());
				entEnterpriseUpdateStatement.executeUpdate();

				enterprise.setStatus(3);
				enterprises.put(enterprise.getUrl(), enterprise);
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					if (entLbsUpdateStatement != null && !entLbsUpdateStatement.isClosed())
						entLbsUpdateStatement.close();
					if (entEnterpriseUpdateStatement != null && !entEnterpriseUpdateStatement.isClosed())
						entEnterpriseUpdateStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
