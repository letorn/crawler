package util;

import java.text.DecimalFormat;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Base64Utils;

public class GeoHash {

	private static DecimalFormat decimalFormat = new DecimalFormat("0.000000");
	private static int numbits = 6 * 5;
	private static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	final static char[] digits1 = { 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't' };
	private static Map<Character, Integer> lookup = new HashMap<Character, Integer>();

	static {
		for (int i = 0; i < digits.length; i++)
			lookup.put(digits[i], i);
	}

	public static void main(String[] args) {
		Double[] lonlat = GeoHash.decode("webwr9yfvvy3");// 113.54224,22.202499 (113.542240, 22.202500)
		System.out.println(String.format("(%f, %f)", lonlat[0]*1000000, lonlat[1]));

		Double[] lonlat2 = GeoHash.decode("webw");
		System.out.println(String.format("(%f, %f)", lonlat2[0]*1000000, lonlat2[1]));

		Double[] lonlat3 = GeoHash.decode("webwr");
		System.out.println(String.format("(%f, %f)", lonlat3[0]*1000000, lonlat3[1]));
	}

	public static String encode(Double lon, Double lat) {
		return encode32(lon, lat);
	}
	
	public static String encode32(Double lon, Double lat) {
		BitSet lons = bitSet(lon, -180, 180);
		BitSet lats = bitSet(lat, -90, 90);
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < numbits; i++) {
			buffer.append((lons.get(i)) ? '1' : '0');
			buffer.append((lats.get(i)) ? '1' : '0');
		}
		return base32(buffer.toString());
	}

	public static Double[] decode(String geohash, int zoom) {
		return decode(geohash.substring(0, zoom + 3));
	}

	private static Double[] decode(String geohash) {
		StringBuffer buffer = new StringBuffer();
		for (char digit : geohash.toCharArray())
			buffer.append(Integer.toString(lookup.get(digit) + 32, 2).substring(1));
		BitSet lons = new BitSet();
		BitSet lats = new BitSet();
		for (int i = 0, lonCounter = 0, latCounter = 0; i < numbits * 2; i++)
			if (i % 2 == 0)
				lons.set(lonCounter++, i < buffer.length() && buffer.charAt(i) == '1' ? true : false);
			else
				lats.set(latCounter++, i < buffer.length() && buffer.charAt(i) == '1' ? true : false);
		double lon = decode(lons, -180, 180);
		double lat = decode(lats, -90, 90);
		return new Double[] { Double.parseDouble(decimalFormat.format(lon)), Double.parseDouble(decimalFormat.format(lat)) };
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

	private static String base8(long l) {
		char[] buf = new char[65];
		int charPos = 64;
		boolean negative = (l < 0);
		if (!negative)
			l = -l;
		while (l <= -32) {
			buf[charPos--] = digits[(int) (-(l % 32))];
			l /= 32;
		}
		buf[charPos] = digits[(int) (-l)];
		if (negative)
			buf[--charPos] = '-';
		return new String(buf, charPos, (65 - charPos));
	}
	
	private static String base32(String code) {
		long l = Long.parseLong(code, 2);
		char[] buf = new char[65];
		int charPos = 64;
		boolean negative = (l < 0);
		if (!negative)
			l = -l;
		while (l <= -32) {
			buf[charPos--] = digits[(int) (-(l % 32))];
			l /= 32;
		}
		buf[charPos] = digits[(int) (-l)];
		if (negative)
			buf[--charPos] = '-';
		return new String(buf, charPos, (65 - charPos));
	}

	private static BitSet bitSet(double lat, double floor, double ceiling) {
		BitSet bits = new BitSet(numbits);
		for (int i = 0; i < numbits; i++) {
			double mid = (floor + ceiling) / 2;
			if (lat >= mid) {
				bits.set(i);
				floor = mid;
			} else {
				ceiling = mid;
			}
		}
		return bits;
	}

}
