package com.data.interceptor;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.data.constant.CommonValue;
import com.data.exception.DataException;
import com.data.service.IRedisService;
import com.data.utils.CommonUtil;
import com.data.utils.JwtUtil;

import io.jsonwebtoken.Claims;

/**
 * 拦截器
 * @author Alex
 *
 */
@Component
public class DataInterceptor implements HandlerInterceptor {
	
	private static Logger logger = LoggerFactory.getLogger(DataInterceptor.class);
	
	@Value("${spring.jwt.secretKey}")
	private String secret;
	
	@Autowired
	private IRedisService redisService;
	
	//免拦截过滤的链接
	private String[] filterUrls;
	
	@PostConstruct
	public void init() {
		filterUrls = new String[] {
				
		};
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//此处就从请求里面取
		String accessToken = request.getParameter("accessToken");
		logger.info("--->>>前台回传的token: {} <<<---", accessToken);
		
		if(CommonUtil.isBlank(accessToken)) {
			throw new DataException("518");
		}
		String elle = accessToken.substring(0, 4).toLowerCase();
		if(!CommonUtil.isNotBlank(elle) || elle.length() < 5) {
			throw new DataException("519");
		} else {
			Claims claims = JwtUtil.parseJwt(accessToken, secret);
			if(isAuthenticate(request, accessToken, claims)) {
				String userId = claims.get(CommonValue.USER_ID).toString();
				request.setAttribute("userId", userId);
			} else {
				//认证失败
				logger.info("--->>>token校验失败<<<---");
				throw new DataException("521");
			}
		}
		return true;
	}
	
	private boolean isAuthenticate(HttpServletRequest request, String accessToken, Claims claims) {
		String requestUrl = request.getServletPath();
		
		//如果是免拦截请求则放通
		if(filterUrls != null && filterUrls.length != 0) {
			for(String url : filterUrls) {
				if(requestUrl.equals(url)) {
					return true;
				}
			}
		}
		
		//否则必须认证
		//将token生成重新匹对
		//不对
		String token = redisService.getAccessToken(CommonValue.ACCESS_TOKEN_KEY + claims.get("userId").toString());
		if(CommonUtil.isBlank(token)) {
			throw new DataException("520");
		}
		String userId = claims.get(CommonValue.USER_ID).toString();
		if(CommonUtil.isNotBlank(userId)) {
			//匹配token
			String shadowToken = JwtUtil.createJwt(userId, secret);
			logger.info("--->>>前台传回生成的shadowToken: {} <<<---", shadowToken);
			return shadowToken.equals(accessToken);
		}
		return false;
	}
}
