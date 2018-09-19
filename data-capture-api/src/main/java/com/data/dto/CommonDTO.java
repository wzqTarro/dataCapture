package com.data.dto;

import java.io.Serializable;

public class CommonDTO implements Serializable {

	private static final long serialVersionUID = 1685003548696455015L;
	
	//
	private String accessToken;
	// 开始时间
	private String startDate;
	// 结束时间
	private String endDate;
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	
}
