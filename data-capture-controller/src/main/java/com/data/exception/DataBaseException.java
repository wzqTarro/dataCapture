package com.data.exception;

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

	@ResponseBody
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.OK)
	public String resolveException(Exception e) {
		
		ResultUtil result = new ResultUtil();
		if(e instanceof DataException) {
			String errorCode = e.getMessage();
			String errorMessage = PropertiesUtil.getMessage(errorCode);
			if(CommonUtil.isNotBlank(errorMessage)) {
				result.setCode(CodeEnum.RESPONSE_99_CODE.getValue());
				result.setMsg(errorMessage);
			}
			
		}
		return FastJsonUtil.objectToString(result);
	}
}
