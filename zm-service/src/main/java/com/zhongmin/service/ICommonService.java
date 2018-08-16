package com.zhongmin.service;

import java.util.List;

import com.zhongmin.constant.PageRecord;

/**
 * 公共服务层接口
 * @author Tarro
 *
 */
public interface ICommonService {

	/**
	 * 查询单个数据
	 * @param statement
	 * @param parameter
	 * @return
	 */
	Object queryObjectByParameter(String statement, Object parameter);
	
	/**
	 * 查询列表
	 * @param statement
	 * @param parameter
	 * @return
	 */
	<T> List<T> queryListByObject(String statement, Object parameter);
	
	/**
	 * 分页查询返回分页bean
	 * @param statement
	 * @param parameter
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	<T> PageRecord<T> queryPageByObject(String countStatement, String listStatement, Object parameter, String pageNum, String pageSize) throws Exception;
	
	/**
	 * 查询总数
	 * @param statement
	 * @param parameter
	 * @return
	 */
	int queryCountByObject(String statement, Object parameter);

	int insert(String statement, Object parameter);
	
	int update(String statement, Object parameter);
	
	int delete(String statement, Object parameter);
}
