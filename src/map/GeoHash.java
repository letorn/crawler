package map;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

public class GeoHash {

	private static int bitsize = 5;
	private static int bitnum = bitsize * 6;
	private static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	private static Map<Character, Integer> lookup = new HashMap<Character, Integer>();

	static {
		for (int i = 0; i < digits.length; i++)
			lookup.put(digits[i], i);
	}

	public static double[] press(double[] point, int grade) {
		return decode(ArrayUtils.subarray(encode(point), 0, 3 + grade));
	}

	private static char[] encode(double[] point) {
		BitSet lons = bitSet(point[0], -180, 180);
		BitSet lats = bitSet(point[1], -90, 90);
		byte[] bytes = new byte[bitnum * 2];
		for (int i = 0; i < bitnum; i++) {
			if (lons.get(i))
				bytes[i * 2] = 1;
			if (lats.get(i))
				bytes[i * 2 + 1] = 1;
		}
		char[] chars = new char[bytes.length / bitsize];
		for (int i = 0; i < chars.length; i++) {
			int digitIndex = 0;
			for (int j = 0; j < bitsize; j++)
				digitIndex = digitIndex << 1 | bytes[i * bitsize + j] & 0xff;
			chars[i] = digits[digitIndex];
		}
		return chars;
	}

	private static double[] decode(char[] chars) {
		byte[] bytes = new byte[bitnum * 2];
		for (int i = 0; i < chars.length; i++) {
			int digitIndex = lookup.get(chars[i]);
			for (int j = 0; j < bitsize; j++)
				bytes[i * bitsize + j] = (byte) ((digitIndex >> bitsize - j - 1) % 2);
		}
		BitSet lons = new BitSet();
		BitSet lats = new BitSet();
		for (int i = 0, lonCounter = 0, latCounter = 0; i < bitnum * 2; i++) {
			if (i % 2 == 0)
				lons.set(lonCounter++, bytes[i] == 1 ? true : false);
			else
				lats.set(latCounter++, bytes[i] == 1 ? true : false);
		}
		return new double[] { decode(lons, -180, 180), decode(lats, -90, 90) };
	}

	private static double decode(BitSet bits, double floor, double ceiling) {
		double mid = 0;
		for (int i = 0; i < bits.length(); i++) {
			mid = (floor + ceiling) / 2;
			if (bits.get(i))
				floor = mid;
			else
				ceiling = mid;
		}
		return mid;
	}

	private static BitSet bitSet(double coord, double floor, double ceiling) {
		BitSet bits = new BitSet(bitnum);
		for (int i = 0; i < bitnum; i++) {
			double mid = (floor + ceiling) / 2;
			if (coord >= mid) {
				bits.set(i);
				floor = mid;
			} else {
				ceiling = mid;
			}
		}
		return bits;
	}

}
