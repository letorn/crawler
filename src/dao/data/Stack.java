package dao.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import model.Area;
import model.Industry;
import model.Param;
import model.Post;
import model.Tag;
import model.Technology;
import model.TechnologyGategory;
import dao.AreaDao;
import dao.IndustryDao;
import dao.ParamDao;
import dao.PostDao;
import dao.TagDao;
import dao.TechnologyDao;
import dao.TechnologyGategoryDao;
import dao.data.C3P0Store.Iterator;

public class Stack {

	@Resource
	private AreaDao areaDao;
	@Resource
	private ParamDao paramDao;
	@Resource
	private TagDao tagDao;
	@Resource
	private PostDao postDao;
	@Resource
	private TechnologyDao technologyDao;
	@Resource
	private TechnologyGategoryDao technologyGategoryDao;
	@Resource
	private IndustryDao industryDao;

	public static Map<String, Area> areas = new ConcurrentHashMap<String, Area>();
	public static Map<String, Param> params = new ConcurrentHashMap<String, Param>();
	public static Map<String, Tag> tags = new ConcurrentHashMap<String, Tag>();
	public static Map<String, Post> posts = new ConcurrentHashMap<String, Post>();
	public static Map<String, Technology> technologies = new ConcurrentHashMap<String, Technology>();
	public static Map<String, TechnologyGategory> technologyGategories = new ConcurrentHashMap<String, TechnologyGategory>();
	public static Map<String, Industry> industries = new ConcurrentHashMap<String, Industry>();

	public void init() {
		areaDao.selectList("select * from zcdh_area where is_delete=1 or is_delete is null", new Iterator<Area>() {
			public boolean next(Area t, int i) throws Exception {
				if (t.getAreaCode() != null)
					areas.put(t.getAreaCode(), t);
				return true;
			}
		});

		paramDao.selectList("select * from zcdh_param where is_delete=1 or is_delete is null", new Iterator<Param>() {
			public boolean next(Param t, int i) throws Exception {
				if (t.getParamCode() != null)
					params.put(t.getParamCode(), t);
				return true;
			}
		});

		tagDao.selectList("select * from zcdh_tag where is_delete=1 or is_delete is null", new Iterator<Tag>() {
			public boolean next(Tag t, int i) throws Exception {
				if (t.getTagCode() != null)
					tags.put(t.getTagCode(), t);
				return true;
			}
		});

		postDao.selectList("select * from zcdh_post where is_delete=1 or is_delete is null", new Iterator<Post>() {
			public boolean next(Post t, int i) throws Exception {
				if (t.getPostCode() != null)
					posts.put(t.getPostCode(), t);
				return true;
			}
		});

		technologyDao.selectList("select * from zcdh_technology where is_delete=1 or is_delete is null", new Iterator<Technology>() {
			public boolean next(Technology t, int i) throws Exception {
				if (t.getTechnicalCode() != null)
					technologies.put(t.getTechnicalCode(), t);
				return true;
			}
		});

		technologyGategoryDao.selectList("select * from zcdh_technology_gategory where is_delete=1 or is_delete is null", new Iterator<TechnologyGategory>() {
			public boolean next(TechnologyGategory t, int i) throws Exception {
				if (t.getCode() != null)
					technologyGategories.put(t.getCode(), t);
				return true;
			}
		});

		industryDao.selectList("select * from zcdh_industry where is_delete=1 or is_delete is null", new Iterator<Industry>() {
			public boolean next(Industry t, int i) throws Exception {
				if (t.getIndustryCode() != null)
					industries.put(t.getIndustryCode(), t);
				return true;
			}
		});
	}

}
