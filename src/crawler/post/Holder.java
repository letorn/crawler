package crawler.post;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import model.Area;
import model.Area.AreaType;
import model.CategoryPost;
import model.EntAbilityRequire;
import model.EntAccount;
import model.EntEnterprise;
import model.EntLbs;
import model.EntPost;
import model.EntPostStatus;
import model.EntPromotion;
import model.Industry;
import model.Param;
import model.Tag;
import model.Technology;
import model.TechnologyGategory;
import model.ViewEntPost;

import org.apache.log4j.Logger;

import util.Ver;
import crawler.post.model.Enterprise;
import crawler.post.model.Post;
import dao.AreaDao;
import dao.CategoryPostDao;
import dao.EntAbilityRequireDao;
import dao.EntAccountDao;
import dao.EntEnterpriseDao;
import dao.EntLbsDao;
import dao.EntPostDao;
import dao.EntPostStatusDao;
import dao.EntPromotionDao;
import dao.IndustryDao;
import dao.ParamDao;
import dao.PostDao;
import dao.TagDao;
import dao.TechnologyDao;
import dao.TechnologyGategoryDao;
import dao.ViewEntPostDao;
import dao.data.C3P0Store;

public class Holder extends C3P0Store {

	private static Logger logger = Logger.getLogger(Holder.class);

	private static Map<String, String> areaNameMap = new ConcurrentHashMap<String, String>();

	private static Map<String, String> tags = new ConcurrentHashMap<String, String>();
	private static Map<String, String> tagNameMap = new ConcurrentHashMap<String, String>();

	private static Map<String, Map<String, String>> postCategories = new ConcurrentHashMap<String, Map<String, String>>();
	private static Map<String, String> postNatures = new ConcurrentHashMap<String, String>();
	private static Map<String, String> postExperiences = new ConcurrentHashMap<String, String>();
	private static Map<String, String> postEducations = new ConcurrentHashMap<String, String>();

	private static Map<String, String> enterpriseCategories = new ConcurrentHashMap<String, String>();
	private static Map<String, String> enterpriseNatures = new ConcurrentHashMap<String, String>();
	private static Map<String, String> enterpriseScales = new ConcurrentHashMap<String, String>();

	private static DecimalFormat accountNumFormat = new DecimalFormat("0000000");
	private static int lastAccountNum = -1;

	private static Map<String, Map<String, Post>> postEntNameMap = new ConcurrentHashMap<String, Map<String, Post>>();
	private static Map<String, Enterprise> enterpriseNameMap = new ConcurrentHashMap<String, Enterprise>();

	private static AreaDao areaDao;
	private static CategoryPostDao categoryPostDao;
	private static EntAbilityRequireDao entAbilityRequireDao;
	private static EntAccountDao entAccountDao;
	private static EntEnterpriseDao entEnterpriseDao;
	private static EntLbsDao entLbsDao;
	private static EntPostDao entPostDao;
	private static EntPostStatusDao entPostStatusDao;
	private static IndustryDao industryDao;
	private static ParamDao paramDao;
	private static PostDao postDao;
	private static TagDao tagDao;
	private static TechnologyDao technologyDao;
	private static TechnologyGategoryDao technologyGategoryDao;
	private static ViewEntPostDao viewEntPostDao;
	private static EntPromotionDao entPromotionDao;

