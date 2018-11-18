package com.data.service;

import org.springframework.web.multipart.MultipartFile;

import com.data.bean.SimpleCode;
import com.data.utils.ResultUtil;

public interface ISimpleCodeService {

	/**
	 * 多条件分页查询
	 * @param param
	 * @return
	 */
	ResultUtil getSimpleCodeByParam(SimpleCode simpleCode, Integer page, Integer limit)throws Exception ;
	/**
	 * 更新
	 * @param simpleCode
	 * @return
	 */
	ResultUtil updateSimpleCode(SimpleCode simpleCode);
	/**
	 * 插入
	 * @param simpleCode
	 * @return
	 */
	ResultUtil insertSimpleCode(SimpleCode simpleCode);
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	ResultUtil deleteSimpleCode(int id);
	
	/**
	 * 按id查标准条码的详情
	 * @param id
	 * @return
	 */
	ResultUtil querySimpleCodeInfo(String id);
	
	/**
	 * 条码数据导入
	 * @param file
	 * @return
	 * @throws Exception
	 */
	ResultUtil uploadSimpleCodeData(MultipartFile file) throws Exception;
}
