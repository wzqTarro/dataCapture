package com.data.utils;

import com.data.constant.enums.ICommonEnum;

public class EnumUtil {

	public static <T extends ICommonEnum> T getEnumByMsg(Class<T> clazz, String msg) {
		for (T e : clazz.getEnumConstants()) {
			if (e.getMsg().equals(msg)) {
				return e;
			}
		}
		return null;
	}
}
