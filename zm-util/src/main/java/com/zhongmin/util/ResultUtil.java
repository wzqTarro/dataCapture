package com.zhongmin.util;

import java.io.Serializable;

import com.zhongmin.constant.TipsEnum;


/**
 * 返回响应bean
 * @author Tarro
 *
 */
public class ResultUtil implements Serializable {
	
	private static final long serialVersionUID = 130769520693217001L;

	/**00:成功  99:失败**/
	private String respCode;
	
	private Object respData;
	
	private String respMsg;
	
	public ResultUtil() {}

	public ResultUtil(String respCode) {
		this.respCode = respCode;
	}
	
	public ResultUtil(String respCode, String respMsg) {
		this.respCode = respCode;
		this.respMsg = respMsg;
	}
	
	public ResultUtil(String respCode, Object respData, String respMsg) {
		this.respCode = respCode;
		this.respData = respData;
		this.respMsg = respMsg;
	}

	public String getRespCode() {
		return respCode;
	}

	public Object getRespData() {
		return respData;
	}

	public String getRespMsg() {
		return respMsg;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public void setRespData(Object respData) {
		this.respData = respData;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}
	
	/**成功**/
	public static ResultUtil success(Object data, String msg) {
		ResultUtil result = new ResultUtil("00");
		result.setRespData(data);
		result.setRespMsg(msg);
		return result;
	}
	
	/**自定义成功**/
	public static ResultUtil success(String msg) {
		ResultUtil result = new ResultUtil("00");
		result.setRespMsg(msg);
		return result;
	}
	
	/**默认成功**/
	public static ResultUtil success() {
		ResultUtil result = new ResultUtil("00", TipsEnum.OPERATE_SUCCESS.getValue());
		return result;
	}
	
	/**成功**/
	public static ResultUtil success(Object data) {
		ResultUtil result = new ResultUtil("00", TipsEnum.OPERATE_SUCCESS.getValue());
		result.setRespData(data);
		return result;
	}
	
	/**自定义错误**/
	public static ResultUtil error(String msg) {
		ResultUtil result = new ResultUtil("99");
		result.setRespMsg(msg);
		return result;
	}
	
	/**错误**/
	public static ResultUtil error(String msg, Object data) {
		ResultUtil result = new ResultUtil("99");
		result.setRespMsg(msg);
		result.setRespData(data);
		return result;
	}
	
	/**默认错误**/
	public static ResultUtil error() {
		ResultUtil result = new ResultUtil("99", TipsEnum.OPERATE_ERROR.getValue());
		return result;
	}
	
	/**错误**/
	public static ResultUtil error(Object data) {
		ResultUtil result = new ResultUtil("99", TipsEnum.OPERATE_ERROR.getValue());
		result.setRespData(data);
		return result;
	}
}
