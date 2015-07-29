package util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.SerializationUtils;

import crawler.post.model.Post;

public class MyTest {

	public static void main(String[] args) {
		Map<String, Post> map = new HashMap<String, Post>();
		for (int i = 0; i < 1000000; i++) {
			Post post = new Post();
			post.setId(Long.valueOf(i));
			post.setName(new String("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
			map.put(String.valueOf(i), post);
		}
		long startTime = System.currentTimeMillis();
		SerializationUtils.serialize(map);
		long endTime = System.currentTimeMillis();
		long time = (endTime - startTime);
		System.out.println(time);
	}

}
