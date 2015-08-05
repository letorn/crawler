package dao.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocalStack {

	private static Map<Object, Object> valueMap = new HashMap<Object, Object>();
	private static Map<Object, List<Object>> listMap = new HashMap<Object, List<Object>>();
	private static Map<Object, Set<Object>> setMap = new HashMap<Object, Set<Object>>();
	private static Map<Object, Map<Object, Object>> hashMap = new HashMap<Object, Map<Object, Object>>();

	public static boolean valuePut(String key, Object value) {
		valueMap.put(key, value);
		return true;
	}

	public static Object valueGet(Object key) {
		return valueMap.get(key);
	}

	public static boolean listPut(Object key, Object value) {
		List<Object> list = listMap.get(key);
		if (list == null) {
			list = new ArrayList<Object>();
			listMap.put(key, list);
		}
		list.add(value);
		return false;
	}

	public static boolean listPutAll(Object key, List<Object> list) {
		listMap.put(key, list);
		return false;
	}

	public static Object listGet(Object key, Long index) {
		List<Object> list = listMap.get(key);
		if (list != null)
			return list.get(index.intValue());
		return null;
	}

	public static boolean setPut(Object key, Object value) {
		Set<Object> set = setMap.get(key);
		if (set == null) {
			set = new HashSet<Object>();
			setMap.put(key, set);
		}
		set.add(value);
		return false;
	}

	public static boolean setPutAll(Object key, Set<Object> value) {
		setMap.put(key, value);
		return false;
	}

	public static boolean setContains(Object key, Set<Object> value) {
		Set<Object> set = setMap.get(key);
		if (set != null)
			return set.contains(value);
		return false;
	}

	public static boolean hashPut(Object key, Object subKey, Object value) {
		Map<Object, Object> map = hashMap.get(key);
		if (map == null) {
			map = new HashMap<Object, Object>();
			hashMap.put(key, map);
		}
		map.put(subKey, value);
		return true;
	}

	public static boolean hashPutAll(Object key, Map<Object, Object> map) {
		hashMap.put(key, map);
		return true;
	}

	public static Object hashContains(Object key, Object subKey) {
		Map<Object, Object> map = hashMap.get(key);
		if (map != null)
			return map.containsKey(subKey);
		return false;
	}

	public static Object hashGet(Object key, Object subKey) {
		Map<Object, Object> map = hashMap.get(key);
		if (map != null)
			return map.get(subKey);
		return null;
	}

}
