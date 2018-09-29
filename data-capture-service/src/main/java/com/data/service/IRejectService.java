package com.data.service;

import java.io.OutputStream;

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
	public ResultUtil getRejectByWeb(String queryDate, String sysId, Integer limit);
	/**
	 * 多条件查询退单数据
	 * @param common
	 * @param reject
	 * @param page
	 * @param limit
	 * @return
	 */
	public ResultUtil getRejectByParam(CommonDTO common, Reject reject, Integer page, Integer limit) throws Exception ;
	/**
	 * 选择字段导出Excel
	 * @param stockNameStr
	 * @param common
	 * @param output
	 * @return
	 * @throws Exception
	 */
	ResultUtil exportRejectExcel(String sysId, String stockNameStr, CommonDTO common, OutputStream output) throws Exception;
}
