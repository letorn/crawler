package util;

public class Ver {

	public static boolean isBlank(String str) {
		return str == null || "".equals(str.trim()) ? true : false;
	}

	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	public static boolean eq(Object o1, Object o2) {
		if (o1 == null && o2 == null)
			return true;
		return o1 == null ? false : o1.equals(o2);
	}

	public static boolean nq(Object o1, Object o2) {
		return !eq(o1, o2);
	}

}
