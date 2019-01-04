package com.data.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.data.bean.SystemUserRole;
import com.data.bean.User;
import com.data.constant.CommonValue;
import com.data.constant.PageRecord;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.constant.enums.ExcelEnum;
import com.data.exception.DataException;
import com.data.service.IRedisService;
import com.data.service.IUserService;
import com.data.utils.CommonUtil;
import com.data.utils.EncryptUtil;
import com.data.utils.ExcelUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.JwtUtil;
import com.data.utils.ResultUtil;

/**
 * 用户服务实现类
 * @author Alex
 *
 */
@Service("userService")
public class UserServiceImpl extends CommonServiceImpl implements IUserService {
	
	private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Value("${spring.jwt.secretKey}")
	private String secret;
	
	@Autowired
	private IRedisService redisService;

	@Override
	public ResultUtil queryUserByCondition(User user, String pageNum, String pageSize) throws Exception {
		if(CommonUtil.isBlank(user)) {
			throw new DataException("401");
		}
		if(CommonUtil.isBlank(pageNum)) {
			throw new DataException("501");
		}
		if(CommonUtil.isBlank(pageSize)) {
			throw new DataException("502");
		}
		logger.info("--->>>传入参数user: {}, 页码: {}, 内容数: {}<<<---", FastJsonUtil.objectToString(user), pageNum, pageSize);
		Map<String, Object> params = new HashMap<>(8);
		if(CommonUtil.isNotBlank(user.getWorkNo())) {
			params.put("workNo", user.getWorkNo());			
		}
		if(CommonUtil.isNotBlank(user.getUsername())) {
			params.put("username", user.getUsername());
		}
		if(CommonUtil.isNotBlank(user.getMobileCode())) {
			params.put("mobileCode", user.getMobileCode());
		}
		if(CommonUtil.isNotBlank(user.getEmail())) {
			params.put("email", user.getEmail());
		}
		if(CommonUtil.isNotBlank(user.getGender())) {
			params.put("gender", user.getGender());
		}
		if(CommonUtil.isNotBlank(user.getDepartment())) {
			params.put("department", user.getDepartment());
		}
		if(CommonUtil.isNotBlank(user.getPosition())) {
			params.put("position", user.getPosition());
		}
		PageRecord<User> userPage = queryPageByObject(QueryId.QUERY_COUNT_USER_BY_CONDITION, QueryId.QUERY_USER_BY_CONDITION, params,
								Integer.parseInt(pageNum), Integer.parseInt(pageSize));
		logger.info("--->>>用户查询集合: {}<<<---", FastJsonUtil.objectToString(userPage));
		return ResultUtil.success(userPage);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil saveUser(User user, String roleId) {
		logger.info("--->>>保存用户信息: {}<<<---", FastJsonUtil.objectToString(user));
		if(CommonUtil.isBlank(user)) {
			//throw new DataException("401");
			return ResultUtil.error("传入用户信息不存在");
		}
		if(CommonUtil.isBlank(roleId)) {
			return ResultUtil.error("用户角色编号错误");
		}
		user.setWorkNo(CommonUtil.createWorkNo());
		user.setPassword(EncryptUtil.Md5Encrypt("123456"));
		insert(InsertId.INSERT_NEW_USER_MESSAGE, user);
		SystemUserRole userRole = new SystemUserRole();
		userRole.setWorkNo(user.getWorkNo());
		userRole.setRoleId(roleId);
		insert(InsertId.INSERT_NEW_SYSTEM_USER_ROLE, userRole);
		return ResultUtil.success();
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil updateUser(User user, String roleId) {
		logger.info("--->>>更新用户信息: {}<<<---", FastJsonUtil.objectToString(user));
		if(CommonUtil.isBlank(user)) {
			//throw new DataException("401");
			return ResultUtil.error("传入用户信息不存在");
		}
		if(CommonUtil.isBlank(roleId)) {
			return ResultUtil.error("用户角色编号错误");
		}
		String password = user.getPassword();
		if(CommonUtil.isNotBlank(password)) {
			user.setPassword(EncryptUtil.Md5Encrypt(password));
		}
		SystemUserRole userRole = new SystemUserRole();
		userRole.setWorkNo(user.getWorkNo());
		userRole.setRoleId(roleId);
		update(UpdateId.UPDATE_USER_MESSAGE_BY_WORK_NO, user);
		//更新缓存
		redisService.updateUserModel(user);
		update(UpdateId.UPDATE_SYSTEM_USER_ROLE_BY_WORK_NO, userRole);
		return ResultUtil.success();
	}

	@Override
	public ResultUtil queryUserDetail(String workNo) {
		logger.info("--->>>查询用户详情 id编号: {} <<<---", workNo);
		if(CommonUtil.isBlank(workNo)) {
			//throw new DataException("404");
			return ResultUtil.error("用工号不能为空");
		}
		User user = redisService.getUserModel(workNo);
		SystemUserRole userRole = (SystemUserRole) queryObjectByParameter(QueryId.QUERY_USER_ROLE_BY_WORK_NO, workNo);
		user.setRoleId(userRole.getRoleId());
		logger.info("--->>>查询用户详情: {} <<<---", FastJsonUtil.objectToString(user));
		return ResultUtil.success(user);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil deleteUser(String workNo) {
		logger.info("--->>>删除用户 workNo编号: {} <<<---", workNo);
		if(CommonUtil.isBlank(workNo)) {
			//throw new DataException("404");
			return ResultUtil.error("用工号不能为空");
		}
		//此处用户删除采用逻辑删除
		User user = new User();
		user.setIsAlive("01");
		user.setWorkNo(workNo);
		update(UpdateId.UPDATE_USER_MESSAGE_BY_WORK_NO, user);
		return ResultUtil.success();
	}

	@Override
	public ResultUtil login(String userId, String password) {
		logger.info("--->>>登录前台传回用户id: {} 密码: {} <<<---", userId, password);
		if(CommonUtil.isBlank(userId) || CommonUtil.isBlank(password)) {
			//throw new DataException("405");
			return ResultUtil.error("登录信息不能为空!");
		}
		int count = queryCountByObject(QueryId.QUERY_COUNT_USER_BY_USER_ID, userId);
		if(count == 0) {
			logger.info("--->>>用户不存在<<<---");
			//throw new DataException("406");
			return ResultUtil.error("用户不存在，请重新数据");
		}
		User user = (User) queryObjectByParameter(QueryId.QUERY_USER_BY_WORK_NO, userId);
		if("01".equals(user.getIsAlive())) {
			logger.info("--->>>用户已失效<<<---");
			//throw new DataException("406");
			return ResultUtil.error("用户不存在，请重新数据");
		}
		try {
			if(!EncryptUtil.verify(password, user.getPassword())) {
				logger.info("--->>>密码校验不通过<<<---");
				//throw new DataException("407");
				return ResultUtil.error("密码不正确，请重新输入");
			}
		} catch (StringIndexOutOfBoundsException e) {
			logger.info("--->>>密码校验异常: {}<<<---", e.getMessage());
			//throw new DataException("407");
			return ResultUtil.error("密码不正确，请重新输入");
		}
		String workNo = user.getWorkNo();
		Map<String, Object> params = new HashMap<>(4);
		params.put("workNo", workNo);
		/**
		 * TODO
		 * 此处做单一用户登录
		 */
		SystemUserRole userRole = (SystemUserRole) queryObjectByParameter(QueryId.QUERY_USER_ROLE_BY_WORK_NO, params);
		String roleId = userRole.getRoleId();
		//生成token并且放入缓存中
		String accessToken = JwtUtil.createJwt(workNo, roleId, secret);
		accessToken = CommonValue.ELLE + accessToken;
		redisService.setAccessToken(CommonValue.ACCESS_TOKEN_KEY + workNo, accessToken);
		//更新用户登录次数及时间
		updateUserLoginTrace(workNo);
		Map<String, Object> map = new HashMap<>();
		//workNo就是userId
		map.put("workNo", workNo);
		map.put("username", user.getUsername());
		map.put("accessToken", accessToken);
		map.put("roleId", roleId);
		logger.info("--->>>用户登录通过: {} <<<---", FastJsonUtil.objectToString(map));
		return ResultUtil.success(map);
	}

	@Override
	public ResultUtil logout(String userId) {
		logger.info("--->>>用户退出 userId: {} <<<---", userId);
		redisService.deleteAccessToken(CommonValue.ACCESS_TOKEN_KEY + userId);
		return ResultUtil.success();
	}
	
	private void updateUserLoginTrace(String workNo) {
		User oldUser = (User) queryObjectByParameter(QueryId.QUERY_USER_BY_WORK_NO, workNo);
		logger.info("--->>>用户{}的信息为: {}<<<---", workNo, FastJsonUtil.objectToString(oldUser));
		User user = new User();
		user.setWorkNo(workNo);
		user.setLoginTimes(oldUser.getLoginTimes() + 1);
		user.setLastLoginDate(new Date(System.currentTimeMillis()));
		update(UpdateId.UPDATE_USER_MESSAGE_BY_WORK_NO, user);
	}

	@Override
	public ResultUtil uploadUserData(MultipartFile file) throws Exception {
		ExcelUtil<User> excelUtil = new ExcelUtil<>();
		List<Map<String, Object>> userMapList = excelUtil.getExcelList(file, ExcelEnum.USER_TEMPLATE_TYPE.value());
		if (userMapList == null) {
			return ResultUtil.error("格式不符，导入失败");
		}
		insert(InsertId.INSERT_BATCH_USER, userMapList);
		return ResultUtil.success();
	}

}
