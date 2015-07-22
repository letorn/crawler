package util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.ListOrderedMap;

public class MyTest {

	public static void main(String[] args) {
		LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
		map.put("a", 1);
		map.put("c", 1);
		map.put("f", 1);
		map.put("b", 1);
		map.put("g", 1);
		map.put("a", 4);
		map.put("c", 2);
		for(String key : map.keySet())
			System.out.println(key + ": " + map.get(key));
		map.values();
		List<Integer> list = new ArrayList<Integer>(map.values());
		LinkedMap aa = new LinkedMap();
		ListOrderedMap j = new ListOrderedMap();
	}
	
}
