package dao.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import model.Area;
import model.Area.AreaType;
import model.CategoryPost;
import model.EntAbilityRequire;
import model.EntAccount;
import model.EntEnterprise;
import model.EntLbs;
import model.EntPost;
import model.EntPostStatus;
import model.Industry;
import model.Param;
import model.Post;
import model.Tag;
import model.Technology;
import model.TechnologyGategory;
import model.ViewEntPost;

import org.apache.log4j.Logger;

import util.Ver;
import dao.AreaDao;
import dao.CategoryPostDao;
import dao.EntAbilityRequireDao;
import dao.EntAccountDao;
import dao.EntEnterpriseDao;
import dao.EntLbsDao;
import dao.EntPostDao;
import dao.EntPostStatusDao;
import dao.IndustryDao;
import dao.ParamDao;
import dao.PostDao;
import dao.TagDao;
import dao.TechnologyDao;
import dao.TechnologyGategoryDao;
import dao.ViewEntPostDao;
import dao.data.C3P0Store.Iterator;

public class Stack {

	private static Logger logger = Logger.getLogger(Stack.class);

	@Resource
	private AreaDao areaDao;
	@Resource
	private CategoryPostDao categoryPostDao;
	@Resource
	private EntAbilityRequireDao entAbilityRequireDao;
	@Resource
	private EntAccountDao entAccountDao;
	@Resource
	private EntEnterpriseDao entEnterpriseDao;
	@Resource
	private EntLbsDao entLbsDao;
	@Resource
	private EntPostDao entPostDao;
	@Resource
	private EntPostStatusDao entPostStatusDao;
	@Resource
	private IndustryDao industryDao;
	@Resource
	private ParamDao paramDao;
	@Resource
	private PostDao postDao;
	@Resource
	private TagDao tagDao;
	@Resource
	private TechnologyDao technologyDao;
	@Resource
	private TechnologyGategoryDao technologyGategoryDao;
	@Resource
	private ViewEntPostDao viewEntPostDao;

	public static Map<Integer, Area> areaIdMap = new ConcurrentHashMap<Integer, Area>();
	public static Map<Integer, List<Area>> areaTypeMap = new ConcurrentHashMap<Integer, List<Area>>();
	public static Map<String, Area> areaCodeMap = new ConcurrentHashMap<String, Area>();

	public static Map<Integer, CategoryPost> categoryPostIdMap = new ConcurrentHashMap<Integer, CategoryPost>();
	public static Map<String, CategoryPost> categoryPostCodeMap = new ConcurrentHashMap<String, CategoryPost>();

	public static Map<Long, EntAbilityRequire> entAbilityRequireIdMap = new ConcurrentHashMap<Long, EntAbilityRequire>();
	public static Map<Long, EntAbilityRequire> entAbilityRequireExperiencePostIdMap = new ConcurrentHashMap<Long, EntAbilityRequire>();
	public static Map<Long, EntAbilityRequire> entAbilityRequireEducationPostIdMap = new ConcurrentHashMap<Long, EntAbilityRequire>();

	public static Map<Long, EntAccount> entAccountIdMap = new ConcurrentHashMap<Long, EntAccount>();
	public static Map<Long, EntAccount> entAccountEntIdMap = new ConcurrentHashMap<Long, EntAccount>();

	public static Map<Long, EntEnterprise> entEnterpriseIdMap = new ConcurrentHashMap<Long, EntEnterprise>();
	public static Map<String, EntEnterprise> entEnterpriseNameMap = new ConcurrentHashMap<String, EntEnterprise>();

	public static Map<Long, EntLbs> entLbsIdMap = new ConcurrentHashMap<Long, EntLbs>();

	public static Map<Long, EntPost> entPostIdMap = new ConcurrentHashMap<Long, EntPost>();
	public static Map<String, Map<String, EntPost>> entPostEntNameMap = new ConcurrentHashMap<String, Map<String, EntPost>>();

	public static Map<Long, EntPostStatus> entPostStatusIdMap = new ConcurrentHashMap<Long, EntPostStatus>();
	public static Map<Long, EntPostStatus> entPostStatusPostIdMap = new ConcurrentHashMap<Long, EntPostStatus>();

	public static Map<Integer, Industry> industryIdMap = new ConcurrentHashMap<Integer, Industry>();

	public static Map<Integer, Param> paramIdMap = new ConcurrentHashMap<Integer, Param>();
	public static Map<String, Param> paramCodeMap = new ConcurrentHashMap<String, Param>();

	public static Map<Integer, Post> postIdMap = new ConcurrentHashMap<Integer, Post>();