	public void init() {

		for (Tag tag : tagDao.findAll()) {
			tags.put(tag.getTagCode(), tag.getTagName());
			tagNameMap.put(tag.getTagName(), tag.getTagCode());
		}

		for (Area area : areaDao.find(AreaType.CITY))
			if (!area.getAreaName().contains("行政"))
				areaNameMap.put(area.getAreaName().replaceAll("\\s+|市$|盟$|地区$|族$|自治州$|族自治州$", ""), area.getAreaCode());

		lastAccountNum = entAccountDao.getLastAutoAccountNum();

		for (model.Post post : postDao.findAll()) {
			CategoryPost categoryPost = categoryPostDao.get(post.getPostCategoryCode());
			if (categoryPost != null) {
				Map<String, String> postCategorie = new HashMap<String, String>();
				postCategorie.put("code", post.getPostCode());
				postCategorie.put("name", post.getPostName());
				postCategorie.put("group", categoryPost.getPostCategoryName());
				postCategories.put(post.getPostCode(), postCategorie);
			}
		}

		for (Param param : paramDao.findAll()) {
			String categoryCode = param.getParamCategoryCode();
			if ("007".equals(categoryCode)) {
				postNatures.put(param.getParamCode(), param.getParamName());
			} else if ("005".equals(categoryCode)) {
				postExperiences.put(param.getParamCode(), param.getParamName());
			} else if ("004".equals(categoryCode)) {
				postEducations.put(param.getParamCode(), param.getParamName());
			} else if ("010".equals(categoryCode)) {
				enterpriseNatures.put(param.getParamCode(), param.getParamName());
			} else if ("011".equals(categoryCode)) {
				enterpriseScales.put(param.getParamCode(), param.getParamName());
			}
		}

		for (Industry industry : industryDao.findAll())
			enterpriseCategories.put(industry.getIndustryCode(), industry.getIndustryName());

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

	public static Map<String, String> getPostExperiences() {
		return postExperiences;
	}

	public static String getPostExperience(String paramName) {
		return postExperiences.get(paramName);
	}

	public static Map<String, String> getPostEducations() {
		return postEducations;
	}

	public static String getPostEducation(String paramName) {
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

	public static boolean savePost(List<Post> list, Integer updateInterval) {
		if (updateInterval == null)
			updateInterval = 3;

		Map<EntLbs, Post> entLbsInsertMap = new HashMap<EntLbs, Post>();
		Map<EntPost, Post> entPostInsertMap = new HashMap<EntPost, Post>();
		Map<EntAbilityRequire, Post> entAbilityRequireInsertMap = new HashMap<EntAbilityRequire, Post>();
		Map<EntPostStatus, Post> entPostStatusInsertMap = new HashMap<EntPostStatus, Post>();
		Map<ViewEntPost, Post> viewEntPostInsertMap = new HashMap<ViewEntPost, Post>();
		Map<EntPromotion, Post> entPromotionInsertMap = new HashMap<EntPromotion, Post>();
		List<EntLbs> entLbsUpdateList = new ArrayList<EntLbs>();
		List<EntPost> entPostUpdateList = new ArrayList<EntPost>();
		List<EntAbilityRequire> entAbilityRequireUpdateList = new ArrayList<EntAbilityRequire>();
		List<EntPostStatus> entPostStatusUpdateList = new ArrayList<EntPostStatus>();
		List<ViewEntPost> viewEntPostUpdateList = new ArrayList<ViewEntPost>();

		for (Post post : list) {
			Enterprise enterprise = enterpriseNameMap.get(post.getEnterpriseName());

			post.setStatus(1);

			if (post.getLbsId() != null) {
				EntLbs entLbs = entLbsDao.get(post.getLbsId());
				if (entLbs != null && (Ver.nq(post.getLbsLon(), entLbs.getLongitude()) || Ver.nq(post.getLbsLat(), entLbs.getLatitude()))) {
					entLbs.setLongitude(post.getLbsLon());
					entLbs.setLatitude(post.getLbsLat());
					entLbsUpdateList.add(entLbs);
				}
			} else {
				EntLbs entLbs = new EntLbs();
				entLbs.setLongitude(post.getLbsLon());
				entLbs.setLatitude(post.getLbsLat());
				entLbsInsertMap.put(entLbs, post);
			}

			if (post.getId() != null) {
				EntPost entPost = entPostDao.get(post.getId());
				if (entPost != null) {

					EntAbilityRequire experienceEntAbilityRequire = entAbilityRequireDao.getExperience(post.getId());
					if (experienceEntAbilityRequire != null) {
						if (Ver.nq(post.getCategoryCode(), experienceEntAbilityRequire.getPostCode()) || Ver.nq(post.getExperienceCode(), experienceEntAbilityRequire.getParamCode())) {
							Param experienceParam = paramDao.get(post.getExperienceCode());
							experienceEntAbilityRequire.setPostCode(post.getCategoryCode());
							experienceEntAbilityRequire.setParamCode(post.getExperienceCode());
							experienceEntAbilityRequire.setGrade(experienceParam.getParamValue());
							experienceEntAbilityRequire.setWeightPoint(experienceEntAbilityRequire.getTotalPoint() / experienceEntAbilityRequire.getGrade());
							entAbilityRequireUpdateList.add(experienceEntAbilityRequire);
						}
					}

					EntAbilityRequire educationEntAbilityRequire = entAbilityRequireDao.getEducation(post.getId());
					if (educationEntAbilityRequire != null) {
						if (Ver.nq(post.getCategoryCode(), educationEntAbilityRequire.getPostCode()) || Ver.nq(post.getExperienceCode(), educationEntAbilityRequire.getParamCode())) {
							Param educationParam = paramDao.get(post.getExperienceCode());
							educationEntAbilityRequire.setPostCode(post.getCategoryCode());
							educationEntAbilityRequire.setParamCode(post.getExperienceCode());
							educationEntAbilityRequire.setGrade(educationParam.getParamValue());
							educationEntAbilityRequire.setWeightPoint(educationEntAbilityRequire.getTotalPoint() / educationEntAbilityRequire.getGrade());
							entAbilityRequireUpdateList.add(educationEntAbilityRequire);
						}
					}

					if (Ver.nq(post.getName(), entPost.getPostAliases()) || Ver.nq(post.getCategory(), entPost.getPostName()) || Ver.nq(post.getCategoryCode(), entPost.getPostCode()) || Ver.nq(post.getNumber(), entPost.getHeadcounts()) || Ver.nq(post.getIsSeveral(), entPost.getIsSeveral()) || Ver.nq(post.getNatureCode(), entPost.getPjobCategory()) || Ver.nq(post.getSalary(), entPost.getPsalary()) || Ver.nq(post.getSalaryType(), entPost.getSalaryType()) || Ver.nq(post.getWelfareCode(), entPost.getTagSelected()) || Ver.nq(post.getIntroduction(), entPost.getPostRemark()) || Ver.nq(post.getAreaCode(), entPost.getParea()) || Ver.nq(post.getAddress(), entPost.getPostAddress()) || Ver.nq(post.getLbsId(), entPost.getLbsId()) || Ver.nq(post.getUpdateDate(), entPost.getUpdateDate())) {
						entPost.setPostAliases(post.getName());
						entPost.setPostName(post.getCategory());
						entPost.setPostCode(post.getCategoryCode());
						entPost.setHeadcounts(post.getNumber());
						entPost.setIsSeveral(post.getIsSeveral());
						entPost.setPjobCategory(post.getNatureCode());
						entPost.setPsalary(post.getSalary());
						entPost.setSalaryType(post.getSalaryType());
						entPost.setTagSelected(post.getWelfareCode());
						entPost.setPostRemark(post.getIntroduction());
						entPost.setParea(post.getAreaCode());
						entPost.setPostAddress(post.getAddress());
						entPost.setLbsId(post.getLbsId());
						entPost.setDataSrc(post.getDataSrc());
						entPost.setDataUrl(post.getDataUrl());
						entPost.setUpdateDate(post.getUpdateDate());
						entPostUpdateList.add(entPost);

						if (Ver.nq(post.getNumber(), entPost.getHeadcounts())) {
							EntPostStatus entPostStatus = entPostStatusDao.get(post.getId());
							if (entPostStatus != null) {
								entPostStatus.setEmployTotal(post.getNumber());
								entPostStatus.setUnemploy(entPostStatus.getEmployTotal() - entPostStatus.getEmployed());
								if (entPostStatus.getUnemploy() < 0)
									entPostStatus.setUnemploy(0);
								entPostStatusUpdateList.add(entPostStatus);
							}
						}

						ViewEntPost viewEntPost = viewEntPostDao.getByPostId(post.getId());
						if (viewEntPost != null) {
							viewEntPost.setPostAliases(post.getName());
							viewEntPost.setPostCode(post.getCategoryCode());
							viewEntPost.setPostPropertyCode(post.getNatureCode());
							viewEntPost.setSalaryCode(post.getSalary());
							Integer minSalary = null;
							Integer maxSalary = null;
							if (post.getSalary() != null && post.getSalary().contains("-")) {
								String[] salaries = post.getSalary().split("-", 2);
								if (salaries[0].matches("^\\d+$"))
									minSalary = Integer.parseInt(salaries[0]);
								if (salaries[1].matches("^\\d+$"))
									maxSalary = Integer.parseInt(salaries[1]);
							}
							viewEntPost.setMinSalary(minSalary);
							viewEntPost.setMaxSalary(maxSalary);
							viewEntPost.setSalaryType(post.getSalaryType());
							viewEntPost.setEntName(enterprise.getName());
							viewEntPost.setIndustry(enterprise.getCategoryCode());
							viewEntPost.setProperty(enterprise.getNatureCode());
							viewEntPost.setEmployNum(enterprise.getScaleCode());
							viewEntPostUpdateList.add(viewEntPost);
						}

						post.setStatus(3);
					}
				}
			} else {
				EntPost entPost = new EntPost();
				entPost.setPostAliases(post.getName());
				entPost.setPostName(post.getCategory());
				entPost.setPostCode(post.getCategoryCode());
				entPost.setHeadcounts(post.getNumber());
				entPost.setIsSeveral(post.getIsSeveral());
				entPost.setPjobCategory(post.getNatureCode());
				entPost.setPsalary(post.getSalary());
				entPost.setSalaryType(post.getSalaryType());
				entPost.setTagSelected(post.getWelfareCode());
				entPost.setPostRemark(post.getIntroduction());
				entPost.setParea(post.getAreaCode());
				entPost.setPostAddress(post.getAddress());
				entPost.setLbsId(post.getLbsId());
				entPost.setDataSrc(post.getDataSrc());
				entPost.setDataUrl(post.getDataUrl());
				entPost.setUpdateDate(post.getUpdateDate());
				entPost.setPublishDate(post.getPublishDate());
				entPostInsertMap.put(entPost, post);

				Param experienceParam = paramDao.get(post.getExperienceCode());
				if (experienceParam != null) {
					Technology experienceTechnology = technologyDao.get(experienceParam.getParamCode());
					if (experienceTechnology != null) {
						TechnologyGategory experienceTechnologyGategory = technologyGategoryDao.get(experienceTechnology.getTechnologyGategoryCode());
						if (experienceTechnologyGategory != null) {
							EntAbilityRequire experienceEntAbilityRequire = new EntAbilityRequire();
							experienceEntAbilityRequire.setPostId(post.getId());
							experienceEntAbilityRequire.setEntId(enterprise.getId());
							experienceEntAbilityRequire.setPostCode(post.getCategoryCode());
							experienceEntAbilityRequire.setParamCode(post.getExperienceCode());
							experienceEntAbilityRequire.setGrade(experienceParam.getParamValue());
							experienceEntAbilityRequire.setMatchType(experienceTechnology.getMatchType());
							experienceEntAbilityRequire.setTechnologyCode(experienceTechnology.getTechnicalCode());
							experienceEntAbilityRequire.setTechnologyCateCode(experienceTechnology.getTechnologyGategoryCode());
							experienceEntAbilityRequire.setTotalPoint(experienceTechnologyGategory.getPercent() / 2.0);
							experienceEntAbilityRequire.setWeightPoint(experienceEntAbilityRequire.getTotalPoint() / experienceEntAbilityRequire.getGrade());
							entAbilityRequireInsertMap.put(experienceEntAbilityRequire, post);
						}
					}
				}

				Param educationParam = paramDao.get(post.getEducationCode());
				if (educationParam != null) {
					Technology educationTechnology = technologyDao.get(educationParam.getParamCode());
					if (educationTechnology != null) {
						TechnologyGategory educationTechnologyGategory = technologyGategoryDao.get(educationTechnology.getTechnologyGategoryCode());
						if (educationTechnologyGategory != null) {
							EntAbilityRequire educationEntAbilityRequire = new EntAbilityRequire();
							educationEntAbilityRequire.setPostId(post.getId());
							educationEntAbilityRequire.setEntId(enterprise.getId());
							educationEntAbilityRequire.setPostCode(post.getCategoryCode());
							educationEntAbilityRequire.setParamCode(post.getExperienceCode());
							educationEntAbilityRequire.setGrade(educationParam.getParamValue());
							educationEntAbilityRequire.setMatchType(educationTechnology.getMatchType());
							educationEntAbilityRequire.setTechnologyCode(educationTechnology.getTechnicalCode());
							educationEntAbilityRequire.setTechnologyCateCode(educationTechnology.getTechnologyGategoryCode());
							educationEntAbilityRequire.setTotalPoint(educationTechnologyGategory.getPercent() / 2.0);
							educationEntAbilityRequire.setWeightPoint(educationEntAbilityRequire.getTotalPoint() / educationEntAbilityRequire.getGrade());
							entAbilityRequireInsertMap.put(educationEntAbilityRequire, post);
						}
					}
				}

				EntPostStatus entPostStatus = new EntPostStatus();
				entPostStatus.setPostId(post.getId());
				entPostStatus.setPostStatus(1);
				entPostStatus.setEmploy(0);
				entPostStatus.setEmployed(0);
				entPostStatus.setEmployTotal(post.getNumber());
				entPostStatus.setUnemploy(post.getNumber());
				entPostStatus.setSkimCount(0);
				entPostStatusInsertMap.put(entPostStatus, post);

				ViewEntPost viewEntPost = new ViewEntPost();
				viewEntPost.setPostId(post.getId());
				viewEntPost.setPostAliases(post.getName());
				viewEntPost.setPostCode(post.getCategoryCode());
				viewEntPost.setPostPropertyCode(post.getNatureCode());
				viewEntPost.setSalaryCode(post.getSalary());
				Integer minSalary = null;
				Integer maxSalary = null;
				if (post.getSalary() != null && post.getSalary().contains("-")) {
					String[] salaries = post.getSalary().split("-", 2);
					if (salaries[0].matches("^\\d+$"))
						minSalary = Integer.parseInt(salaries[0]);
					if (salaries[1].matches("^\\d+$"))
						maxSalary = Integer.parseInt(salaries[1]);
				}
				viewEntPost.setMinSalary(minSalary);
				viewEntPost.setMaxSalary(maxSalary);
				viewEntPost.setSalaryType(post.getSalaryType());
				viewEntPost.setEntName(enterprise.getName());
				viewEntPost.setIndustry(enterprise.getCategoryCode());
				viewEntPost.setProperty(enterprise.getNatureCode());
				viewEntPost.setEmployNum(enterprise.getScaleCode());
				viewEntPost.setPublishDate(post.getPublishDate());
				viewEntPostInsertMap.put(viewEntPost, post);

				EntPromotion entPromotion = new EntPromotion();
				entPromotion.setEntPostId(post.getId());
				entPromotion.setEntId(enterprise.getId());
				entPromotion.setPromotionValue("");
				entPromotionInsertMap.put(entPromotion, post);

				post.setStatus(2);
			}

			Map<String, Post> postNameMap = postEntNameMap.get(enterprise.getName());
			if (postNameMap == null) {
				postNameMap = new HashMap<String, Post>();
				postEntNameMap.put(enterprise.getName(), postNameMap);
			}
			postNameMap.put(post.getName(), post);
		}

		entLbsDao.update(entLbsUpdateList);
		entLbsDao.add(new ArrayList<EntLbs>(entLbsInsertMap.keySet()));
		for (EntLbs entLbs : entLbsInsertMap.keySet())
			entLbsInsertMap.get(entLbs).setLbsId(entLbs.getLbsId());
		for (EntPost entPost : entPostInsertMap.keySet())
			entPost.setLbsId(entPostInsertMap.get(entPost).getLbsId());

		entPostDao.update(entPostUpdateList);
		entPostDao.add(new ArrayList<EntPost>(entPostInsertMap.keySet()));
		for (EntPost entPost : entPostInsertMap.keySet())
			entPostInsertMap.get(entPost).setId(entPost.getId());
		for (EntAbilityRequire entAbilityRequire : entAbilityRequireInsertMap.keySet())
			entAbilityRequire.setPostId(entAbilityRequireInsertMap.get(entAbilityRequire).getId());
		for (EntPostStatus entPostStatus : entPostStatusInsertMap.keySet())
			entPostStatus.setPostId(entAbilityRequireInsertMap.get(entPostStatus).getId());
		for (ViewEntPost viewEntPost : viewEntPostInsertMap.keySet())
			viewEntPost.setPostId(entAbilityRequireInsertMap.get(viewEntPost).getId());
		for (EntPromotion entPromotion : entPromotionInsertMap.keySet())
			entPromotion.setEntPostId(entAbilityRequireInsertMap.get(entPromotion).getId());

		entAbilityRequireDao.update(entAbilityRequireUpdateList);
		entAbilityRequireDao.add(new ArrayList<EntAbilityRequire>(entAbilityRequireInsertMap.keySet()));

		entPostStatusDao.update(entPostStatusUpdateList);
		entPostStatusDao.add(new ArrayList<EntPostStatus>(entPostStatusInsertMap.keySet()));

		viewEntPostDao.update(viewEntPostUpdateList);
		viewEntPostDao.add(new ArrayList<ViewEntPost>(viewEntPostInsertMap.keySet()));

		entPromotionDao.add(new ArrayList<EntPromotion>(entPromotionInsertMap.keySet()));

		return true;
	}

	public static boolean savePost(Post post) {
		Map<EntAbilityRequire, Post> entAbilityRequireInsertMap = new HashMap<EntAbilityRequire, Post>();
		List<EntAbilityRequire> entAbilityRequireUpdateList = new ArrayList<EntAbilityRequire>();

		Enterprise enterprise = enterpriseNameMap.get(post.getEnterpriseName());

		post.setStatus(1);

		if (post.getLbsId() != null) {
			EntLbs entLbs = entLbsDao.get(post.getLbsId());
			if (entLbs != null && (Ver.nq(post.getLbsLon(), entLbs.getLongitude()) || Ver.nq(post.getLbsLat(), entLbs.getLatitude()))) {
				entLbs.setLongitude(post.getLbsLon());
				entLbs.setLatitude(post.getLbsLat());
				entLbsDao.update(entLbs);
			}
		} else {
			EntLbs entLbs = new EntLbs();
			entLbs.setLongitude(post.getLbsLon());
			entLbs.setLatitude(post.getLbsLat());
			entLbsDao.add(entLbs);
			post.setLbsId(entLbs.getLbsId());
		}

		if (post.getId() != null) {
			EntPost entPost = entPostDao.get(post.getId());
			if (entPost != null) {

				EntAbilityRequire experienceEntAbilityRequire = entAbilityRequireDao.getExperience(post.getId());
				if (experienceEntAbilityRequire != null) {
					if (Ver.nq(post.getCategoryCode(), experienceEntAbilityRequire.getPostCode()) || Ver.nq(post.getExperienceCode(), experienceEntAbilityRequire.getParamCode())) {
						Param experienceParam = paramDao.get(post.getExperienceCode());
						experienceEntAbilityRequire.setPostCode(post.getCategoryCode());
						experienceEntAbilityRequire.setParamCode(post.getExperienceCode());
						experienceEntAbilityRequire.setGrade(experienceParam.getParamValue());
						experienceEntAbilityRequire.setWeightPoint(experienceEntAbilityRequire.getTotalPoint() / experienceEntAbilityRequire.getGrade());
						entAbilityRequireUpdateList.add(experienceEntAbilityRequire);
					}
				}

				EntAbilityRequire educationEntAbilityRequire = entAbilityRequireDao.getEducation(post.getId());
				if (educationEntAbilityRequire != null) {
					if (Ver.nq(post.getCategoryCode(), educationEntAbilityRequire.getPostCode()) || Ver.nq(post.getExperienceCode(), educationEntAbilityRequire.getParamCode())) {
						Param educationParam = paramDao.get(post.getExperienceCode());
						educationEntAbilityRequire.setPostCode(post.getCategoryCode());
						educationEntAbilityRequire.setParamCode(post.getExperienceCode());
						educationEntAbilityRequire.setGrade(educationParam.getParamValue());
						educationEntAbilityRequire.setWeightPoint(educationEntAbilityRequire.getTotalPoint() / educationEntAbilityRequire.getGrade());
						entAbilityRequireUpdateList.add(educationEntAbilityRequire);
					}
				}

				if (Ver.nq(post.getName(), entPost.getPostAliases()) || Ver.nq(post.getCategory(), entPost.getPostName()) || Ver.nq(post.getCategoryCode(), entPost.getPostCode()) || Ver.nq(post.getNumber(), entPost.getHeadcounts()) || Ver.nq(post.getIsSeveral(), entPost.getIsSeveral()) || Ver.nq(post.getNatureCode(), entPost.getPjobCategory()) || Ver.nq(post.getSalary(), entPost.getPsalary()) || Ver.nq(post.getSalaryType(), entPost.getSalaryType()) || Ver.nq(post.getWelfareCode(), entPost.getTagSelected()) || Ver.nq(post.getIntroduction(), entPost.getPostRemark()) || Ver.nq(post.getAreaCode(), entPost.getParea()) || Ver.nq(post.getAddress(), entPost.getPostAddress()) || Ver.nq(post.getLbsId(), entPost.getLbsId()) || Ver.nq(post.getUpdateDate(), entPost.getUpdateDate())) {
					entPost.setPostAliases(post.getName());
					entPost.setPostName(post.getCategory());
					entPost.setPostCode(post.getCategoryCode());
					entPost.setHeadcounts(post.getNumber());
					entPost.setIsSeveral(post.getIsSeveral());
					entPost.setPjobCategory(post.getNatureCode());
					entPost.setPsalary(post.getSalary());
					entPost.setSalaryType(post.getSalaryType());
					entPost.setTagSelected(post.getWelfareCode());
					entPost.setPostRemark(post.getIntroduction());
					entPost.setParea(post.getAreaCode());
					entPost.setPostAddress(post.getAddress());
					entPost.setLbsId(post.getLbsId());
					entPost.setDataSrc(post.getDataSrc());
					entPost.setDataUrl(post.getDataUrl());
					entPost.setUpdateDate(post.getUpdateDate());
					entPostDao.update(entPost);

					if (Ver.nq(post.getNumber(), entPost.getHeadcounts())) {
						EntPostStatus entPostStatus = entPostStatusDao.get(post.getId());
						if (entPostStatus != null) {
							entPostStatus.setEmployTotal(post.getNumber());
							entPostStatus.setUnemploy(entPostStatus.getEmployTotal() - entPostStatus.getEmployed());
							if (entPostStatus.getUnemploy() < 0)
								entPostStatus.setUnemploy(0);
							entPostStatusDao.update(entPostStatus);
						}
					}

					ViewEntPost viewEntPost = viewEntPostDao.getByPostId(post.getId());
					if (viewEntPost != null) {
						viewEntPost.setPostAliases(post.getName());
						viewEntPost.setPostCode(post.getCategoryCode());
						viewEntPost.setPostPropertyCode(post.getNatureCode());
						viewEntPost.setSalaryCode(post.getSalary());
						Integer minSalary = null;
						Integer maxSalary = null;
						if (post.getSalary() != null && post.getSalary().contains("-")) {
							String[] salaries = post.getSalary().split("-", 2);
							if (salaries[0].matches("^\\d+$"))
								minSalary = Integer.parseInt(salaries[0]);
							if (salaries[1].matches("^\\d+$"))
								maxSalary = Integer.parseInt(salaries[1]);
						}
						viewEntPost.setMinSalary(minSalary);
						viewEntPost.setMaxSalary(maxSalary);
						viewEntPost.setSalaryType(post.getSalaryType());
						viewEntPost.setEntName(enterprise.getName());
						viewEntPost.setIndustry(enterprise.getCategoryCode());
						viewEntPost.setProperty(enterprise.getNatureCode());
						viewEntPost.setEmployNum(enterprise.getScaleCode());
						viewEntPostDao.update(viewEntPost);
					}

					post.setStatus(3);
				}

			}
		} else {
			EntPost entPost = new EntPost();
			entPost.setPostAliases(post.getName());
			entPost.setPostName(post.getCategory());
			entPost.setPostCode(post.getCategoryCode());
			entPost.setHeadcounts(post.getNumber());
			entPost.setIsSeveral(post.getIsSeveral());
			entPost.setPjobCategory(post.getNatureCode());
			entPost.setPsalary(post.getSalary());
			entPost.setSalaryType(post.getSalaryType());
			entPost.setTagSelected(post.getWelfareCode());
			entPost.setPostRemark(post.getIntroduction());
			entPost.setParea(post.getAreaCode());
			entPost.setPostAddress(post.getAddress());
			entPost.setLbsId(post.getLbsId());
			entPost.setDataSrc(post.getDataSrc());
			entPost.setDataUrl(post.getDataUrl());
			entPost.setUpdateDate(post.getUpdateDate());
			entPost.setPublishDate(post.getPublishDate());
			entPostDao.add(entPost);
			post.setId(entPost.getId());

			Param experienceParam = paramDao.get(post.getExperienceCode());
			if (experienceParam != null) {
				Technology experienceTechnology = technologyDao.get(experienceParam.getParamCode());
				if (experienceTechnology != null) {
					TechnologyGategory experienceTechnologyGategory = technologyGategoryDao.get(experienceTechnology.getTechnologyGategoryCode());
					if (experienceTechnologyGategory != null) {
						EntAbilityRequire experienceEntAbilityRequire = new EntAbilityRequire();
						experienceEntAbilityRequire.setPostId(post.getId());
						experienceEntAbilityRequire.setEntId(enterprise.getId());
						experienceEntAbilityRequire.setPostCode(post.getCategoryCode());
						experienceEntAbilityRequire.setParamCode(post.getExperienceCode());
						experienceEntAbilityRequire.setGrade(experienceParam.getParamValue());
						experienceEntAbilityRequire.setMatchType(experienceTechnology.getMatchType());
						experienceEntAbilityRequire.setTechnologyCode(experienceTechnology.getTechnicalCode());
						experienceEntAbilityRequire.setTechnologyCateCode(experienceTechnology.getTechnologyGategoryCode());
						experienceEntAbilityRequire.setTotalPoint(experienceTechnologyGategory.getPercent() / 2.0);
						experienceEntAbilityRequire.setWeightPoint(experienceEntAbilityRequire.getTotalPoint() / experienceEntAbilityRequire.getGrade());
						entAbilityRequireInsertMap.put(experienceEntAbilityRequire, post);
					}
				}
			}

			Param educationParam = paramDao.get(post.getEducationCode());
			if (educationParam != null) {
				Technology educationTechnology = technologyDao.get(educationParam.getParamCode());
				if (educationTechnology != null) {
					TechnologyGategory educationTechnologyGategory = technologyGategoryDao.get(educationTechnology.getTechnologyGategoryCode());
					if (educationTechnologyGategory != null) {
						EntAbilityRequire educationEntAbilityRequire = new EntAbilityRequire();
						educationEntAbilityRequire.setPostId(post.getId());
						educationEntAbilityRequire.setEntId(enterprise.getId());
						educationEntAbilityRequire.setPostCode(post.getCategoryCode());
						educationEntAbilityRequire.setParamCode(post.getExperienceCode());
						educationEntAbilityRequire.setGrade(educationParam.getParamValue());
						educationEntAbilityRequire.setMatchType(educationTechnology.getMatchType());
						educationEntAbilityRequire.setTechnologyCode(educationTechnology.getTechnicalCode());
						educationEntAbilityRequire.setTechnologyCateCode(educationTechnology.getTechnologyGategoryCode());
						educationEntAbilityRequire.setTotalPoint(educationTechnologyGategory.getPercent() / 2.0);
						educationEntAbilityRequire.setWeightPoint(educationEntAbilityRequire.getTotalPoint() / educationEntAbilityRequire.getGrade());
						entAbilityRequireInsertMap.put(educationEntAbilityRequire, post);
					}
				}
			}

			EntPostStatus entPostStatus = new EntPostStatus();
			entPostStatus.setPostId(post.getId());
			entPostStatus.setPostStatus(1);
			entPostStatus.setEmploy(0);
			entPostStatus.setEmployed(0);
			entPostStatus.setEmployTotal(post.getNumber());
			entPostStatus.setUnemploy(post.getNumber());
			entPostStatus.setSkimCount(0);
			entPostStatusDao.add(entPostStatus);

			ViewEntPost viewEntPost = new ViewEntPost();
			viewEntPost.setPostId(post.getId());
			viewEntPost.setPostAliases(post.getName());
			viewEntPost.setPostCode(post.getCategoryCode());
			viewEntPost.setPostPropertyCode(post.getNatureCode());
			viewEntPost.setSalaryCode(post.getSalary());
			Integer minSalary = null;
			Integer maxSalary = null;
			if (post.getSalary() != null && post.getSalary().contains("-")) {
				String[] salaries = post.getSalary().split("-", 2);
				if (salaries[0].matches("^\\d+$"))
					minSalary = Integer.parseInt(salaries[0]);
				if (salaries[1].matches("^\\d+$"))
					maxSalary = Integer.parseInt(salaries[1]);
			}
			viewEntPost.setMinSalary(minSalary);
			viewEntPost.setMaxSalary(maxSalary);
			viewEntPost.setSalaryType(post.getSalaryType());
			viewEntPost.setEntName(enterprise.getName());
			viewEntPost.setIndustry(enterprise.getCategoryCode());
			viewEntPost.setProperty(enterprise.getNatureCode());
			viewEntPost.setEmployNum(enterprise.getScaleCode());
			viewEntPost.setPublishDate(post.getPublishDate());
			viewEntPostDao.add(viewEntPost);

			EntPromotion entPromotion = new EntPromotion();
			entPromotion.setEntPostId(post.getId());
			entPromotion.setEntId(enterprise.getId());
			entPromotion.setPromotionValue("");
			entPromotionDao.add(entPromotion);

			post.setStatus(2);
		}

		entAbilityRequireDao.update(entAbilityRequireUpdateList);
		entAbilityRequireDao.add(new ArrayList<EntAbilityRequire>(entAbilityRequireInsertMap.keySet()));

		Map<String, Post> postNameMap = postEntNameMap.get(enterprise.getName());
		if (postNameMap == null) {
			postNameMap = new HashMap<String, Post>();
			postEntNameMap.put(enterprise.getName(), postNameMap);
		}
		postNameMap.put(post.getName(), post);

		return true;
	}

	public static boolean saveEnterprise(List<Enterprise> list, Integer updateInterval) {
		if (updateInterval == null)
			updateInterval = 3;

		Map<EntLbs, Enterprise> entLbsInsertMap = new HashMap<EntLbs, Enterprise>();
		Map<EntEnterprise, Enterprise> entEnterpriseInsertMap = new HashMap<EntEnterprise, Enterprise>();
		Map<EntAccount, Enterprise> entAccountInsertMap = new HashMap<EntAccount, Enterprise>();
		List<EntLbs> entLbsUpdateList = new ArrayList<EntLbs>();
		List<EntEnterprise> entEnterpriseUpdateList = new ArrayList<EntEnterprise>();

		for (Enterprise enterprise : list) {

			enterprise.setStatus(1);

			if (enterprise.getLbsId() != null) {
				EntLbs entLbs = entLbsDao.get(enterprise.getLbsId());
				if (entLbs != null) {
					if (Ver.nq(enterprise.getLbsLon(), entLbs.getLongitude()) || Ver.nq(enterprise.getLbsLat(), entLbs.getLatitude())) {
						entLbs.setLongitude(enterprise.getLbsLon());
						entLbs.setLatitude(enterprise.getLbsLat());
						entLbsUpdateList.add(entLbs);
					}
				}
			} else {
				EntLbs entLbs = new EntLbs();
				entLbs.setLongitude(enterprise.getLbsLon());
				entLbs.setLatitude(enterprise.getLbsLat());
				entLbsInsertMap.put(entLbs, enterprise);
			}

			if (enterprise.getId() != null) {
				EntEnterprise entEnterprise = entEnterpriseDao.get(enterprise.getId());
				if (entEnterprise != null) {
					if (Ver.nq(enterprise.getName(), entEnterprise.getEntName()) || Ver.nq(enterprise.getCategoryCode(), entEnterprise.getIndustry()) || Ver.nq(enterprise.getNatureCode(), entEnterprise.getProperty()) || Ver.nq(enterprise.getScaleCode(), entEnterprise.getEmployNum()) || Ver.nq(enterprise.getIntroduction(), entEnterprise.getIntroduction()) || Ver.nq(enterprise.getWebsite(), entEnterprise.getEntWeb()) || Ver.nq(enterprise.getAreaCode(), entEnterprise.getParea()) || Ver.nq(enterprise.getAddress(), entEnterprise.getAddress())) {
						entEnterprise.setEntName(enterprise.getName());
						entEnterprise.setIndustry(enterprise.getCategoryCode());
						entEnterprise.setProperty(enterprise.getNatureCode());
						entEnterprise.setEmployNum(enterprise.getScaleCode());
						entEnterprise.setIntroduction(enterprise.getIntroduction());
						entEnterprise.setEntWeb(enterprise.getWebsite());
						entEnterprise.setParea(enterprise.getAreaCode());
						entEnterprise.setAddress(enterprise.getAddress());
						entEnterprise.setLbsId(enterprise.getLbsId());
						entEnterprise.setDataSrc(enterprise.getDataSrc());
						entEnterprise.setDataUrl(enterprise.getDataUrl());
						entEnterpriseUpdateList.add(entEnterprise);

						enterprise.setStatus(3);
					}
				}
			} else {
				EntEnterprise entEnterprise = new EntEnterprise();
				entEnterprise.setEntName(enterprise.getName());
				entEnterprise.setIndustry(enterprise.getCategoryCode());
				entEnterprise.setProperty(enterprise.getNatureCode());
				entEnterprise.setEmployNum(enterprise.getScaleCode());
				entEnterprise.setIntroduction(enterprise.getIntroduction());
				entEnterprise.setEntWeb(enterprise.getWebsite());
				entEnterprise.setParea(enterprise.getAreaCode());
				entEnterprise.setAddress(enterprise.getAddress());
				entEnterprise.setLbsId(enterprise.getLbsId());
				entEnterprise.setDataSrc(enterprise.getDataSrc());
				entEnterprise.setDataUrl(enterprise.getDataUrl());
				entEnterprise.setCreateDate(enterprise.getCreateDate());
				entEnterpriseInsertMap.put(entEnterprise, enterprise);

				EntAccount entAccount = new EntAccount();
				entAccount.setEntId(enterprise.getId());
				entAccount.setAccount(String.format("zcdh%s", accountNumFormat.format(++lastAccountNum)));
				entAccount.setPwd("oSBrriGEzW8KHAL6b9J63w==");
				entAccount.setCreateMode(2);
				entAccount.setCreateDate(enterprise.getCreateDate());
				entAccount.setStatus(1);
				entAccountInsertMap.put(entAccount, enterprise);

				enterprise.setStatus(2);
			}

			enterpriseNameMap.put(enterprise.getName(), enterprise);
		}

		entLbsDao.update(entLbsUpdateList);
		entLbsDao.add(new ArrayList<EntLbs>(entLbsInsertMap.keySet()));
		for (EntLbs entLbs : entLbsInsertMap.keySet())
			entLbsInsertMap.get(entLbs).setLbsId(entLbs.getLbsId());
		for (EntEnterprise entEnterprise : entEnterpriseInsertMap.keySet())
			entEnterprise.setLbsId(entEnterpriseInsertMap.get(entEnterprise).getLbsId());

		entEnterpriseDao.update(entEnterpriseUpdateList);
		entEnterpriseDao.add(new ArrayList<EntEnterprise>(entEnterpriseInsertMap.keySet()));
		for (EntEnterprise entEnterprise : entEnterpriseInsertMap.keySet())
			entEnterpriseInsertMap.get(entEnterprise).setId(entEnterprise.getEntId());
		for (EntAccount entAccount : entAccountInsertMap.keySet())
			entAccount.setEntId(entAccountInsertMap.get(entAccount).getId());

		entAccountDao.add(new ArrayList<EntAccount>(entAccountInsertMap.keySet()));

		return true;
	}

	public static boolean saveEnterprise(Enterprise enterprise) {
		enterprise.setStatus(1);

		if (enterprise.getLbsId() != null) {
			EntLbs entLbs = entLbsDao.get(enterprise.getLbsId());
			if (entLbs != null) {
				if (Ver.nq(enterprise.getLbsLon(), entLbs.getLongitude()) || Ver.nq(enterprise.getLbsLat(), entLbs.getLatitude())) {
					entLbs.setLongitude(enterprise.getLbsLon());
					entLbs.setLatitude(enterprise.getLbsLat());
					entLbsDao.update(entLbs);
				}
			}
		} else {
			EntLbs entLbs = new EntLbs();
			entLbs.setLongitude(enterprise.getLbsLon());
			entLbs.setLatitude(enterprise.getLbsLat());
			entLbsDao.add(entLbs);
			enterprise.setLbsId(entLbs.getLbsId());
		}

		if (enterprise.getId() != null) {
			EntEnterprise entEnterprise = entEnterpriseDao.get(enterprise.getId());
			if (entEnterprise != null) {
				if (Ver.nq(enterprise.getName(), entEnterprise.getEntName()) || Ver.nq(enterprise.getCategoryCode(), entEnterprise.getIndustry()) || Ver.nq(enterprise.getNatureCode(), entEnterprise.getProperty()) || Ver.nq(enterprise.getScaleCode(), entEnterprise.getEmployNum()) || Ver.nq(enterprise.getIntroduction(), entEnterprise.getIntroduction()) || Ver.nq(enterprise.getWebsite(), entEnterprise.getEntWeb()) || Ver.nq(enterprise.getAreaCode(), entEnterprise.getParea()) || Ver.nq(enterprise.getAddress(), entEnterprise.getAddress())) {
					entEnterprise.setEntName(enterprise.getName());
					entEnterprise.setIndustry(enterprise.getCategoryCode());
					entEnterprise.setProperty(enterprise.getNatureCode());
					entEnterprise.setEmployNum(enterprise.getScaleCode());
					entEnterprise.setIntroduction(enterprise.getIntroduction());
					entEnterprise.setEntWeb(enterprise.getWebsite());
					entEnterprise.setParea(enterprise.getAreaCode());
					entEnterprise.setAddress(enterprise.getAddress());
					entEnterprise.setLbsId(enterprise.getLbsId());
					entEnterprise.setDataSrc(enterprise.getDataSrc());
					entEnterprise.setDataUrl(enterprise.getDataUrl());
					entEnterpriseDao.update(entEnterprise);

					enterprise.setStatus(3);
				}
			}
		} else {
			EntEnterprise entEnterprise = new EntEnterprise();
			entEnterprise.setEntName(enterprise.getName());
			entEnterprise.setIndustry(enterprise.getCategoryCode());
			entEnterprise.setProperty(enterprise.getNatureCode());
			entEnterprise.setEmployNum(enterprise.getScaleCode());
			entEnterprise.setIntroduction(enterprise.getIntroduction());
			entEnterprise.setEntWeb(enterprise.getWebsite());
			entEnterprise.setParea(enterprise.getAreaCode());
			entEnterprise.setAddress(enterprise.getAddress());
			entEnterprise.setLbsId(enterprise.getLbsId());
			entEnterprise.setDataSrc(enterprise.getDataSrc());
			entEnterprise.setDataUrl(enterprise.getDataUrl());
			entEnterprise.setCreateDate(enterprise.getCreateDate());
			entEnterpriseDao.add(entEnterprise);
			enterprise.setId(entEnterprise.getEntId());

			EntAccount entAccount = new EntAccount();
			entAccount.setEntId(enterprise.getId());
			entAccount.setAccount(String.format("zcdh%s", accountNumFormat.format(++lastAccountNum)));
			entAccount.setPwd("oSBrriGEzW8KHAL6b9J63w==");
			entAccount.setCreateMode(2);
			entAccount.setCreateDate(enterprise.getCreateDate());
			entAccount.setStatus(1);
			entAccountDao.add(entAccount);

			enterprise.setStatus(2);
		}

		enterpriseNameMap.put(enterprise.getName(), enterprise);

		return true;
	}

	public static Enterprise mergeEnterprise(Enterprise ent) {
		Enterprise enterprise = enterpriseNameMap.get(ent.getName());
		if (enterprise != null) {

			ent.setId(enterprise.getId());

			if (Ver.isNotBlank(enterprise.getCategory()))
				ent.setCategory(enterprise.getCategory());

			if (Ver.isNotBlank(enterprise.getCategoryCode()))
				ent.setCategoryCode(enterprise.getCategoryCode());

			if (Ver.isNotBlank(enterprise.getNature()))
				ent.setNature(enterprise.getNature());

			if (Ver.isNotBlank(enterprise.getNatureCode()))
				ent.setNatureCode(enterprise.getNatureCode());

			if (Ver.isNotBlank(enterprise.getScale()))
				ent.setScale(enterprise.getScale());

			if (Ver.isNotBlank(enterprise.getScaleCode()))
				ent.setScaleCode(enterprise.getScaleCode());

			if (Ver.isNotBlank(enterprise.getIntroduction()))
				ent.setIntroduction(enterprise.getIntroduction());

			if (Ver.isNotBlank(enterprise.getWebsite()))
				ent.setWebsite(enterprise.getWebsite());

			if (Ver.isNotBlank(enterprise.getAreaCode()))
				ent.setAreaCode(enterprise.getAreaCode());

			if (Ver.isNotBlank(enterprise.getAddress()))
				ent.setAddress(enterprise.getAddress());

			if (enterprise.getLbsId() != null)
				ent.setLbsId(enterprise.getLbsId());

			if (enterprise.getCreateDate() != null)
				ent.setCreateDate(enterprise.getCreateDate());
		}
		return enterprise;
	}

	public static Post mergePost(Post p) {
		Map<String, Post> postNameMap = postEntNameMap.get(p.getEnterpriseName());
		if (postNameMap != null) {
			Post post = postNameMap.get(p.getName());
			if (post != null) {
				p.setId(post.getId());

				if (Ver.isNotBlank(post.getCategory()))
					p.setCategory(post.getCategory());

				if (Ver.isNotBlank(post.getCategoryCode()))
					p.setCategoryCode(post.getCategoryCode());

				if (post.getNumber() != null)
					p.setNumber(post.getNumber());

				if (Ver.isNotBlank(post.getNumberText()))
					p.setNumberText(post.getNumberText());

				if (post.getIsSeveral() != null)
					p.setIsSeveral(post.getIsSeveral());

				if (Ver.isNotBlank(post.getNature()))
					p.setNature(post.getNature());

				if (Ver.isNotBlank(post.getNatureCode()))
					p.setNatureCode(post.getNatureCode());

				if (Ver.isNotBlank(post.getSalary()))
					p.setSalary(post.getSalary());

				if (Ver.isNotBlank(post.getSalaryText()))
					p.setSalaryText(post.getSalaryText());

				if (post.getSalaryType() != null)
					p.setSalaryType(post.getSalaryType());

				if (Ver.isNotBlank(post.getExperience()))
					p.setExperience(post.getExperience());

				if (Ver.isNotBlank(post.getExperienceCode()))
					p.setExperienceCode(post.getExperienceCode());

				if (Ver.isNotBlank(post.getEducation()))
					p.setEducation(post.getEducation());

				if (Ver.isNotBlank(post.getEducationCode()))
					p.setEducationCode(post.getEducationCode());

				if (Ver.isBlank(post.getWelfare()))
					p.setWelfare(post.getWelfare());

				if (Ver.isNotBlank(post.getWelfareCode()))
					p.setWelfareCode(post.getWelfareCode());

				if (Ver.isNotBlank(post.getIntroduction()))
					p.setIntroduction(post.getIntroduction());

				if (Ver.isNotBlank(post.getAreaCode()))
					p.setAreaCode(post.getAreaCode());

				if (Ver.isNotBlank(post.getAddress()))
					p.setAddress(post.getAddress());

				if (post.getLbsId() != null)
					p.setLbsId(post.getLbsId());

				if (post.getUpdateDate() != null)
					p.setUpdateDate(post.getUpdateDate());

				if (post.getPublishDate() != null)
					p.setPublishDate(post.getPublishDate());
			}
		}
		return p;
	}

}
