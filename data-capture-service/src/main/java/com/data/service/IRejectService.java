package com.data.service;

import com.data.bean.Reject;
import com.data.dto.CommonDTO;
import com.data.utils.ResultUtil;

public interface IRejectService {
	/**
	 * 抓取数据
	 * @param queryDate
	 * @param sysId
	 * @param page
	 * @param limit
	 * @return
	 */
	public ResultUtil getRejectByWeb(String queryDate, String sysId, Integer page, Integer limit);
	/**
	 * 多条件查询退单数据
	 * @param common
	 * @param reject
	 * @param page
	 * @param limit
	 * @return
	 */
	public ResultUtil getRejectByParam(CommonDTO common, Reject reject, Integer page, Integer limit) throws Exception ;
}
