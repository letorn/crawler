package crawler.post;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import map.Cluster;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import crawler.post.model.Bill;
import crawler.post.model.Enterprise;
import crawler.post.model.Post;

public class Collector {

	private static Logger logger = Logger.getLogger(Collector.class);
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private Date date;

	private String id;
	private String nid;

	private String region;
	private String area;
	private Map<String, Object> norm;

	private Map<String, Integer> billUrlIndexes;
	private List<Bill> bills;
	private Map<String, Integer> postUrlIndexes;
	private List<Post> posts;
	private Map<String, Integer> enterpriseUrlIndexes;
	private List<Enterprise> enterprises;
	private Map<String, Set<String>> enterpriseRelevances;
	private Map<String, String> postRelevances;

	private Explorer explorer;
	private Processor processor;

	private Cluster<Post> postCluster;

	public Collector(String region, String area, Map<String, Object> norm) {
		date = new Date();
		nid = (String) norm.get("nid");
		id = StringUtils.isBlank(area) ? String.format("%s.%s", nid, region) : String.format("%s.%s.%s", nid, region, area);

		this.region = region;
		this.area = area;
		this.norm = norm;

		billUrlIndexes = new HashMap<String, Integer>();
		bills = new ArrayList<Bill>();
		postUrlIndexes = new HashMap<String, Integer>();
		posts = new ArrayList<Post>();
		enterpriseUrlIndexes = new HashMap<String, Integer>();
		enterprises = new ArrayList<Enterprise>();
		enterpriseRelevances = new HashMap<String, Set<String>>();
		postRelevances = new HashMap<String, String>();

		explorer = new Explorer(this);
		processor = new Processor(this);

		postCluster = new Cluster<Post>();
	}

	public Boolean start() {
		logger.info("start collector[id=" + id + ", norm=" + nid + ", region=" + region + ", area=" + area + ", date=" + dateFormat.format(date) + "]");
		return explorer.start() && processor.start();
	}

	public Boolean pause() {
		logger.info("pause collector[id=" + id + ", norm=" + nid + ", region=" + region + ", area=" + area + ", date=" + dateFormat.format(date) + "]");
		return explorer.pause() && processor.pause();
	}

	public Boolean stop() {
		logger.info("stop collector[id=" + id + ", norm=" + nid + ", region=" + region + ", area=" + area + ", date=" + dateFormat.format(date) + "]");
		date = new Date();
		return explorer.stop() && processor.stop();
	}

	public Boolean clear() {
		logger.info("clear collector[id=" + id + ", norm=" + nid + ", region=" + region + ", area=" + area + ", date=" + dateFormat.format(date) + "]");
		explorer.stop();
		processor.stop();
		clearBill();
		clearPost();
		clearEnterprise();
		return false;
	}

	public Boolean saveBill(Bill bill) {
		String url = bill.getPostUrl();
		if (!billUrlIndexes.containsKey(url)) {
			if (bills.add(bill)) {
				billUrlIndexes.put(url, bills.size() - 1);
				return true;
			}
		}
		return false;
	}

	public List<Bill> findBill(Integer start) {
		List<Bill> list = new ArrayList<Bill>();
		Integer last = bills.size() - 1;
		if (start <= last)
			for (int i = start; i <= last; i++)
				list.add(bills.get(i));
		return list;
	}

	public List<Bill> findBill(Integer start, Integer limit) {
		List<Bill> list = new ArrayList<Bill>();
		Integer last = bills.size() - 1;
		if (start <= last) {
			Integer end = start + limit - 1;
			if (end > last) {
				end = last;
			}
			for (int i = start; i <= end; i++) {
				list.add(bills.get(i));
			}
		}
		return list;
	}

	public Integer billSize() {
		return bills.size();
	}

	public Integer[] billSizes() {
		Integer raw = 0;
		Integer ignored = 0;
		Integer processed = 0;
		for (int i = 0; i < bills.size(); i++) {
			Bill bill = bills.get(i);
			if (bill.getStatus() == 0) {
				raw++;
			} else if (bill.getStatus() == 1) {
				ignored++;
			} else if (bill.getStatus() == 2) {
				processed++;
			}
		}
		return new Integer[] { raw, ignored, processed };
	}

	public Boolean clearBill() {
		if (billUrlIndexes != null)
			billUrlIndexes.clear();
		if (bills != null)
			bills.clear();
		return true;
	}

