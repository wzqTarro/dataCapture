package com.data.service;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.data.bean.Reject;
import com.data.dto.CommonDTO;
import com.data.model.RejectModel;
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
	public ResultUtil getRejectByWeb(String queryDate, String sysId, Integer limit) throws IOException;
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
	void exportRejectExcel(String stockNameStr, CommonDTO common, Reject reject, OutputStream output) throws Exception;
	
	/**
	 * 查询退单报警集合列表
	 * @param reject
	 * @param page
	 * @param limit
	 * @return
	 */
	ResultUtil queryRejectAlarmList(RejectModel reject, Integer page, Integer limit) throws Exception;
	
	/**
	 * 退单报表导出
	 * @param reject
	 * @param response
	 */
	void rejectAlarmListExcel(RejectModel reject, HttpServletResponse response) throws Exception;
}
