package com.data.utils;

import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import com.data.constant.CommonValue;
import com.data.exception.DataException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * jwt工具类
 * @author Alex
 *
 */
public class JwtUtil {
	
	private static Base64.Decoder DECODER = Base64.getDecoder();
	
	private static final int EXPIRE_DATE = 60 * 60 * 300 * 1000; 
	
	/**
	 * 生成jwt
	 * @param userId
	 * @param secret
	 * @return
	 */
	public static String createJwt(String userId, String secret) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CommonValue.USER_ID, userId);
		return createJwt(SignatureAlgorithm.HS256, EXPIRE_DATE, secret, claims);
	}
	
	/**
	 * 生成jwt
	 * @param issure
	 * @param ttl
	 * @param claims
	 * @return
	 */
	public static String createJwt(Map<String, Object> claims, String secret, long ttl) {
		return createJwt(SignatureAlgorithm.HS256, ttl, secret, claims);
	}
	
	/**
	 * 生成jwt
	 * @param claims
	 * @return
	 */
	private static String createJwt(SignatureAlgorithm algorithm, long ttl, String secret, Map<String, Object> claims) {
//		long nowTimeMillis = System.currentTimeMillis();
//		Date nowDate = new Date(nowTimeMillis);
		
		Calendar nowTime = Calendar.getInstance();
		//设置1天后过期
		nowTime.add(Calendar.DATE, 1);
		Date expireDate = nowTime.getTime();
		
		if(CommonUtil.isBlank(secret)) {
			throw new DataException("511");
		}
		byte[] secretKeyBytes = DECODER.decode(secret);
		Key secretKeySpec = new SecretKeySpec(secretKeyBytes, algorithm.getJcaName());
		
		//构建builder
		JwtBuilder builder = Jwts.builder().setClaims(claims)
								.setHeaderParam("typ", "JWT")
								.setIssuer("JWT")
								.signWith(algorithm, secretKeySpec);
		//设置过期时间
//		if(EXPIRE_DATE >= 0) {
//			long expireMillis = nowTimeMillis + EXPIRE_DATE;
//			Date expireDate = new Date(expireMillis);
//			builder.setExpiration(expireDate).setNotBefore(nowDate);
//			
//		}
		builder.setExpiration(expireDate).setNotBefore(DateUtil.getSystemDate());
		return builder.compact();
	}
	
	
	
	/**
	 * 解析jwt
	 * @param token
	 * @return
	 */
	public static Claims parseJwt(String token, String secret) {
		if(CommonUtil.isBlank(token)) {
			return null;
		}
		if(CommonUtil.isBlank(secret)) {
			return null;
		}
		return Jwts.parser().setSigningKey(DECODER.decode(secret)).parseClaimsJws(token).getBody();
	}

}
