package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.TemplateProduct;
import com.data.service.ITemplateProductService;
import com.data.utils.FastJsonUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/templateProduct")
@Api(tags = "模板商品数据接口")
public class TemplateProductController {

	@Autowired
	private ITemplateProductService templateProductService;
	
	@RequestMapping(value = "getTemplateProductByParam", method = RequestMethod.POST)
	@ApiOperation(value = "多条件查询商品模板信息", httpMethod = "POST")
	public String getTemplateProductByParam(@RequestParam("param") String param) throws Exception {
		return FastJsonUtil.objectToString(templateProductService.getTemplateProductByParam(param));
	}
	
	@RequestMapping(value = "insertProduct", method = RequestMethod.POST)
	@ApiOperation(value = "插入商品模板信息", httpMethod = "POST")
	public String insertProduct(TemplateProduct product) {
		return FastJsonUtil.objectToString(templateProductService.insertTemplateProduct(product));
	}
	
	@RequestMapping(value = "updateProduct", method = RequestMethod.PUT)
	@ApiOperation(value = "更新商品模板信息", httpMethod = "PUT")
	public String updateProduct(TemplateProduct product) {
		return FastJsonUtil.objectToString(templateProductService.updateTemplateProduct(product));
	}
	
	@RequestMapping(value = "deleteProduct", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除商品模板信息", httpMethod = "DELETE")
	public String deleteProduct(Integer id) {
		return FastJsonUtil.objectToString(templateProductService.deleteTemplateProduct(id));
	}
	
}