	public static Map<Integer, Tag> tagIdMap = new ConcurrentHashMap<Integer, Tag>();

	public static Map<Integer, Technology> technologyIdMap = new ConcurrentHashMap<Integer, Technology>();
	public static Map<String, Technology> technologyCodeMap = new ConcurrentHashMap<String, Technology>();

	public static Map<Integer, TechnologyGategory> technologyGategoryIdMap = new ConcurrentHashMap<Integer, TechnologyGategory>();
	public static Map<String, TechnologyGategory> technologyGategoryCodeMap = new ConcurrentHashMap<String, TechnologyGategory>();

	public static Map<Long, ViewEntPost> viewEntPostIdMap = new ConcurrentHashMap<Long, ViewEntPost>();
	public static Map<Long, ViewEntPost> viewEntPostPostIdMap = new ConcurrentHashMap<Long, ViewEntPost>();
	public static Map<Long, ViewEntPost> viewEntPostEntIdMap = new ConcurrentHashMap<Long, ViewEntPost>();

	static {
		areaTypeMap.put(AreaType.PROVINCE, new ArrayList<Area>());
		areaTypeMap.put(AreaType.CITY, new ArrayList<Area>());
		areaTypeMap.put(AreaType.REGION, new ArrayList<Area>());
	}

	public void init() {
		logger.info("------ init Stack ------");
		areaDao.selectList("select * from zcdh_area where is_delete=1 or is_delete is null", new Iterator<Area>() {
			public boolean next(Area area, int index) throws Exception {
				if (area.getId() != null)
					areaIdMap.put(area.getId(), area);
				if (Ver.nb(area.getAreaCode()) && Ver.nb(area.getAreaName())) {
					if (area.getAreaCode().matches("^\\d{3}$"))
						areaTypeMap.get(AreaType.PROVINCE).add(area);
					else if (area.getAreaCode().matches("^\\d{3}.\\d{3}$"))
						areaTypeMap.get(AreaType.CITY).add(area);
					else if (area.getAreaCode().matches("^\\d{3}.\\d{3}.\\d{3}$"))
						areaTypeMap.get(AreaType.REGION).add(area);
					areaCodeMap.put(area.getAreaCode(), area);
				}
				return true;
			}
		});

		categoryPostDao.selectList("select * from zcdh_category_post where is_delete=1 or is_delete is null", new Iterator<CategoryPost>() {
			public boolean next(CategoryPost categoryPost, int index) throws Exception {
				if (categoryPost.getId() != null)
					categoryPostIdMap.put(categoryPost.getId(), categoryPost);
				if (Ver.nb(categoryPost.getPostCategoryCode()))
					categoryPostCodeMap.put(categoryPost.getPostCategoryCode(), categoryPost);
				return true;
			}
		});

		entAbilityRequireDao.selectList("select * from zcdh_ent_ability_require where technology_code in('-0000000000003','-0000000000004')", new Iterator<EntAbilityRequire>() {
			public boolean next(EntAbilityRequire entAbilityRequire, int index) throws Exception {
				if (entAbilityRequire.getEntAbilityId() != null)
					entAbilityRequireIdMap.put(entAbilityRequire.getEntAbilityId(), entAbilityRequire);
				if (entAbilityRequire.getPostId() != null)
					if ("-0000000000003".equals(entAbilityRequire.getTechnologyCode()))
						entAbilityRequireExperiencePostIdMap.put(entAbilityRequire.getPostId(), entAbilityRequire);
					else if ("-0000000000004".equals(entAbilityRequire.getTechnologyCode()))
						entAbilityRequireEducationPostIdMap.put(entAbilityRequire.getPostId(), entAbilityRequire);

				return true;
			}
		});

		entAccountDao.selectAll(new Iterator<EntAccount>() {
			public boolean next(EntAccount entAccount, int index) throws Exception {
				if (entAccount.getEntId() != null)
					entAccountEntIdMap.put(entAccount.getEntId(), entAccount);
				return true;
			}
		});

		entEnterpriseDao.selectAll(new Iterator<EntEnterprise>() {
			public boolean next(EntEnterprise entEnterprise, int index) throws Exception {
				if (entEnterprise.getEntId() != null)
					entEnterpriseIdMap.put(entEnterprise.getEntId(), entEnterprise);
				if (Ver.nb(entEnterprise.getEntName()))
					entEnterpriseNameMap.put(entEnterprise.getEntName(), entEnterprise);
				return true;
			}
		});

		entLbsDao.selectAll(new Iterator<EntLbs>() {
			public boolean next(EntLbs entLbs, int index) throws Exception {
				if (entLbs.getLbsId() != null)
					entLbsIdMap.put(entLbs.getLbsId(), entLbs);
				return true;
			}

		});

		entPostDao.selectAll(new Iterator<EntPost>() {
			public boolean next(EntPost entPost, int index) throws Exception {
				if (entPost.getId() != null) {
					entPostIdMap.put(entPost.getId(), entPost);
					if (entPost.getEntId() != null) {
						EntEnterprise entEnterprise = entEnterpriseIdMap.get(entPost.getEntId());
						if (entEnterprise != null) {
							String entName = entEnterprise.getEntName();
							if (Ver.nb(entName) && Ver.nb(entPost.getPostAddress())) {
								Map<String, EntPost> entPostNameMap = entPostEntNameMap.get(entName);
								if (entPostNameMap == null) {
									entPostNameMap = new HashMap<String, EntPost>();
									entPostEntNameMap.put(entName, entPostNameMap);
								}
								entPostNameMap.put(entPost.getPostAddress(), entPost);
							}
						}
					}
				}
				return true;
			}
		});

		entPostStatusDao.selectAll(new Iterator<EntPostStatus>() {
			public boolean next(EntPostStatus entPostStatus, int index) throws Exception {
				if (entPostStatus.getPsId() != null)
					entPostStatusIdMap.put(entPostStatus.getPsId(), entPostStatus);
				if (entPostStatus.getPostId() != null)
					entPostStatusPostIdMap.put(entPostStatus.getPostId(), entPostStatus);
				return true;
			}
		});

		industryDao.selectList("select * from zcdh_industry where is_delete=1 or is_delete is null", new Iterator<Industry>() {
			public boolean next(Industry industry, int index) throws Exception {
				if (industry.getId() != null)
					industryIdMap.put(industry.getId(), industry);
				return true;
			}
		});

		paramDao.selectList("select * from zcdh_param where is_delete=1 or is_delete is null", new Iterator<Param>() {
			public boolean next(Param param, int index) throws Exception {
				if (param.getId() != null)
					paramIdMap.put(param.getId(), param);
				if (Ver.nb(param.getParamCode()))
					paramCodeMap.put(param.getParamCode(), param);
				return true;
			}
		});

		postDao.selectList("select * from zcdh_post where is_delete=1 or is_delete is null", new Iterator<Post>() {
			public boolean next(Post post, int index) throws Exception {
				if (post.getId() != null)
					postIdMap.put(post.getId(), post);
				return true;
			}
		});

		tagDao.selectList("select * from zcdh_tag where is_delete=1 or is_delete is null", new Iterator<Tag>() {
			public boolean next(Tag tag, int index) throws Exception {
				if (tag.getTagId() != null)
					tagIdMap.put(tag.getTagId(), tag);
				return true;
			}
		});

		technologyDao.selectList("select * from zcdh_technology where is_delete=1 or is_delete is null", new Iterator<Technology>() {
			public boolean next(Technology technology, int index) throws Exception {
				if (technology.getTechnologyId() != null)
					technologyIdMap.put(technology.getTechnologyId(), technology);
				if (Ver.nb(technology.getTechnicalCode()))
					technologyCodeMap.put(technology.getTechnicalCode(), technology);
				return true;
			}
		});

		technologyGategoryDao.selectList("select * from zcdh_technology_gategory where is_delete=1 or is_delete is null", new Iterator<TechnologyGategory>() {
			public boolean next(TechnologyGategory technologyGategory, int index) throws Exception {
				if (technologyGategory.getId() != null)
					technologyGategoryIdMap.put(technologyGategory.getId(), technologyGategory);
				if (Ver.nb(technologyGategory.getTechnologyGategoryCode()))
					technologyGategoryCodeMap.put(technologyGategory.getTechnologyGategoryCode(), technologyGategory);
				return true;
			}
		});

		viewEntPostDao.selectAll(new Iterator<ViewEntPost>() {
			public boolean next(ViewEntPost viewEntPost, int index) throws Exception {
				if (viewEntPost.getId() != null)
					viewEntPostIdMap.put(viewEntPost.getId(), viewEntPost);
				if (viewEntPost.getPostId() != null)
					viewEntPostPostIdMap.put(viewEntPost.getPostId(), viewEntPost);
				if (viewEntPost.getEntId() != null)
					viewEntPostEntIdMap.put(viewEntPost.getEntId(), viewEntPost);
				return true;
			}
		});
		logger.info("------------------------");
	}

}
