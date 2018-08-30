package com.data.service;

import java.util.Map;

import com.data.utils.ResultUtil;

/**
 * jwt服务接口
 * @author Administrator
 *
 */
public interface IJwtService {

	/**
	 * 生成token令牌
	 * @param params
	 * @return
	 */
	ResultUtil createToken(Map<String, Object> params);
	
	/**
	 * 校验token
	 * @param token
	 * @return
	 */
	ResultUtil checkToken(String token);
}
