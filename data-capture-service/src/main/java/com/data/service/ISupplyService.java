package com.data.service;

import com.data.bean.TemplateSupply;
import com.data.utils.ResultUtil;

public interface ISupplyService {

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
	ResultUtil querySupplyByConditiion(String param) throws Exception ;
}
