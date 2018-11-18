package com.data.service;

import org.springframework.web.multipart.MultipartFile;

import com.data.bean.TemplateSupply;
import com.data.utils.ResultUtil;

public interface ITemplateSupplyService {

	/**
	 * 插入
	 * @param supply
	 * @return
	 */
	ResultUtil insertSupply(TemplateSupply supply);
	/**
	 * 更新
	 * @param supply
	 * @return
	 */
	ResultUtil updateSupply(TemplateSupply supply);
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	ResultUtil deleteSupply(Integer id);
	/**
	 * 分页
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	ResultUtil querySupplyByConditiion(TemplateSupply templateSupply, Integer page, Integer limit) throws Exception ;
	/**
	 * 获取供应链菜单
	 * @return
	 */
	ResultUtil getSupplyMenu();
	
	/**
	 * 按id查询供应商信息
	 * @param id
	 * @return
	 */
	ResultUtil querySupplyInfo(String id);
	
	/**
	 * 模板供应商导入
	 * @param file
	 * @return
	 * @throws Exception
	 */
	ResultUtil uploadTemplateSupplyData(MultipartFile file) throws Exception;
}
