package crawler.post;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import crawler.post.model.Bill;
import crawler.post.model.Enterprise;
import crawler.post.model.Post;

public class Heap {

	private static Map<String, Map<String, Object>> billNorms = new ConcurrentHashMap<String, Map<String, Object>>();
	private static Map<String, Map<String, Object>> postNorms = new ConcurrentHashMap<String, Map<String, Object>>();
	private static Map<String, Map<String, Object>> enterpriseNorms = new ConcurrentHashMap<String, Map<String, Object>>();

	private static Map<String, Collector> collectors = new ConcurrentHashMap<String, Collector>();

	private static Map<String, Map<String, Integer>> billURLIndexes = new ConcurrentHashMap<String, Map<String, Integer>>();
	private static Map<String, List<Bill>> bills = new ConcurrentHashMap<String, List<Bill>>();

	private static Map<String, Map<String, Integer>> postURLIndexes = new ConcurrentHashMap<String, Map<String, Integer>>();
	private static Map<String, List<Post>> posts = new ConcurrentHashMap<String, List<Post>>();

	private static Map<String, Map<String, Integer>> enterpriseURLIndexes = new ConcurrentHashMap<String, Map<String, Integer>>();
	private static Map<String, List<Enterprise>> enterprises = new ConcurrentHashMap<String, List<Enterprise>>();

	private static Map<String, Map<String, Set<String>>> enterpriseRelevances = new ConcurrentHashMap<String, Map<String, Set<String>>>();
	private static Map<String, Map<String, String>> postRelevances = new ConcurrentHashMap<String, Map<String, String>>();

	static {
		String webappRoot = System.getProperty("webapp.root");
		File normDir = new File(webappRoot, "platform/norm/post");
		File[] normFiles = normDir.listFiles();
		for (File normFile : normFiles) {
			String name = normFile.getName();
			Map<String, Object> norm = loadNorm(normFile);
			billNorms.put(name, (Map<String, Object>) norm.get("bill"));
			postNorms.put(name, (Map<String, Object>) norm.get("post"));
			enterpriseNorms.put(name, (Map<String, Object>) norm.get("enterprise"));
		}
	}

	public static Boolean checkCollector(String cid) {
		for (String id : collectors.keySet()) {
			if (cid.contains(id)) {
				return false;
			}
		}
		return true;
	}

	public static Boolean existCollector(String cid) {
		return collectors.keySet().contains(cid);
	}

	public static Boolean saveCollector(Collector collector) {
		String cid = collector.getId();
		if (checkCollector(cid)) {
			collectors.put(cid, collector);
			return true;
		}
		return false;
	}

	public static Collector getCollector(String cid) {
		return collectors.get(cid);
	}

	public static Map<String, Collector> getCollectors() {
		return collectors;
	}

	public static Collector removeCollector(String cid) {
		Collector collector = collectors.remove(cid);
		if (0 != collector.getStatus()) {
			collector.stop();
		}
		clearBills(cid);
		clearPosts(cid);
		clearEnterprises(cid);
		return collector;
	}

	public static Map<String, Object> getBillNorm(String name) {
		return billNorms.get(name);
	}

	public static Map<String, Map<String, Object>> getBillNorms() {
		return billNorms;
	}

	public static Map<String, Object> getPostNorm(String name) {
		return postNorms.get(name);
	}

	public static Map<String, Object> getEnterpriseNorm(String name) {
		return enterpriseNorms.get(name);
	}

	public static Bill getBill(String cid, String url) {
		Map<String, Integer> urlIndexes = billURLIndexes.get(cid);
		List<Bill> stack = bills.get(cid);
		return stack.get(urlIndexes.get(url));
	}

	public static List<Bill> findBill(String cid, Integer start) {
		List<Bill> list = new ArrayList<Bill>();
		List<Bill> stack = bills.get(cid);
		if (null != stack) {
			Integer last = stack.size() - 1;
			if (start <= last) {
				for (int i = start; i <= last; i++) {
					list.add(stack.get(i));
				}
			}
		}
		return list;
	}

	public static List<Bill> findBill(String cid, Integer start, Integer limit) {
		List<Bill> list = new ArrayList<Bill>();
		List<Bill> stack = bills.get(cid);
		if (null != stack) {
			Integer last = stack.size() - 1;
			if (start <= last) {
				Integer end = start + limit - 1;
				if (end > last) {
					end = last;
				}
				for (int i = start; i <= end; i++) {
					list.add(stack.get(i));
				}
			}
		}
		return list;
	}