	public Boolean savePost(Post post) {
		String postUrl = post.getDataUrl();
		Integer urlIndex = postUrlIndexes.get(postUrl);
		if (urlIndex != null) {
			posts.set(urlIndex, post);
		} else {
			posts.add(post);
			postUrlIndexes.put(postUrl, posts.size() - 1);

			String enterpriseUrl = post.getEnterpriseUrl();
			// add enterprise -> posts index
			Set<String> enterpriseHeap = enterpriseRelevances.get(enterpriseUrl);
			if (enterpriseHeap == null) {
				enterpriseHeap = new HashSet<String>();
				enterpriseRelevances.put(enterpriseUrl, enterpriseHeap);
			}
			if (!enterpriseHeap.contains(postUrl))
				enterpriseHeap.add(postUrl);

			// add post -> enterprise index
			if (!postRelevances.containsKey(postUrl))
				postRelevances.put(postUrl, enterpriseUrl);
		}
		return true;
	}

	public Boolean existPost(String url) {
		return postUrlIndexes.containsKey(url);
	}

	public Post getPost(String url) {
		return posts.get(postUrlIndexes.get(url));
	}

	public List<Post> getPosts() {
		return posts;
	}

	public List<Post> findPost(Integer status, Integer start, Integer limit) {
		List<Post> list = new ArrayList<Post>();
		Integer last = posts.size() - 1;
		if (start <= last) {
			Integer index = -1;
			for (int i = 0; i <= last; i++) {
				Post post = posts.get(i);
				if (post.getStatus() == status) {
					if ((++index) >= start) {
						list.add(post);
					}
					if (list.size() >= limit) {
						break;
					}
				}
			}
		}
		return list;
	}

	public Integer[] postSizes() {
		Integer failed = 0;
		Integer raw = 0;
		Integer ignored = 0;
		Integer inserted = 0;
		Integer updated = 0;
		for (int i = 0; i < posts.size(); i++) {
			Post post = posts.get(i);
			if (post.getStatus() == -1) {
				failed++;
			} else if (post.getStatus() == 0) {
				raw++;
			} else if (post.getStatus() == 1) {
				ignored++;
			} else if (post.getStatus() == 2) {
				inserted++;
			} else if (post.getStatus() == 3) {
				updated++;
			}
		}
		return new Integer[] { failed, raw, ignored, inserted, updated };
	}

	public Boolean clearPost() {
		if (postUrlIndexes != null)
			postUrlIndexes.clear();
		if (posts != null)
			posts.clear();
		if (enterpriseRelevances != null)
			enterpriseRelevances.clear();
		if (postRelevances != null)
			postRelevances.clear();
		return true;
	}

	public Boolean saveEnterprise(Enterprise enterprise) {
		String entUrl = enterprise.getDataUrl();
		Integer urlIndex = enterpriseUrlIndexes.get(entUrl);
		if (urlIndex != null) {
			enterprises.set(urlIndex, enterprise);
		} else {
			enterprises.add(enterprise);
			enterpriseUrlIndexes.put(entUrl, enterprises.size() - 1);
		}
		return true;
	}

	public Enterprise getEnterprise(String url) {
		return enterprises.get(enterpriseUrlIndexes.get(url));
	}

	public List<Enterprise> getEnterprises() {
		return enterprises;
	}

	public List<Enterprise> findEnterprise(Integer start, Integer limit) {
		List<Enterprise> list = new ArrayList<Enterprise>();
		Integer last = enterprises.size() - 1;
		if (start <= last) {
			Integer end = start + limit - 1;
			if (end > last)
				end = last;
			for (int i = start; i <= end; i++)
				list.add(enterprises.get(i));
		}
		return list;
	}

	public Integer enterpriseSize() {
		return enterprises.size();
	}

	public Boolean clearEnterprise() {
		if (enterpriseUrlIndexes != null)
			enterpriseUrlIndexes.clear();
		if (enterprises != null)
			enterprises.clear();
		return true;
	}

	public Integer getStatus() {
		if (explorer.getStatus() == 0 && processor.getStatus() == 0) {
			return 0;
		} else if (explorer.getStatus() == 1 || processor.getStatus() == 1) {
			return 1;
		} else if (explorer.getStatus() == 2 && processor.getStatus() == 2) {
			return 2;
		} else if (explorer.getStatus() == 3 && processor.getStatus() == 3) {
			return 3;
		} else {
			return -1;
		}
	}

	public Date getDate() {
		return date;
	}

	public String getId() {
		return id;
	}

	public String getNid() {
		return nid;
	}

	public String getRegion() {
		return region;
	}

	public String getArea() {
		return area;
	}

	public Map<String, Object> getNorm() {
		return norm;
	}

	public Explorer getExplorer() {
		return explorer;
	}

	public Processor getProcessor() {
		return processor;
	}

	public Cluster<Post> getPostCluster() {
		return postCluster;
	}
}
