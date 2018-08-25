package com.data.service;

import com.data.bean.Supply;
import com.data.utils.ResultUtil;

public interface ISupplyService {

	/**
	 * 插入
	 * @param supply
	 * @return
	 */
	ResultUtil insertSupply(Supply supply);
	/**
	 * 更新
	 * @param supply
	 * @return
	 */
	ResultUtil updateSupply(Supply supply);
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
