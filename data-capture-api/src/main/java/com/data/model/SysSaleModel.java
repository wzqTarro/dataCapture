package com.data.model;

import java.io.Serializable;
import java.util.List;

import com.data.bean.Sale;

public class SysSaleModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5010150980383229322L;

	private String sysId;
	
	private String sysName;
	
	private List<Sale> saleList;

	public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

	public String getSysName() {
		return sysName;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public List<Sale> getSaleList() {
		return saleList;
	}

	public void setSaleList(List<Sale> saleList) {
		this.saleList = saleList;
	}
	
	
}
