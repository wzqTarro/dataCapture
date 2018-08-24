package com.data.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.Supply;
import com.data.constant.PageRecord;
import com.data.service.ISupplyService;
import com.data.utils.FastJsonUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/supply")
@Api(tags = "供应链服务接口")
public class SupplyController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ISupplyService supplyService;
	/**
	 * 插入
	 * @param supply
	 * @return
	 */
	@RequestMapping(value = "/insertSupply", method = RequestMethod.POST)
	@ApiOperation(value="插入供应链厂商", httpMethod = "POST")
	public String insertSupply(Supply supply){
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
	public String querySupplyByCondition(@RequestBody String param) throws Exception{
		return FastJsonUtil.objectToString(supplyService.querySupplyByConditiion(param));
	}
	/**
	 * 更新
	 * @param supply
	 * @return
	 */
	@RequestMapping(value = "/updateSupply", method = RequestMethod.PUT)
	@ApiOperation(value = "更新供应链厂商", httpMethod = "PUT")
	public String updateSupply(Supply supply){
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
}
