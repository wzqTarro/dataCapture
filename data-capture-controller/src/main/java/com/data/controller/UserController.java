package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.User;
import com.data.service.IUserService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@Api(tags = "用户服务接口")
//@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
	
	@Autowired
	private IUserService userService;
	
	/**
	 * 查询用户列表
	 * @param user
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/queryUserList", method = RequestMethod.GET)
	@ApiOperation(value = "用户列表查询", httpMethod = "GET")
	public String queryUserByCondition(User user, String pageNum, String pageSize) throws Exception {
		ResultUtil result = userService.queryUserByCondition(user, pageNum, pageSize);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 保存用户
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/saveUser", method = RequestMethod.POST)
	@ApiOperation(value = "保存用户信息", httpMethod = "POST")
	public String saveUser(User user) {
		ResultUtil result = userService.saveUser(user);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 更新用户
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	@ApiOperation(value = "更新用户信息", httpMethod = "POST")
	public String updateUser(User user) {
		ResultUtil result = userService.updateUser(user);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 查询用户详情
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryUserDetail", method = RequestMethod.GET)
	@ApiOperation(value = "查看用户信息", httpMethod = "GET")
	public String queryUserDetail(String id) {
		ResultUtil result = userService.queryUserDetail(id);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 删除用户
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
	@ApiOperation(value = "删除用户信息", httpMethod = "POST")
	public String deleteUser(String id) {
		ResultUtil result = userService.deleteUser(id);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 用户登录
	 * @param userId
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ApiOperation(value = "用户登录", httpMethod = "POST")
	public String login(String userId, String password) {
		ResultUtil result = userService.login(userId, password);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 用户退出
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ApiOperation(value = "用户退出", httpMethod = "GET")
	public String logout(String userId) {
		ResultUtil result = userService.logout(userId);
		return FastJsonUtil.objectToString(result);
	}
}
