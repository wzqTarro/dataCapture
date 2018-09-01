package com.data.test;

import org.junit.Test;

import com.data.utils.CommonUtil;
import com.data.utils.EncryptUtil;

public class MyTest {

	@Test
	public void createPassword() {
		System.err.println("workNo: " + CommonUtil.createWorkNo());
		
		String password = "123456";
		System.err.println("password: " + EncryptUtil.Md5Encrypt(password));
		
	}
}
