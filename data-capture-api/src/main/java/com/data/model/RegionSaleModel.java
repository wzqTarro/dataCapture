package com.data.model;

import java.io.Serializable;
import java.util.List;

import com.data.bean.Sale;

public class RegionSaleModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2239370351854681245L;
	private String region;
	private List<Sale> saleList;
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public List<Sale> getSaleList() {
		return saleList;
	}
	public void setSaleList(List<Sale> saleList) {
		this.saleList = saleList;
	}
}
