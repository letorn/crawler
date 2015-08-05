package dao.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisStack implements ApplicationContextAware {

	public static RedisTemplate<Object, Object> redisTemplate;

	public static boolean valuePut(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
		return true;
	}

	public static Object valueGet(Object key) {
		return redisTemplate.opsForValue().get(key);
	}

	public static boolean listPut(Object key, Object value) {
		redisTemplate.opsForList().rightPush(key, value);
		return false;
	}

	public static boolean listPutAll(Object key, List<Object> list) {
		redisTemplate.opsForList().rightPushAll(key, list);
		return false;
	}

	public static Object listGet(Object key, Long index) {
		return redisTemplate.opsForList().index(key, index);
	}

	public static boolean setPut(Object key, Object value) {
		redisTemplate.opsForSet().add(key, value);
		return false;
	}

	public static boolean setPutAll(Object key, Set<Object> value) {
		redisTemplate.opsForSet().add(key, value);
		return false;
	}

	public static boolean setContains(Object key, Set<Object> value) {
		return redisTemplate.opsForSet().isMember(key, value);
	}

	public static boolean hashPut(Object key, Object subKey, Object value) {
		redisTemplate.opsForHash().put(key, subKey, value);
		return true;
	}

	public static boolean hashPutAll(Object key, Map<? extends Object, ? extends Object> map) {
		redisTemplate.opsForHash().putAll(key, map);
		return true;
	}

	public static Object hashContains(Object key, Object subKey) {
		return redisTemplate.opsForHash().hasKey(key, subKey);
	}

	public static Object hashGet(Object key, Object subKey) {
		return redisTemplate.opsForHash().get(key, subKey);
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		redisTemplate = applicationContext.getBean(RedisTemplate.class);
	}

}
