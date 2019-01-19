package com.data.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.data.constant.enums.CodeEnum;
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
	//@ResponseStatus(HttpStatus.OK)
	public String resolveException(Exception e) {
        
//        StringWriter stringWriter = new StringWriter();
//        PrintWriter printWriter = new PrintWriter(stringWriter);
//        e.printStackTrace(printWriter);
//        String exceptionString = stringWriter.toString();
//        String separator = System.getProperty("line.separator") + "\t";
//        int i = exceptionString.indexOf(separator);
//        String errCause = "，详细错误信息：" + exceptionString.substring(0, i) + ", " + exceptionString.substring(i + 3, exceptionString.indexOf(separator, i + 6));
//        int j;
//        String cause = "Caused by:";
//        if ((j = exceptionString.lastIndexOf(cause)) != -1)
//            errCause += "；" + exceptionString.substring(j, exceptionString.indexOf(separator, j + 10));
//		logger.error("---->>>>{}<<<<-----", errCause);
		
		ResultUtil result = new ResultUtil();
		String errorCode, errorMessage;
		if(e instanceof DataException) {
			errorCode = e.getMessage();
			errorMessage = PropertiesUtil.getMessage(errorCode);
			logger.error("--->>>异常编号为: {}, 异常信息: {}<<<---", errorCode, errorMessage);
			if(CommonUtil.isNotBlank(errorMessage)) {
				result.setCode(CodeEnum.RESPONSE_99_CODE.value());
				result.setMsg(errorMessage);
			}
			
		} else if(e instanceof GlobalException) {
			errorCode = ((GlobalException) e).getErrorCode();
			errorMessage = ((GlobalException) e).getErrorMsg();
			logger.error("--->>>异常编号为: {}, 异常信息: {}<<<---", errorCode, errorMessage);
			if(CommonUtil.isNotBlank(errorCode) && CommonUtil.isNotBlank(errorMessage)) {
				result.setCode(errorCode);
				result.setMsg(errorMessage);
			}
		} else if(e instanceof GetDataException) {
			result.setCode(CodeEnum.RESPONSE_99_CODE.value());
			result.setMsg(e.getMessage());
		} else {
			result.setCode(CodeEnum.RESPONSE_99_CODE.value());
			result.setMsg(CodeEnum.RESPONSE_99_DESC.value());
		}
		return FastJsonUtil.objectToString(result);
	}
	
	/**
	 * 认证异常响应处理
	 * @param e
	 * @return
	 */
	@ResponseBody
	@ExceptionHandler(AuthException.class)
	//@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public String resolveAuthException(AuthException e) {
		ResultUtil result = new ResultUtil();
		String errorCode, errorMessage;
		errorCode = e.getMessage();
		errorMessage = PropertiesUtil.getMessage(errorCode);
		logger.error("--->>>异常编号为: {}, 异常信息: {}<<<---", errorCode, errorMessage);
		if (CommonUtil.isNotBlank(errorMessage)) {
			result.setCode(CodeEnum.RESPONSE_01_CODE.value());
			result.setMsg(errorMessage);
		}
		return FastJsonUtil.objectToString(result);
	}
}
