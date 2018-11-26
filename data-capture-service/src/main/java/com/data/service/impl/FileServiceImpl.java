package com.data.service.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.data.constant.enums.CodeEnum;
import com.data.exception.GlobalException;
import com.data.service.IFileService;
import com.data.utils.CommonUtil;

@Service
public class FileServiceImpl extends CommonServiceImpl implements IFileService {
	
	private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

	@Override
	public void downloadTemplateExcel(String type, HttpServletResponse response) throws Exception {
		logger.info("--->>>文件下载类型为{} <<<---", type);
		if(CommonUtil.isBlank(type)) {
			throw new GlobalException(CodeEnum.RESPONSE_99_CODE.value(), "下载模板类型不能为空! ");
		}
		String title = matchFileName(type) + "上传模板";
		InputStream in = FileServiceImpl.class.getClassLoader().getResourceAsStream("template/" + type + "_template_excel.xlsx");
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title, "UTF-8") + ".xlsx");
		OutputStream out = response.getOutputStream();
		byte[] buf = new byte[1024];
		int len = 0;
		while((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}
	
	private final String matchFileName(String type) {
		String fileName = "";
		switch(type) {
		case "order" : {
			fileName = "订单";
			break;
		}
		case "promotion_detail": {
			fileName = "促销明细";
			break;
		}
		case "reject": {
			fileName = "退单";
			break;
		}
		case "sale": {
			fileName = "销售";
			break;
		}
		case "simple_code": {
			fileName = "码表";
			break;
		}
		case "stock": {
			fileName = "库存";
			break;
		}
		case "template_product": {
			fileName = "商品";
			break;
		}
		case "template_store": {
			fileName = "门店";
			break;
		}
		case "template_supply": {
			fileName = "供应链";
			break;
		}
		}
		return fileName;
	}

}
