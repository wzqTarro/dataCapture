package com.zhongmin.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zhongmin.constant.PageRecord;
import com.zhongmin.dao.ICommonDao;

@Repository
public class CommonDaoImpl implements ICommonDao {
	
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Override
	public <T> List<T> queryListByObject(String statement, Object parameter) {
		return sqlSessionTemplate.selectList(statement, parameter);
	}

	@Override
	public <T> PageRecord<T> queryPageByObject(String countStatement, String listStatement, Object parameter,
			int pageNum, int pageSize) {
		PageRecord<T> page = new PageRecord<>();
		page.setList(sqlSessionTemplate.selectList(listStatement, parameter));
		page.setPageTotal(sqlSessionTemplate.selectOne(countStatement, parameter));
		page.setPageNum((pageNum - 1) * pageSize);
		page.setPageSize(pageSize);
		return page;
	}

	@Override
	public int queryCountByObject(String statement, Object parameter) {
		return sqlSessionTemplate.selectOne(statement, parameter);
	}

	@Override
	public int insert(String statement, Object parameter) {
		return sqlSessionTemplate.insert(statement, parameter);
	}

	@Override
	public int update(String statement, Object parameter) {
		return sqlSessionTemplate.update(statement, parameter);
	}

	@Override
	public int delete(String statement, Object parameter) {
		return sqlSessionTemplate.delete(statement, parameter);
	}

	@Override
	public Object queryObjectByParameter(String statement, Object parameter) {
		return sqlSessionTemplate.selectOne(statement, parameter);
	}
	
	

}
