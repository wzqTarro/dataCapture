package com.data.dto;

import java.io.Serializable;

public class CommonDTO implements Serializable {

	private static final long serialVersionUID = 1685003548696455015L;
	
	//
	private String accessToken;
	// 编号
	private int id;
	// 当前页号
	private int page;
	// 当前行数
	private int limit;
	// 开始时间
	private String startDate;
	// 结束时间
	private String endDate;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
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
