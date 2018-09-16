package com.data.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.data.constant.CodeEnum;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.PropertiesUtil;
import com.data.utils.ResultUtil;

/**
 * 异常控制类
 * @author Alex
 *
 */
@ControllerAdvice
public class DataBaseException {
	
	private static Logger logger = LoggerFactory.getLogger(DataBaseException.class);

	@ResponseBody
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.OK)
	public String resolveException(Exception e) {
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String exceptionString = stringWriter.toString();
        String separator = System.getProperty("line.separator") + "\t";
        int i = exceptionString.indexOf(separator);
        String errCause = "，详细错误信息：" + exceptionString.substring(0, i) + ", " + exceptionString.substring(i + 3, exceptionString.indexOf(separator, i + 6));
        int j;
        String cause = "Caused by:";
        if ((j = exceptionString.lastIndexOf(cause)) != -1)
            errCause += "；" + exceptionString.substring(j, exceptionString.indexOf(separator, j + 10));
		logger.info("---->>>>{}<<<<-----", errCause);
		
		ResultUtil result = new ResultUtil();
		if(e instanceof DataException) {
			String errorCode = e.getMessage();
			String errorMessage = PropertiesUtil.getMessage(errorCode);
			logger.info("--->>>异常编号为: {}, 异常信息: {}<<<---", errorCode, errorMessage);
			if(CommonUtil.isNotBlank(errorMessage)) {
				result.setCode(CodeEnum.RESPONSE_99_CODE.getValue());
				result.setMsg(errorMessage);
			}
			
		} else {
			result.setCode(CodeEnum.RESPONSE_99_CODE.getValue());
			result.setMsg(e.getMessage());
		}
		return FastJsonUtil.objectToString(result);
	}
}
