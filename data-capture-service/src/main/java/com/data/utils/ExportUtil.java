package com.data.utils;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.data.constant.enums.ICommonEnum;
import com.data.dto.CommonDTO;
import com.data.service.impl.CommonServiceImpl;

@Component
public class ExportUtil extends CommonServiceImpl{

	/**
	 * 拼接查询字段
	 * @param stockNameStr
	 * @param common
	 * @param output
	 * @return
	 * @throws Exception
	 */
	public <T extends ICommonEnum> String[] joinColumn(Class<T> clazz, StringBuilder builder, String[] header, CommonDTO common) throws Exception {
		String[] methodNameArray = new String[header.length];
		int methodIndex = 0;
		// 拼接查询字段
		for (String s : header) {
			T e = EnumUtil.getEnumByMsg(clazz, s.trim());
			if (e == null) {
				throw new Exception(s.trim()+"列名错误");
			}
			builder.append(e.getColumn());
			builder.append(",");
			methodNameArray[methodIndex] = e.getMethodName(); 
			methodIndex++;
		}
		builder.deleteCharAt(builder.length() - 1);		
		return methodNameArray;
	}
	/**
	 * 导出excel
	 * @throws Exception 
	 */
	public <T> void exportExcel(Class<T> clazz, String startDate, String endDate, String sysId, String column, String queryMapper,
			String title, String[] methodNameArray, String[] header, OutputStream output) throws Exception {
		Map<String, Object> param = new HashMap<>(2);
		param.put("column", column);
		param.put("startDate", startDate);
		param.put("endDate", endDate);
		param.put("sysId", sysId);
		List<T> dataList = queryListByObject(queryMapper, param);
		
		ExcelUtil<T> excelUtil = new ExcelUtil<>();
		excelUtil.exportCustom2007(title, header, methodNameArray, dataList, output);
	}
}
