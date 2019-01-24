package com.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.data.bean.TemplateSupply;
import com.data.service.ITemplateSupplyService;
import com.data.utils.FastJsonUtil;
import com.data.utils.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/supply")
@Api(tags = "供应链服务接口")
@CrossOrigin(origins="*", maxAge=3600)
public class TemplateSupplyController {
	
	@Autowired
	private ITemplateSupplyService supplyService;
	/**
	 * 插入
	 * @param supply
	 * @return
	 */
	@RequestMapping(value = "/insertSupply", method = RequestMethod.POST)
	@ApiOperation(value="插入供应链厂商", httpMethod = "POST")
	public String insertSupply(TemplateSupply supply){
		return FastJsonUtil.objectToString(supplyService.insertSupply(supply));
	}
	/**
	 * 查询
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/querySupplyByCondition", method = RequestMethod.POST)
	@ApiOperation(value = "多条件分页查询供应链厂商", httpMethod = "POST")
	public String querySupplyByCondition(TemplateSupply templateSupply, String queryDate,
			@RequestParam(value = "page", required = false)Integer page, 
			@RequestParam(value = "limit", required = false)Integer limit) throws Exception{
		return FastJsonUtil.objectToString(supplyService.querySupplyByConditiion(templateSupply, queryDate, page, limit));
	}
	/**
	 * 更新
	 * @param supply
	 * @return
	 */
	@RequestMapping(value = "/updateSupply", method = RequestMethod.PUT)
	@ApiOperation(value = "更新供应链厂商", httpMethod = "PUT")
	public String updateSupply(TemplateSupply supply){
		return FastJsonUtil.objectToString(supplyService.updateSupply(supply));
	}
	/**
	 * 删除
	 * @param supply
	 * @return
	 */
	@RequestMapping(value = "/deleteSupply", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除供应链厂商", httpMethod = "DELETE")
	public String deleteSupply(Integer id){
		return FastJsonUtil.objectToString(supplyService.deleteSupply(id));
	}
	/**
	 * 获取供应链菜单
	 * @return
	 */
	@RequestMapping(value = "/getSupplyMenu", method = RequestMethod.GET)
	@ApiOperation(value = "获取供应链菜单", httpMethod = "GET")
	public String getSupplyMenu() {
		return FastJsonUtil.objectToString(supplyService.getSupplyMenu());
	}
	
	/**
	 *	
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/querySupplyInfo", method = RequestMethod.POST)
	@ApiOperation(value = "按ID查询供应商信息", httpMethod = "POST")
	public String querySupplyInfo(String id) {
		ResultUtil result = supplyService.querySupplyInfo(id);
		return FastJsonUtil.objectToString(result);
	}
	
	/**
     * 模板供应商数据导入
     * @param file
     * @param request
     * @throws Exception 
     */
    @RequestMapping(value = "/uploadTemplateSupplyExcel", method = RequestMethod.POST)
    @ApiOperation(value = "模板供应商数据导入", httpMethod = "POST")
    public String uploadTemplateSupplyExcel(@RequestParam("file") MultipartFile file) throws Exception {
    	ResultUtil result = supplyService.uploadTemplateSupplyData(file);
    	return FastJsonUtil.objectToString(result);
    }
}
