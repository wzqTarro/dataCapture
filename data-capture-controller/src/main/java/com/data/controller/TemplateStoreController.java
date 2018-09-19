package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
	public String getTemplateStoreByParam(@RequestParam(value = "templateStore", required = false)TemplateStore templateStore,
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
	public String updateTemplateStore(TemplateStore templateStore) {
		ResultUtil result = storeService.updateTemplateStore(templateStore);
		return FastJsonUtil.objectToString(result);
	}
	/**
	 * 插入模板门店信息
	 * @param templateStore
	 * @return
	 */
	@RequestMapping(value = "/insertTemplateStore", method = RequestMethod.POST)
	@ApiOperation(value = "插入模板门店信息", httpMethod = "POST")
	public String insertTemplateStore(TemplateStore templateStore) {
		ResultUtil result = storeService.insertTemplateStore(templateStore);
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
}
