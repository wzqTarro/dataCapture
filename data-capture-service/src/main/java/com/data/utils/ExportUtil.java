package com.data.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.data.constant.enums.ICommonEnum;
import com.data.dto.CommonDTO;
import com.data.exception.DataException;

@Component
public class ExportUtil {
	/**
	 * 自选导出Excel表条件判断
	 * @param common
	 * @param t
	 * @param stockNameStr
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	public <T> void exportConditionJudge(CommonDTO common, T t, String stockNameStr) throws Exception {
		if (null == common || CommonUtil.isBlank(common.getStartDate()) || CommonUtil.isBlank(common.getEndDate())) {
			throw new DataException("534");
		}
		if (CommonUtil.isBlank(stockNameStr)) {
			throw new DataException("540");
		}
		if (t == null) {
			throw new DataException("538");
		}
		Class clazz = t.getClass();
		Method method = clazz.getMethod("getSysId", new Class[]{});
		Object sysId = method.invoke(t, new Object[]{});
		if (CommonUtil.isBlank(sysId)) {
			throw new DataException("538");
		}
	}
	/**
	 * 拼接查询字段
	 * @param stockNameStr
	 * @param common
	 * @param output
	 * @return
	 * @throws Exception
	 */
	public <T extends ICommonEnum> Map<String, Object> joinColumn(Class<T> clazz, String[] header) throws Exception {
		StringBuilder builder = new StringBuilder();
		builder.append(" ");
		String[] methodNameArray = new String[header.length];
		int methodIndex = 0;
		// 拼接查询字段
		for (String s : header) {
			T e = EnumUtil.getEnumByMsg(clazz, s.trim());
			if (e == null) {
				throw new DataException(s.trim()+"列名错误");
			}
			builder.append(e.getColumn());
			builder.append(",");
			methodNameArray[methodIndex] = e.getMethodName(); 
			methodIndex++;
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append(" ");
		Map<String, Object> map = new HashMap<>(2);
		map.put("methodNameArray", methodNameArray);
		map.put("column", builder.toString());
		return map;
	}
	/**
	 * 自选字段查询参数
	 * @throws Exception 
	 */
	public Map<String, Object> joinParam(String startDate, String endDate, String column, String sysId) throws Exception {
		Map<String, Object> param = new HashMap<>(4);
		param.put("column", column);
		if (CommonUtil.isNotBlank(startDate) && CommonUtil.isNotBlank(endDate)) {
			param.put("startDate", startDate.trim());
			param.put("endDate", endDate.trim());
		}
		param.put("sysId", sysId);
		return param;
	}
}