	public static Integer billSize(String cid) {
		List<Bill> stack = bills.get(cid);
		if (null != stack) {
			return stack.size();
		}
		return 0;
	}

	public static Integer[] billSizeGroupByStatus(String cid) {
		Integer raw = 0;
		Integer ignored = 0;
		Integer processed = 0;
		List<Bill> stack = bills.get(cid);
		if (null != stack) {
			for (int i = 0; i <= stack.size() - 1; i++) {
				Integer status = stack.get(i).getStatus();
				if (0 == status) {
					raw++;
				} else if (1 == status) {
					ignored++;
				} else if (2 == status) {
					processed++;
				}
			}
		}
		return new Integer[] { raw, ignored, processed };
	}

	public static Boolean saveBill(String cid, Bill bill) {
		List<Bill> stack = bills.get(cid);
		Map<String, Integer> urlIndexes = billURLIndexes.get(cid);
		if (null == stack) {
			stack = new ArrayList<Bill>();
			urlIndexes = new HashMap<String, Integer>();
			bills.put(cid, stack);
			billURLIndexes.put(cid, urlIndexes);
		}
		String url = bill.getPostURL();
		if (!urlIndexes.containsKey(url)) {
			if (stack.add(bill)) {
				urlIndexes.put(url, stack.size() - 1);
				return true;
			}
		}
		return false;
	}

	public static void clearBills(String cid) {
		List<Bill> stack = bills.get(cid);
		Map<String, Integer> urlIndexes = billURLIndexes.get(cid);
		if (null != stack) {
			stack.clear();
		}
		if (null != urlIndexes) {
			urlIndexes.clear();
		}
	}

	public static Boolean existPost(String cid, String url) {
		Map<String, Integer> urlIndexes = postURLIndexes.get(cid);
		if (null == urlIndexes) {
			return false;
		} else {
			return urlIndexes.containsKey(url);
		}
	}

	public static Post getPost(String cid, String url) {
		Map<String, Integer> urlIndexes = postURLIndexes.get(cid);
		List<Post> stack = posts.get(cid);
		return stack.get(urlIndexes.get(url));
	}

	public static List<Post> findPost(String cid, Integer start, Integer limit) {
		List<Post> list = new ArrayList<Post>();
		List<Post> stack = posts.get(cid);
		if (null != stack) {
			Integer last = stack.size() - 1;
			if (start <= last) {
				Integer end = start + limit - 1;
				if (end > last) {
					end = last;
				}
				for (int i = start; i <= end; i++) {
					list.add(stack.get(i));
				}
			}
		}
		return list;
	}

