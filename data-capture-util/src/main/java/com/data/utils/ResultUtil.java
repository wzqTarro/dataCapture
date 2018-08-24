package com.data.utils;

import java.io.Serializable;
import java.util.Collections;

import com.data.constant.CodeEnum;
import com.data.constant.PageRecord;
import com.data.constant.TipsEnum;


/**
 * 返回响应bean
 * @author Tarro
 *
 */
public class ResultUtil implements Serializable {
	
	private static final long serialVersionUID = 130769520693217001L;

	/**00:成功  99:失败**/
	private String code;
	
	private Object data;
	
	private String msg;
	
	private long count;
	
	public ResultUtil() {}

	public ResultUtil(String code) {
		this.code = code;
	}
	
	public ResultUtil(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public ResultUtil(String code, Object data, String msg) {
		this.code = code;
		this.data = data;
		this.msg = msg;
	}

	
	/**成功**/
	public static ResultUtil success(Object data, String msg) {
		ResultUtil result = new ResultUtil(CodeEnum.RESPONSE_00_CODE.getValue());
		if (null == data) {
			result.setData(Collections.EMPTY_LIST);
		}else {
			result.setData(data);	
		}
		result.setMsg(msg);
		return result;
	}
	
	public static ResultUtil success(Object data, long count, String msg) {
		ResultUtil result = new ResultUtil(CodeEnum.RESPONSE_00_CODE.getValue());
		if (null == data) {
			result.setData(Collections.EMPTY_LIST);
		}else {
			result.setData(data);	
		}
		result.setMsg(msg);
		result.setCount(count);
		return result;
	}
	
	public static <T> ResultUtil success(PageRecord<T> data) {
		ResultUtil result = new ResultUtil(CodeEnum.RESPONSE_00_CODE.getValue(), TipsEnum.OPERATE_SUCCESS.getValue());
		if (null == data) {
			result.setData(Collections.EMPTY_LIST);
		}else {
			result.setData(data.getList());	
		}
		result.setCount(data.getPageTotal());
		return result;
	}
	
	/**自定义成功**/
	public static ResultUtil success(String msg) {
		ResultUtil result = new ResultUtil(CodeEnum.RESPONSE_00_CODE.getValue());
		result.setMsg(msg);
		return result;
	}
	
	/**默认成功**/
	public static ResultUtil success() {
		ResultUtil result = new ResultUtil(CodeEnum.RESPONSE_00_CODE.getValue(), TipsEnum.OPERATE_SUCCESS.getValue());
		result.setData(Collections.EMPTY_LIST);
		return result;
	}
	
	/**成功**/
	public static ResultUtil success(Object data) {
		ResultUtil result = new ResultUtil(CodeEnum.RESPONSE_00_CODE.getValue(), TipsEnum.OPERATE_SUCCESS.getValue());
		if (null == data) {
			result.setData(Collections.EMPTY_LIST);
		}else {
			result.setData(data);	
		}
		return result;
	}
	
	/**自定义错误**/
	public static ResultUtil error(String msg) {
		ResultUtil result = new ResultUtil(CodeEnum.RESPONSE_99_CODE.getValue());
		result.setMsg(msg);
		return result;
	}
	
	/**错误**/
	public static ResultUtil error(String msg, Object data) {
		ResultUtil result = new ResultUtil(CodeEnum.RESPONSE_99_CODE.getValue());
		result.setMsg(msg);
		result.setData(data);
		return result;
	}
	
	/**默认错误**/
	public static ResultUtil error() {
		ResultUtil result = new ResultUtil(CodeEnum.RESPONSE_99_CODE.getValue(), TipsEnum.OPERATE_ERROR.getValue());
		return result;
	}
	
	/**错误**/
	public static ResultUtil error(Object data) {
		ResultUtil result = new ResultUtil(CodeEnum.RESPONSE_99_CODE.getValue(), TipsEnum.OPERATE_ERROR.getValue());
		result.setData(data);
		return result;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
