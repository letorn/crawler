package util;

public class Ver {

	public static boolean nu(Object str) {// null
		return str == null;
	}

	public static boolean nn(Object str) {// not null
		return !nu(str);
	}

	public static boolean bl(String str) {// blank
		return str == null || str.trim().length() == 0 ? true : false;
	}

	public static boolean nb(String str) {// not blank
		return !bl(str);
	}

	public static boolean po(Integer num) {// > 0
		return num != null && num > 0 ? true : false;
	}

	public static boolean pz(Integer num) {// >= 0
		return num != null && num >= 0 ? true : false;
	}

	public static boolean ne(Integer num) {// < 0
		return num != null && num < 0 ? true : false;
	}

	public static boolean nz(Integer num) {// <= 0
		return num != null && num <= 0 ? true : false;
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
