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
import com.data.exception.AuthException;
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
				"/swagger-ui.html",
				"/error",
				"/user/login",
				"/sale/storeDailyExcel",
				"/sale/calculateStoreDailySale"
				
		};
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String requestUrl = request.getServletPath();
		logger.info("--->>>请求url: {} <<<---", requestUrl);
		
		//如果是免拦截请求则放通
		if(filterUrls != null && filterUrls.length != 0) {
			for(String url : filterUrls) {
				if(requestUrl.equals(url)) {
					return true;
				}
			}
		}
		
		//此处就从请求里面取
		String accessToken = request.getParameter("accessToken");
		logger.info("--->>>前台回传的token: {} <<<---", accessToken);
		
		if(CommonUtil.isBlank(accessToken)) {
			throw new AuthException("518");
		}
		
		String elle = accessToken.substring(0, 5).toLowerCase();
		if(!CommonUtil.isNotBlank(elle) || elle.length() < 5) {
			throw new DataException("519");
		} else {
			accessToken = accessToken.substring(5);
			Claims claims;
			try {
				claims = JwtUtil.parseJwt(accessToken, secret);
			} catch (Exception e) {
				logger.info("--->>>token解析失败<<<---");
				throw new AuthException("521");
			}
			if(isAuthenticate(accessToken, claims)) {
				String userId = claims.get(CommonValue.USER_ID).toString();
				request.setAttribute("workNo", userId);
				logger.info("--->>>用户: {} 认证通过<<<---", userId);
				return true;
			} else {
				//认证失败
				logger.info("--->>>token校验失败<<<---");
				throw new AuthException("521");
			}
		}
	}
	
	private boolean isAuthenticate(String accessToken, Claims claims) {
		logger.info("--->>>前台传回生成的accessToken: {} <<<---", accessToken);
		String token = "";
		try {
			token = redisService.getAccessToken(CommonValue.ACCESS_TOKEN_KEY + claims.get("userId").toString());
			token = token.substring(5);
			logger.info("--->>>后台保存的token: {} <<<---", token);
			if(CommonUtil.isBlank(token)) {
				throw new AuthException("520");
			}
			/**
			 * TODO
			 * 用户续命问题
			 */
			return token.equals(accessToken);
		} catch (Exception e) {
			logger.info("--->>>令牌已失效，请重新登录<<<---");
			return false;
		}
	}
}
