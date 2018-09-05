package com.data.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.data.constant.CommonValue;
import com.data.exception.DataException;
import com.data.service.IJwtService;
import com.data.service.IRedisService;
import com.data.utils.CommonUtil;
import com.data.utils.JwtUtil;
import com.data.utils.ResultUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

@Service("jwtService")
public class JwtServiceImpl extends CommonServiceImpl implements IJwtService {
	
	private static Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);
	
	@Value("${spring.jwt.secretKey}")
	private String secret;
	
	@Autowired
	private IRedisService redisService;

	@Override
	public ResultUtil createToken(Map<String, Object> params) {
		if(CommonUtil.isBlank(params)) {
			//throw new DataException("512");
			return ResultUtil.error("生成token参数不能为空");
		}
		String userId = (String) params.get(CommonValue.USER_ID);
		if(CommonUtil.isBlank(userId)) {
			//throw new DataException("403");
			return ResultUtil.error("用工号不能为空");
		}
		/**
		 * TODO 将生成token的代码放至在服务中
		 */
		String token = JwtUtil.createJwt(params, secret, 60 * 60 * 30 * 1000);
		redisService.setUserId(userId, token);
		return ResultUtil.success(token);
	}

	/**
	 * TODO
	 */
	@Override
	public ResultUtil checkToken(String token) {
		if(CommonUtil.isBlank(token)) {
			//throw new DataException("513");
			return ResultUtil.error("token解析异常");
		}
		logger.info("--->>>校验token--token: {} <<<---", token);
		Claims claims;
		try {
			claims = JwtUtil.parseJwt(token, secret);
		} catch (ExpiredJwtException e) {
			logger.info("--->>>身份已失效， 请重新登录<<<---");
			//throw new DataException("516");
			return ResultUtil.error("token已失效，请重新登录");
		} catch (Exception e) {
			logger.info("--->>>系统异常， 请重新登录<<<---");
			//throw new DataException("517");
			return ResultUtil.error("系统异常，请重新登录");
		}
		if(claims == null) {
			//throw new DataException("514");
			return ResultUtil.error("token解析异常");
		}
		String userId = (String) claims.get(CommonValue.USER_ID);
		if(CommonUtil.isBlank(userId)) {
			//throw new DataException("403");
			return ResultUtil.error("用工号不能为空");
		}
		if(CommonUtil.isNotBlank(userId)) {
			//查询用户是否存在 从缓存查
			String id = redisService.getUserId(token);
			if(CommonUtil.isBlank(id)) {
				//throw new DataException("403");
				return ResultUtil.error("用工号不能为空");
			}
			/**
			 * 此处校验代码参考拦截器校验 需修改
			 */
			if(!id.equals(userId)) {
				//throw new DataException("515");
				return ResultUtil.error("token校验失败");
			}
			return ResultUtil.success(userId);
		}
		
		return null;
	}

}
