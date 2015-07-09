/**
 * 
 * @author focus, 2015-1-28 上午11:30:51
 */
package util;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;

/**
 * geohash 算法
 * @author focus, 2015-1-28 上午11:30:51
 */
public class GeoHashFactory {

	private static int numbits = 6 * 5;
	final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	final static char[] digits1 = { 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't' };

	final static HashMap<Character, Integer> lookup = new HashMap<Character, Integer>();
	static {
		int i = 0;
		for (char c : digits)
			lookup.put(c, i++);
	}

	public static void main(String[] args) throws Exception {
		// webwr9yfvvy3
		String lbs = "113.54224,22.202499";
		// webwr9y6x613
		lbs = "113.541611,22.202436";
		// webwr6sutk2v
		lbs = "113.528478,22.206912";
		// wkgedq1xv8yc
		lbs = "106.273057,27.368278";
		// wxx9fe22d5yq
		lbs = "123.157176,42.513077";
		// txn57xk4606t
		lbs = "76.096891,39.986503";
		// 福州 wssv9u2q2647
		lbs = "119.256949,26.303727";
		// 杭州 wtmks5jxnx5h
		lbs = "120.06643,30.339985";
		// 南昌 wt47q2kyhmpm
		lbs = "115.945436,28.69872";

		// 最北 y9x92cfez6wr
		lbs = "123.083587,53.667498";
		// 最南 w7np8ndhdt2f
		lbs = "109.690355,18.229866";
		// 最西 txh29f5r5j5g
		lbs = "73.558064,39.475106";
		// 最东 ybxu54ffvjdx
		lbs = "134.784268,48.531234";

		// webxxqpyqfz2
		lbs = "113.532671,22.446156";
		print(lbs);
		// webxxqpyrmht
		lbs = "113.532689,22.446169";
		print(lbs);
		// 测试俩个最近的点的距离

	}

	static GeoHashFactory geo = new GeoHashFactory();

	private static void print(String lbs) {

		double arr[] = parse2lbs(lbs);
		double lon = arr[0];
		double lat = arr[1];

		geo.encode8(lat, lon);
		String geohash = geo.encode32(lat, lon);

		System.out.println(lbs);
		System.out.println(Arrays.toString(geo.decode32(geohash)));
	}

	private static double[] parse2lbs(String lbsStr) {
		String[] data = lbsStr.split(",");
		double[] arr = { Double.valueOf(data[0]), Double.valueOf(data[1]) };
		return arr;
	}

	public static double[] decode32(String geohash) {
		StringBuilder buffer = new StringBuilder();
		for (char c : geohash.toCharArray()) {

			int i = lookup.get(c) + 32;
			buffer.append(Integer.toString(i, 2).substring(1));
		}

		BitSet lonset = new BitSet();
		BitSet latset = new BitSet();

		// even bits
		int j = 0;
		for (int i = 0; i < numbits * 2; i += 2) {
			boolean isSet = false;
			if (i < buffer.length())
				isSet = buffer.charAt(i) == '1';
			lonset.set(j++, isSet);
		}

		// odd bits
		j = 0;
		for (int i = 1; i < numbits * 2; i += 2) {
			boolean isSet = false;
			if (i < buffer.length())
				isSet = buffer.charAt(i) == '1';
			latset.set(j++, isSet);
		}

		double lon = decode(lonset, -180, 180);
		double lat = decode(latset, -90, 90);

		return new double[] { lat, lon };
	}

	private static double decode(BitSet bs, double floor, double ceiling) {
		double mid = 0;
		for (int i = 0; i < bs.length(); i++) {
			mid = (floor + ceiling) / 2;
			if (bs.get(i))
				floor = mid;
			else
				ceiling = mid;
		}
		return mid;
	}

	public static String encode32(double lat, double lon) {
		StringBuilder buffer = encodeBitStr(lat, lon);
		String base32Str = base32(Long.parseLong(buffer.toString(), 2));
		System.out.println("base32 : " + base32Str);
		return base32Str;
	}

	public static String encode8(double lat, double lon) {
		StringBuilder buffer = encodeBitStr(lat, lon);
		String base8Str = base8(buffer);
		System.out.println("base8 : " + base8Str);
		return base8Str;
	}

	private static StringBuilder encodeBitStr(double lat, double lon) {
		BitSet latbits = getBits(lat, -90, 90);
		BitSet lonbits = getBits(lon, -180, 180);
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < numbits; i++) {
			buffer.append((lonbits.get(i)) ? '1' : '0');
			buffer.append((latbits.get(i)) ? '1' : '0');
		}
		return buffer;
	}

	private static BitSet getBits(double lat, double floor, double ceiling) {
		BitSet buffer = new BitSet(numbits);
		for (int i = 0; i < numbits; i++) {
			double mid = (floor + ceiling) / 2;
			if (lat >= mid) {
				buffer.set(i);
				floor = mid;
			} else {
				ceiling = mid;
			}
		}
		return buffer;
	}

	// 截取字符串的长度
	private static final int scale = 3;

	private static String base8(StringBuilder str) {

		if (str == null)
			return "";
		StringBuilder result = new StringBuilder();
		for (int i = 0, len = str.length(); i < len; i += scale) {
			if (i <= len - scale)
				result.append(digits1[Integer.parseInt(str.substring(i, i + scale), 2)]);
			else
				result.append(digits1[Integer.parseInt(str.substring(i, len), 2)]);
		}

		return result.toString();
	}

	public static String base32(long i) {
		char[] buf = new char[65];
		int charPos = 64;
		boolean negative = (i < 0);
		if (!negative)
			i = -i;
		while (i <= -32) {
			buf[charPos--] = digits[(int) (-(i % 32))];
			i /= 32;
		}
		buf[charPos] = digits[(int) (-i)];

		if (negative)
			buf[--charPos] = '-';
		return new String(buf, charPos, (65 - charPos));
	}
}
