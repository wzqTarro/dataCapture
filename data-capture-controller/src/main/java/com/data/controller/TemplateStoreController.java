package com.data.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.TemplateStore;
import com.data.service.ITemplateStoreService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/store")
@Api(tags = {"模板门店数据接口"})
@CrossOrigin(origins="*", maxAge=3600)
public class TemplateStoreController {

	@Autowired
	private ITemplateStoreService storeService;
	
	/**
	 * 多条件查询模板门店信息
	 * @param param
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/getTemplateStoreByParam", method = RequestMethod.POST)
	@ApiOperation(value = "多条件分页查询模板门店信息", httpMethod = "POST")
	public String getTemplateStoreByParam(TemplateStore templateStore,
			@RequestParam(value = "page", required = false)Integer page, 
			@RequestParam(value = "limit", required = false)Integer limit) throws Exception {
		ResultUtil result = storeService.getTemplateStoreByParam(templateStore, page, limit);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 更新模板门店信息
	 * @param templateStore
	 * @return
	 */
	@RequestMapping(value = "/updateTemplateStore", method = RequestMethod.PUT)
	@ApiOperation(value = "更新模板门店信息", httpMethod = "PUT")
	public String updateTemplateStore(TemplateStore templateStore, String practiceDate) {
		ResultUtil result = null;
		try {
			result = storeService.updateTemplateStore(templateStore, practiceDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 插入模板门店信息
	 * @param templateStore
	 * @return
	 */
	@RequestMapping(value = "/insertTemplateStore", method = RequestMethod.POST)
	@ApiOperation(value = "插入模板门店信息", httpMethod = "POST")
	public String insertTemplateStore(TemplateStore templateStore, String practiceDate) {
		ResultUtil result=null;
		try {
			result = storeService.insertTemplateStore(templateStore, practiceDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 删除模板门店信息
	 * @param templateStore
	 * @return
	 */
	@RequestMapping(value = "/deleteTemplateStore", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除模板门店信息", httpMethod = "DELETE")
	public String deleteTemplateStore(int id) {
		ResultUtil result = storeService.deleteTemplateStoreById(id);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 获取系统下大区及其对应省区菜单
	 * @param sysId
	 * @return
	 */
	@RequestMapping(value = "getRegionMenu", method = RequestMethod.GET)
	@ApiOperation(value = "获取系统下大区及其对应省区菜单", httpMethod =  "GET")
	public String getRegionMenu(String sysId) {
		ResultUtil result = storeService.getRegionMenu(sysId);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 获取系统下门店菜单
	 * @param sysId
	 * @return
	 */
	@RequestMapping(value = "getStoreMenu", method = RequestMethod.GET)
	@ApiOperation(value = "获取系统下门店菜单", httpMethod =  "GET")
	public String getStoreMenu(String sysId) {
		ResultUtil result = storeService.getStoreMenu(sysId);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 *	
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryStoreInfo", method = RequestMethod.POST)
	@ApiOperation(value = "按ID查询门店信息", httpMethod = "POST")
	public String queryStoreInfo(String id) {
		ResultUtil result = storeService.queryStoreInfo(id);
		return FastJsonUtil.objectToString(result);
	}
	
}
