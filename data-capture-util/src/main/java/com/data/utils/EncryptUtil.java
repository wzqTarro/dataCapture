package com.data.utils;

import java.security.MessageDigest;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;

/**
 * 密文工具类
 * @author Alex
 *
 */
public class EncryptUtil {
	
	public static final String[] CODE = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
			"q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z"};

	/**
	 * MD5加密
	 * @param password
	 * @return
	 */
	public static String Md5Encrypt(String password) {
		Random r = new Random();
 		StringBuilder sb = new StringBuilder(16);
 		sb.append(r.nextInt(99999999)).append(r.nextInt(99999999));
// 		for(int i = 1; i <= 26 * 2 + 10; i++) {
// 			sb.append(CODE[r.nextInt(i)]); 			
// 		}
 		int len = sb.length();
 		if (len < 16) {
 			for (int i = 0; i < 16 - len; i++) {
 				sb.append("0");
 			}
 		}
 		String salt = sb.toString();
 		password = md5Hex(password + salt);
 		char[] cs = new char[48];
 		for (int i = 0; i < 48; i += 3) {
 			cs[i] = password.charAt(i / 3 * 2);
 			char c = salt.charAt(i / 3);
 			cs[i + 1] = c;
 			cs[i + 2] = password.charAt(i / 3 * 2 + 1);
 		}
		return new String(cs);
	}
	
	/**
	 * 校验加盐后是否和原文一致
	 * @author daniel
	 * @time 2016-6-11 下午8:45:39
	 * @param password
	 * @param md5
	 * @return
	 */
	public static boolean verify(String password, String md5) {
 		char[] cs1 = new char[32];
		char[] cs2 = new char[16];
		for (int i = 0; i < 48; i += 3) {
			cs1[i / 3 * 2] = md5.charAt(i);
			cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
			cs2[i / 3] = md5.charAt(i + 1);
		}
		String salt = new String(cs2);
		return md5Hex(password + salt).equals(new String(cs1));
	}

	
	/**
	 * 获取十六进制字符串形式的MD5摘要
	 */
	private static String md5Hex(String src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] bs = md5.digest(src.getBytes());
			return new String(new Hex().encode(bs));
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void main(String[] args) {
		String password = Md5Encrypt("123");
		System.err.println(password);
		System.err.println("check: " + verify("123", password));
	}

}
