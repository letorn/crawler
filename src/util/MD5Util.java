package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

public class MD5Util {

	public static String md5Digest(String msg) {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			// TODO Auto-generated catch block // oSBrriGEzW8KHAL6b9J63w==
			byte[] bts = md.digest(msg.getBytes());// 方一个byte[]进去
			// System.out.println(new String(bts));
			// 采用BASE64算法处理
			BASE64Encoder encoder = new BASE64Encoder();
			return encoder.encode(bts);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println(md5Digest("zcdhjob.com"));
	}
}
