package com.data.interceptor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.data.bean.SystemFunction;
import com.data.constant.CommonValue;
import com.data.constant.enums.CodeEnum;
import com.data.exception.AuthException;
import com.data.exception.DataException;
import com.data.exception.GlobalException;
import com.data.service.IRedisService;
import com.data.service.ISystemFunctionService;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.JwtUtil;
import com.data.utils.ResultUtil;

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
	
	@Autowired
	private ISystemFunctionService functionService;
	
	//免拦截过滤的链接
	private String[] filterUrls;
	
	@PostConstruct
	public void init() {
		filterUrls = new String[] {
				"/swagger-ui.html",
				"/error",
				"/user/login",
				"/sale/storeDailyExcel",
				"/sale/calculateStoreDailySale",
				"/sale/exportSaleExcelByRegion",
				"/sale/exportSaleExcelBySysId",
				"/sale/exportSaleExcelByProvinceArea",
				"/sale/exportSaleExcelByStoreCode",
				"/stock/getStockByParam",
				"/system/buildMenuList",
				"/system/queryAllFunctionList",
				"/file/downloadTemplateExcel",
				"/system/queryRoleFunctionByRoleId",
				"/user/queryUserDetail",
				"/user/logout",
				
				//查询列表不拦截
				"/user/queryUserList",
				"/supply/querySupplyByCondition",
				"/store/getTemplateStoreByParam",
				"/templateProduct/getTemplateProductByParam",
				"/simpleCode/getSimpleCodeByParam",
				"/promotionDetail/getPromotionDetailByCondition",
				"/sale/getDataByParam",
				"/stock/getStockByParam",
				"/order/getOrderByCondition",
				"/reject/getRejectByCondition",
				"/sale/queryStoreDailySaleReport",
				"/order/queryOrderAlarmList",
				"/reject/queryRejectAlarmList",
				"/promotionStoreList/getPromotionStoreList",
				
				//查询详情面拦截
				"/promotionDetail/queryPromotionInfo",
				"/simpleCode/querySimpleCodeInfo",
				"/templateProduct/queryProductInfo",
				"/store/queryStoreInfo",
				"/supply/querySupplyInfo",
				"/user/queryUserDetail",
				
				//下拉菜单 免拦截
				"/store/getRegionMenu",
				"/templateProduct/getBrandMenu",
				"/store/getStoreMenu",
				"/supply/getSupplyMenu",
				
				//权限测试 暂时先放在了里面
				"/system/addMenu",
				"/system/updateMenu",
				"/system/deleteMenu",
				"/system/queryMenuListByPage",
				"/system/queryRoleMenuList",
				"/system/updateRoleFunction",
				"/systemRole/querySystemRoleByPage",
				"/systemRole/querySystemRoleDetail",
				"/systemRole/addSystemRole",
				"/systemRole/updateSystemRole",
				"/systemRole/deleteSystemRoleByRoleId",
				"/systemRole/deleteSystemRoleByIds",
				"/system/updateRoleMenu",
				"/system/queryRoleMenu"
				
				//测试上传 暂时面拦截
//				"/order/uploadOrderExcel",
//				"/reject/uploadRejectExcel",
//				"/sale/uploadSaleExcel",
//				"/simpleCode/uploadSimpleCodeExcel",
//				"/stock/uploadStockExcel",
//				"/supply/uploadTemplateSupplyExcel",
//				"/user/uploadUserExcel"
				
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
				logger.info("--->>>令牌({})token解析失败<<<---", accessToken);
				throw new AuthException("521");
			}
			if(isAuthenticate(accessToken, claims, request)) {
				String userId = claims.get(CommonValue.USER_ID).toString();
				request.setAttribute("workNo", userId);
				logger.info("--->>>用户: {} 认证通过<<<---", userId);
				return true;
			} else {
				//认证失败
				logger.error("--->>>token({})校验失败<<<---", accessToken);
				throw new AuthException("521");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean isAuthenticate(String accessToken, Claims claims, HttpServletRequest request) {
		logger.info("--->>>前台传回生成的accessToken: {} <<<---", accessToken);
		String token = "";
		try {
			token = redisService.getAccessToken(CommonValue.ACCESS_TOKEN_KEY + claims.get("userId").toString());
			token = token.substring(5);
			logger.info("--->>>后台保存的token: {} <<<---", token);
			if(CommonUtil.isBlank(token)) {
				/**
				 * TODO
				 * 此处做单一用户登录
				 */
				throw new AuthException("520");
			}
			if(!token.equals(accessToken)) {
				logger.info("--->>>token({})校验不通过<<<---", accessToken);
				throw new AuthException("537");
			}
			//动作权限检验
			String roleId = claims.get("roleId").toString();
			List<SystemFunction> functionList = new ArrayList<>(10);
			ResultUtil resultUtil = functionService.queryRoleFunctionList(roleId);
			if(CodeEnum.RESPONSE_00_CODE.value().equals(resultUtil.getCode())) {
				List<SystemFunction> functionDataList = (List<SystemFunction>) resultUtil.getData();
				String functionListJson = FastJsonUtil.objectToString(functionDataList);
				//logger.info("--->>>用户{}角色为{},所具有的权限为{}<<<---", claims.get("userId").toString(), roleId, functionListJson);
				functionList = (List<SystemFunction>) FastJsonUtil.jsonToList(functionListJson, SystemFunction.class);
			}
			String requestPath = request.getServletPath();
			for(SystemFunction function : functionList) {
				String url = function.getFunctionUrl();
				if(url.equals(requestPath)) {
					return true;
				}
			}
			logger.error("--->>>{}角色没有相关权限<<<---", roleId);
			throw new AuthException("538");
		} catch (DataException | GlobalException e) {
			logger.info("--->>>令牌({})已失效<<<---", accessToken);
			return false;
		}
	}
}
