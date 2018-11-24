package com.data.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.data.service.IFileService;

import io.swagger.annotations.ApiOperation;

/**
 * 文件下载上传控制器
 * @author Alex
 *
 */
@RestController
@RequestMapping("/file")
public class FileController {

	@Autowired
	private IFileService fileService;
	
	/**
	 * 按类型下载模板文件
	 * @param type
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/downloadTemplateExcel", method = RequestMethod.GET)
	@ApiOperation(value = "按类型下载模板文件", httpMethod = "GET")
	public void downloadTemplateExcel(String type, HttpServletResponse response) throws Exception {
		fileService.downloadTemplateExcel(type, response);
	}
}
