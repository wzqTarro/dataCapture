package com.zhongmin.service;

import com.zhongmin.bean.Supply;
import com.zhongmin.constant.PageRecord;

public interface ISupplyService {

	/**
	 * 插入
	 * @param supply
	 * @return
	 */
	int insert(Supply supply);
	/**
	 * 更新
	 * @param supply
	 * @return
	 */
	int update(Supply supply);
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	int delete(Integer id);
	/**
	 * 分页
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	PageRecord<Supply> queryByConditiion(String pageNum, String pageSize) throws Exception ;
}
