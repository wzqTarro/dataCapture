package com.data.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.data.bean.User;
import com.data.constant.PageRecord;
import com.data.constant.dbSql.InsertId;
import com.data.constant.dbSql.QueryId;
import com.data.constant.dbSql.UpdateId;
import com.data.exception.DataException;
import com.data.service.IUserService;
import com.data.service.IRedisService;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

/**
 * 用户服务实现类
 * @author Alex
 *
 */
@Service("userService")
public class UserServiceImpl extends CommonServiceImpl implements IUserService {
	
	private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
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
	public ResultUtil saveUser(User user) {
		logger.info("--->>>保存用户信息: {}<<<---", FastJsonUtil.objectToString(user));
		if(CommonUtil.isBlank(user)) {
			throw new DataException("401");
		}
		insert(InsertId.INSERT_NEW_USER_MESSAGE, user);
		redisService.saveUserModel(user);
		return ResultUtil.success();
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil updateUser(User user) {
		logger.info("--->>>更新用户信息: {}<<<---", FastJsonUtil.objectToString(user));
		if(CommonUtil.isBlank(user)) {
			throw new DataException("401");
		}
		update(UpdateId.UPDATE_USER_MESSAGE, user);
		redisService.updateUserModel(user);
		return ResultUtil.success();
	}

	@Override
	public ResultUtil queryUserDetail(String workNo) {
		logger.info("--->>>查询用户详情 id编号: {} <<<---", workNo);
		if(CommonUtil.isBlank(workNo)) {
			throw new DataException("404");
		}
		User user = redisService.getUserModel(workNo);
		if(CommonUtil.isBlank(user)) {
			user = (User) queryObjectByParameter(QueryId.QUERY_USER_DETAIL, workNo);
		}
		logger.info("--->>>查询用户详情: {} <<<---", FastJsonUtil.objectToString(user));
		return ResultUtil.success(user);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class })
	public ResultUtil deleteUser(String workNo) {
		logger.info("--->>>删除用户 workNo编号: {} <<<---", workNo);
		if(CommonUtil.isBlank(workNo)) {
			throw new DataException("404");
		}
		//此处用户删除采用逻辑删除
		User user = new User();
		user.setIsAlive("01");
		user.setWorkNo(workNo);
		update(UpdateId.UPDATE_USER_MESSAGE, user);
		return ResultUtil.success();
	}

}
