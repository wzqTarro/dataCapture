package com.data.service;

import javax.servlet.http.HttpServletResponse;

/**
 * 文件上传下载服务类
 * @author Ale'x
 *
 */
public interface IFileService {

	/**
	 * 根据类型下载模板
	 * @param type
	 */
	void downloadTemplateExcel(String type, HttpServletResponse response) throws Exception;
}
