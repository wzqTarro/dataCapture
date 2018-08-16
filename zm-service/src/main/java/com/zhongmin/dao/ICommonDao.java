package com.zhongmin.dao;

import java.util.List;

import com.zhongmin.constant.PageRecord;



public interface ICommonDao {
	/**
	 * 查询单个
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
	//<T> PageRecord<T> queryPageByObject(String countStatement, String listStatement, Object parameter, int pageNum, int pageSize);
	
	/**
	 * 物理分页返回分页bean
	 * @param countStatement
	 * @param listStatement
	 * @param parameter
	 * @return
	 */
	<T> PageRecord<T> queryPageByObject(String countStatement, String listStatement, Object parameter, int pageNum, int pageSize);
	
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
