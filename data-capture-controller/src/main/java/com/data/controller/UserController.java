package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.User;
import com.data.service.IUserService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/user")
@Api(tags = "用户服务接口")
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
	public String deleteUser(String id) {
		ResultUtil result = userService.deleteUser(id);
		return FastJsonUtil.objectToString(result);
	}
}
