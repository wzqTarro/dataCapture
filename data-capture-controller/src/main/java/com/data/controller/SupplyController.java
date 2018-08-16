package com.data.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.data.bean.Supply;
import com.data.constant.PageRecord;
import com.data.service.ISupplyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/supply")
@Api(value = "供应链服务接口")
public class SupplyController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ISupplyService supplyService;
	/**
	 * 插入
	 * @param supply
	 * @return
	 */
	@PostMapping("/insertSupply")
	@ApiOperation(value="插入供应链厂商", httpMethod = "POST")
	public int insertSupply(Supply supply){
		return supplyService.insert(supply);
	}
	/**
	 * 查询
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/querySupplyByCondition")
	@ApiOperation(value = "分页查询供应链厂商", httpMethod = "GET")
	public PageRecord<Supply> querySupplyByCondition(String pageNum, String pageSize) throws Exception{
		return supplyService.queryByConditiion(pageNum, pageSize);
	}
	/**
	 * 更新
	 * @param supply
	 * @return
	 */
	@PutMapping("/updateSupply")
	@ApiOperation(value = "更新供应链厂商", httpMethod = "PUT")
	public int updateSupply(Supply supply){
		return supplyService.update(supply);
	}
	/**
	 * 删除
	 * @param supply
	 * @return
	 */
	@DeleteMapping("/deleteSupply")
	@ApiOperation(value = "删除供应链厂商", httpMethod = "DELETE")
	public int deleteSupply(Integer id){
		return supplyService.delete(id);
	}
}
