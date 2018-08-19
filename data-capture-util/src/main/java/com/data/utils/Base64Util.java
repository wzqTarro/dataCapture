package com.data.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * base64转码工具类
 * @author alex
 *
 */
public class Base64Util {
	
	private static final Base64.Decoder decoder = Base64.getDecoder();
	private static final Base64.Encoder encoder = Base64.getEncoder();
	
	private static final String CHARSET = "UTF-8";
	
	/**
	 * 64转码
	 * @param content
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encode(String content) throws UnsupportedEncodingException {
		byte[] contentByte = content.getBytes(CHARSET); 
		return encoder.encodeToString(contentByte);
	}
	
	/**
	 * 64解码
	 * @param encodeStr
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String decode(String encodeStr) throws UnsupportedEncodingException {
		byte[] decodeByte = decoder.decode(encodeStr);
		return new String(decodeByte, CHARSET);
	}
}
