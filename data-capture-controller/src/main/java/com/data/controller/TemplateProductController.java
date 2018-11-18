package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.data.bean.TemplateProduct;
import com.data.service.ITemplateProductService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/templateProduct")
@Api(tags = "模板商品数据接口")
@CrossOrigin(origins="*", maxAge=3600)
public class TemplateProductController {

	@Autowired
	private ITemplateProductService templateProductService;
	
	@RequestMapping(value = "getTemplateProductByParam", method = RequestMethod.POST)
	@ApiOperation(value = "多条件查询商品模板信息", httpMethod = "POST")
	public String getTemplateProductByParam(TemplateProduct templateProduct,
			@RequestParam(value = "page", required = false)Integer page, 
			@RequestParam(value = "limit", required = false)Integer limit) throws Exception {
		ResultUtil result = templateProductService.getTemplateProductByParam(templateProduct, page, limit);
		return FastJsonUtil.objectToString(result);
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
	public String deleteProduct(int id) {
		return FastJsonUtil.objectToString(templateProductService.deleteTemplateProduct(id));
	}
	/**
	 * 获取品牌菜单
	 * @param sysId
	 * @return
	 */
	@RequestMapping(value = "getBrandMenu", method = RequestMethod.GET)
	@ApiOperation(value = "获取品牌菜单", httpMethod = "GET")
	public String getBrandMenu(String sysId) {
		return FastJsonUtil.objectToString(templateProductService.getBrandMenu(sysId));
	}
	
	/**
	 *	
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryProductInfo", method = RequestMethod.POST)
	@ApiOperation(value = "按ID查询产品信息", httpMethod = "POST")
	public String queryProductInfo(String id) {
		ResultUtil result = templateProductService.queryProductInfo(id);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
     * 模板产品数据导入
     * @param file
     * @param request
     * @throws Exception 
     */
    @RequestMapping(value = "/uploadTemplateProductExcel", method = RequestMethod.POST)
    @ApiOperation(value = "模板产品数据导入", httpMethod = "POST")
    public String uploadTemplateProductExcel(@RequestParam("file") MultipartFile file) throws Exception {
    	ResultUtil result = templateProductService.uploadTemplateProductData(file);
    	return FastJsonUtil.objectToString(result);
    }
}