	public static List<Post> findPost(String cid, Integer status, Integer start, Integer limit) {
		List<Post> list = new ArrayList<Post>();
		List<Post> stack = posts.get(cid);
		if (null != stack) {
			Integer last = stack.size() - 1;
			if (start <= last) {
				Integer index = -1;
				for (int i = 0; i <= last; i++) {
					Post post = stack.get(i);
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
		}
		return list;
	}

	public static List<Post> findPost(String cid, Integer[] statuses, Integer start, Integer limit) {
		List<Post> list = new ArrayList<Post>();
		List<Post> stack = posts.get(cid);
		if (null != stack) {
			Integer last = stack.size() - 1;
			if (start <= last) {
				Integer index = -1;
				for (int i = 0; i <= last; i++) {
					Post post = stack.get(i);
					Boolean isContain = false;
					for (Integer status : statuses) {
						if (post.getStatus() == status) {
							isContain = true;
							break;
						}
					}
					if (isContain) {
						if ((++index) >= start) {
							list.add(post);
						}
						if (list.size() >= limit) {
							break;
						}
					}
				}
			}
		}
		return list;
	}

	public static Integer postSize(String cid) {
		List<Post> stack = posts.get(cid);
		if (null != stack) {
			return stack.size();
		}
		return 0;
	}

	public static Integer[] postSizeGroupByStatus(String cid) {
		Integer raw = 0;
		Integer ignored = 0;
		Integer inserted = 0;
		Integer updated = 0;
		List<Post> stack = posts.get(cid);
		if (null != stack) {
			for (int i = 0; i <= stack.size() - 1; i++) {
				Integer status = stack.get(i).getStatus();
				if (0 == status) {
					raw++;
				} else if (1 == status) {
					ignored++;
				} else if (2 == status) {
					inserted++;
				} else if (3 == status) {
					updated++;
				}
			}
		}
		return new Integer[] { raw, ignored, inserted, updated };
	}

	public static Boolean savePost(String cid, Post post) {
		List<Post> stack = posts.get(cid);
		Map<String, Integer> urlIndexes = postURLIndexes.get(cid);
		if (null == stack) {
			stack = new ArrayList<Post>();
			urlIndexes = new HashMap<String, Integer>();
			posts.put(cid, stack);
			postURLIndexes.put(cid, urlIndexes);
		}
		String url = post.getURL();
		String enterpriseURL = post.getEnterpriseURL();
		if (!urlIndexes.containsKey(url)) {
			if (stack.add(post)) {
				urlIndexes.put(url, stack.size() - 1);
				saveRelevance(cid, enterpriseURL, url);
				return true;
			}
		}
		return false;
	}

	public static void clearPosts(String cid) {
		List<Post> stack = posts.get(cid);
		Map<String, Integer> urlIndexes = postURLIndexes.get(cid);
		if (null != stack) {
			stack.clear();
		}
		if (null != urlIndexes) {
			urlIndexes.clear();
		}
		clearRelevances(cid);
	}

	public static Enterprise getEnterprise(String cid, String url) {
		Map<String, Integer> urlIndexes = enterpriseURLIndexes.get(cid);
		List<Enterprise> stack = enterprises.get(cid);
		return stack.get(urlIndexes.get(url));
	}

	public static List<Enterprise> findEnterprise(String cid, Integer start, Integer limit) {
		List<Enterprise> list = new ArrayList<Enterprise>();
		List<Enterprise> stack = enterprises.get(cid);
		if (null != stack) {
			Integer last = stack.size() - 1;
			if (start <= last) {
				Integer end = start + limit - 1;
				if (end > last) {
					end = last;
				}
				for (int i = start; i <= end; i++) {
					list.add(stack.get(i));
				}
			}
		}
		return list;
	}

	public static Integer enterpriseSize(String cid) {
		List<Enterprise> stack = enterprises.get(cid);
		if (null != stack) {
			return stack.size();
		}
		return 0;
	}

	public static Boolean saveEnterprise(String cid, Enterprise enterprise) {
		List<Enterprise> stack = enterprises.get(cid);
		Map<String, Integer> urlIndexes = enterpriseURLIndexes.get(cid);
		if (null == stack) {
			stack = new ArrayList<Enterprise>();
			urlIndexes = new HashMap<String, Integer>();
			enterprises.put(cid, stack);
			enterpriseURLIndexes.put(cid, urlIndexes);
		}
		String url = enterprise.getURL();
		if (!urlIndexes.containsKey(url)) {
			if (stack.add(enterprise)) {
				urlIndexes.put(url, stack.size() - 1);
				return true;
			}
		}
		return false;
	}

	public static void clearEnterprises(String cid) {
		List<Enterprise> stack = enterprises.get(cid);
		Map<String, Integer> urlIndexes = enterpriseURLIndexes.get(cid);
		if (null != stack) {
			stack.clear();
		}
		if (null != urlIndexes) {
			urlIndexes.clear();
		}
	}

	public static void saveRelevance(String cid, String enterpriseURL, String postURL) {
		Map<String, Set<String>> enterpriseStack = enterpriseRelevances.get(cid);
		Map<String, String> postStack = postRelevances.get(cid);

		// add enterprise -> posts index
		if (null == enterpriseStack) {
			enterpriseStack = new HashMap<String, Set<String>>();
			enterpriseRelevances.put(cid, enterpriseStack);
		}

		Set<String> enterpriseHeap = enterpriseStack.get(enterpriseURL);
		if (null == enterpriseHeap) {
			enterpriseHeap = new HashSet<String>();
			enterpriseStack.put(enterpriseURL, enterpriseHeap);
		}

		if (!enterpriseHeap.contains(postURL)) {
			enterpriseHeap.add(postURL);
		}

		// add post -> enterprise index
		if (null == postStack) {
			postStack = new HashMap<String, String>();
			postRelevances.put(cid, postStack);
		}

		if (!postStack.containsKey(postURL)) {
			postStack.put(postURL, enterpriseURL);
		}
	}

	public static void clearRelevances(String cid) {
		Map<String, Set<String>> enterpriseStack = enterpriseRelevances.get(cid);
		Map<String, String> postStack = postRelevances.get(cid);
		if (null != enterpriseStack) {
			enterpriseStack.clear();
		}
		if (null != postStack) {
			postStack.clear();
		}
	}

	private static Map<String, Object> loadNorm(File file) {
		StringBuffer fileBuffer = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			for (String line = br.readLine(); null != line; line = br.readLine()) {
				fileBuffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(true);
		return (Map<String, Object>) JSONObject.fromObject(fileBuffer.toString(), jsonConfig);
	}

}
