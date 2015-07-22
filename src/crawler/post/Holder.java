package crawler.post;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import map.Cluster;
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
import util.WebContext;
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

public class Holder {

	private static Logger logger = Logger.getLogger(Holder.class);

	private static Map<String, String> areaNameMap = new ConcurrentHashMap<String, String>();

	private static Map<String, String> tagCodeMap = new ConcurrentHashMap<String, String>();
	private static Map<String, String> tagNameMap = new ConcurrentHashMap<String, String>();

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
	// private static Map<String, Post> postUrlMap = new
	// ConcurrentHashMap<String, Post>();
	private static Map<String, Integer> postUrlIndexes = new ConcurrentHashMap<String, Integer>();
	private static List<Post> postList = new ArrayList<Post>();
	private static ThreadLocal<Integer> localPostNameCount = new ThreadLocal<Integer>();
	private static Map<String, Map<String, Post>> postEntNameMap = new ConcurrentHashMap<String, Map<String, Post>>();
	private static Map<String, Enterprise> enterpriseUrlMap = new ConcurrentHashMap<String, Enterprise>();
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
		logger.info("------ init holder ------");

		logger.info("binning dao ...");
		areaDao = WebContext.getBean(AreaDao.class);
		categoryPostDao = WebContext.getBean(CategoryPostDao.class);
		entAbilityRequireDao = WebContext.getBean(EntAbilityRequireDao.class);
		entAccountDao = WebContext.getBean(EntAccountDao.class);
		entEnterpriseDao = WebContext.getBean(EntEnterpriseDao.class);
		entLbsDao = WebContext.getBean(EntLbsDao.class);
		entPostDao = WebContext.getBean(EntPostDao.class);
		entPostStatusDao = WebContext.getBean(EntPostStatusDao.class);
		industryDao = WebContext.getBean(IndustryDao.class);
		paramDao = WebContext.getBean(ParamDao.class);
		postDao = WebContext.getBean(PostDao.class);
		tagDao = WebContext.getBean(TagDao.class);
		technologyDao = WebContext.getBean(TechnologyDao.class);
		technologyGategoryDao = WebContext.getBean(TechnologyGategoryDao.class);
		viewEntPostDao = WebContext.getBean(ViewEntPostDao.class);
		entPromotionDao = WebContext.getBean(EntPromotionDao.class);

		logger.info("binning tag ...");
		for (Tag tag : tagDao.findAll()) {
			tagCodeMap.put(tag.getTagCode(), tag.getTagName());
			tagNameMap.put(tag.getTagName(), tag.getTagCode());
		}

		logger.info("binning area ...");
		for (Area area : areaDao.find(AreaType.CITY))
			if (!area.getAreaName().contains("行政"))
				areaNameMap.put(area.getAreaName().replaceAll("\\s+|市$|盟$|地区$|族$|自治州$|族自治州$", ""), area.getAreaCode());

		logger.info("binning post ...");
		for (model.Post post : postDao.findAll()) {
			CategoryPost categoryPost = categoryPostDao.get(post.getPostCategoryCode());
			if (categoryPost != null) {
				Map<String, String> postCategorie = new HashMap<String, String>();
				postCategorie.put("code", post.getPostCode());
				postCategorie.put("name", post.getPostName());
				postCategorie.put("group", categoryPost.getPostCategoryName());
				postCategoryCodeMap.put(post.getPostCode(), postCategorie);
			}
		}

		logger.info("binning param ...");
		for (Param param : paramDao.findAll()) {
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
		}

		logger.info("binning industry ...");
		for (Industry industry : industryDao.findAll())
			enterpriseCategoryCodeMap.put(industry.getIndustryCode(), industry.getIndustryName());

		logger.info("binning lastAccountNum ...");
		lastAccountNum = entAccountDao.getLastAutoAccountNum();

		logger.info("binning entEnterprise ...");
		for (EntEnterprise entEnterprise : entEnterpriseDao.findAll()) {
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
		}

