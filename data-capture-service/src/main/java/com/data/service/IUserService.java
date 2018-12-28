package com.data.service;

import org.springframework.web.multipart.MultipartFile;

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
	ResultUtil saveUser(User user, String roleId);
	
	/**
	 * 更新用户
	 * @param user
	 * @return
	 */
	ResultUtil updateUser(User user, String roleId);
	
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
	
	/**
	 * 用户登录
	 * @param userId
	 * @param password
	 * @param request
	 * @return
	 */
	ResultUtil login(String userId, String password);
	
	/**
	 * 用户退出
	 * @param userId
	 * @return
	 */
	ResultUtil logout(String userId);
	
	/**
	 * 用户数据导入
	 * @param file
	 * @return
	 * @throws Exception
	 */
	ResultUtil uploadUserData(MultipartFile file) throws Exception;
}
