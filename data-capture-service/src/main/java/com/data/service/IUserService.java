package com.data.service;

import com.data.bean.User;
import com.data.utils.ResultUtil;

/**
 * 用户服务接口
 * @author Alex
 *
 */
public interface IUserService {

	/**
	 * 查询用户列表
	 * @param user
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	ResultUtil queryUserByCondition(User user, String pageNum, String pageSize) throws Exception;
	
	/**
	 * 保存用户
	 * @param user
	 * @return
	 */
	ResultUtil saveUser(User user);
	
	/**
	 * 更新用户
	 * @param user
	 * @return
	 */
	ResultUtil updateUser(User user);
	
	/**
	 * 查询用户详情
	 * @param id
	 * @return
	 */
	ResultUtil queryUserDetail(String workNo);
	
	/**
	 * 删除用户
	 * @param id
	 * @return
	 */
	ResultUtil deleteUser(String workNo);
}