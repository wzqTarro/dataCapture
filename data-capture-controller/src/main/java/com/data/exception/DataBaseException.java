package com.data.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.data.constant.CodeEnum;
import com.data.utils.CommonUtil;
import com.data.utils.FastJsonUtil;
import com.data.utils.MessageUtil;
import com.data.utils.ResultUtil;

/**
 * 异常控制类
 * @author Alex
 *
 */
@ControllerAdvice
public class DataBaseException {

	@ExceptionHandler(Exception.class)
	public String resolveException(HttpServletRequest request, HttpServletResponse response, Exception e) {
		
		ResultUtil result = new ResultUtil();
		if(e instanceof CustomeException) {
			String errorCode = e.getMessage();
			String errorMessage = MessageUtil.getMessage(errorCode);
			if(CommonUtil.isNotBlank(errorMessage)) {
				result.setCode(CodeEnum.RESPONSE_99_CODE.getValue());
				result.setMsg(errorMessage);
			}
		}
		return FastJsonUtil.objectToString(result);
	}
}
