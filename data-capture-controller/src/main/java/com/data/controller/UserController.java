package com.data.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.data.bean.User;
import com.data.service.IUserService;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@Api(tags = "用户服务接口")
@CrossOrigin(origins = "*", maxAge = 3600)
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
	public String saveUser(User user, String roleId) {
		ResultUtil result = userService.saveUser(user, roleId);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 更新用户
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	@ApiOperation(value = "更新用户信息", httpMethod = "POST")
	public String updateUser(User user, String roleId) {
		ResultUtil result = userService.updateUser(user, roleId);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 查询用户详情
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryUserDetail", method = RequestMethod.GET)
	@ApiOperation(value = "查看用户信息", httpMethod = "GET")
	public String queryUserDetail(String workNo) {
		ResultUtil result = userService.queryUserDetail(workNo);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 删除用户
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
	@ApiOperation(value = "删除用户信息", httpMethod = "POST")
	public String deleteUser(String workNo) {
		ResultUtil result = userService.deleteUser(workNo);
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
	
	/**
	 * 返回用户工号
	 * @return
	 */
	@RequestMapping(value = "/createUserWorkNo", method = RequestMethod.GET)
	public String createUserWorkNo() {
		Map<String, Object> map = new HashMap<>(4);
		map.put("workNo", CommonUtil.createWorkNo());
		return FastJsonUtil.objectToString(map);
	}
	
	/**
     * 用户数据导入
     * @param file
     * @param request
     * @throws Exception 
     */
    @RequestMapping(value = "/uploadUserExcel", method = RequestMethod.POST)
    @ApiOperation(value = "用户数据导入", httpMethod = "POST")
    public String uploadUserExcel(@RequestParam("file") MultipartFile file) throws Exception {
    	ResultUtil result = userService.uploadUserData(file);
    	return FastJsonUtil.objectToString(result);
    }
}