		logger.info("binning entPost ...");
		for (EntPost entPost : entPostDao.findAll()) {
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
		}

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
						if (Ver.nq(post.getCategoryCode(), educationEntAbilityRequire.getPostCode()) || Ver.nq(post.getEducationCode(), educationEntAbilityRequire.getParamCode())) {
							Param educationParam = paramDao.get(post.getEducationCode());
							educationEntAbilityRequire.setPostCode(post.getCategoryCode());
							educationEntAbilityRequire.setParamCode(post.getEducationCode());
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
				entPost.setEntId(enterprise.getId());
				entPostInsertMap.put(entPost, post);

				Param experienceParam = paramDao.get(post.getExperienceCode());
				if (experienceParam != null) {
					Technology experienceTechnology = technologyDao.get("-0000000000003");
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
					Technology educationTechnology = technologyDao.get("-0000000000004");
					if (educationTechnology != null) {
						TechnologyGategory educationTechnologyGategory = technologyGategoryDao.get(educationTechnology.getTechnologyGategoryCode());
						if (educationTechnologyGategory != null) {
							EntAbilityRequire educationEntAbilityRequire = new EntAbilityRequire();
							educationEntAbilityRequire.setPostId(post.getId());
							educationEntAbilityRequire.setEntId(enterprise.getId());
							educationEntAbilityRequire.setPostCode(post.getCategoryCode());
							educationEntAbilityRequire.setParamCode(post.getEducationCode());
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
				viewEntPost.setEntId(enterprise.getId());
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
			entPostStatus.setPostId(entPostStatusInsertMap.get(entPostStatus).getId());
		for (ViewEntPost viewEntPost : viewEntPostInsertMap.keySet())
			viewEntPost.setPostId(viewEntPostInsertMap.get(viewEntPost).getId());
		for (EntPromotion entPromotion : entPromotionInsertMap.keySet())
			entPromotion.setEntPostId(entPromotionInsertMap.get(entPromotion).getId());

		entAbilityRequireDao.update(entAbilityRequireUpdateList);
		entAbilityRequireDao.add(new ArrayList<EntAbilityRequire>(entAbilityRequireInsertMap.keySet()));

		entPostStatusDao.update(entPostStatusUpdateList);
		entPostStatusDao.add(new ArrayList<EntPostStatus>(entPostStatusInsertMap.keySet()));

		viewEntPostDao.update(viewEntPostUpdateList);
		viewEntPostDao.add(new ArrayList<ViewEntPost>(viewEntPostInsertMap.keySet()));

		entPromotionDao.add(new ArrayList<EntPromotion>(entPromotionInsertMap.keySet()));

		holdAllPost(list);

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
					if (Ver.nq(post.getCategoryCode(), educationEntAbilityRequire.getPostCode()) || Ver.nq(post.getEducationCode(), educationEntAbilityRequire.getParamCode())) {
						Param educationParam = paramDao.get(post.getEducationCode());
						educationEntAbilityRequire.setPostCode(post.getCategoryCode());
						educationEntAbilityRequire.setParamCode(post.getEducationCode());
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
			entPost.setEntId(enterprise.getId());
			entPostDao.add(entPost);
			post.setId(entPost.getId());

			Param experienceParam = paramDao.get(post.getExperienceCode());
			if (experienceParam != null) {
				Technology experienceTechnology = technologyDao.get("-0000000000003");
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
				Technology educationTechnology = technologyDao.get("-0000000000004");
				if (educationTechnology != null) {
					TechnologyGategory educationTechnologyGategory = technologyGategoryDao.get(educationTechnology.getTechnologyGategoryCode());
					if (educationTechnologyGategory != null) {
						EntAbilityRequire educationEntAbilityRequire = new EntAbilityRequire();
						educationEntAbilityRequire.setPostId(post.getId());
						educationEntAbilityRequire.setEntId(enterprise.getId());
						educationEntAbilityRequire.setPostCode(post.getCategoryCode());
						educationEntAbilityRequire.setParamCode(post.getEducationCode());
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
			viewEntPost.setAreaCode(post.getAreaCode());
			viewEntPost.setLon(post.getLbsLon());
			viewEntPost.setLat(post.getLbsLat());
			viewEntPost.setEntId(enterprise.getId());
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

		holdPost(post);

		return true;
	}

	public static boolean saveEnterprise(List<Enterprise> list) {
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

		holdAllEnterprise(list);

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

		holdEnterprise(enterprise);

		return true;
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

}
