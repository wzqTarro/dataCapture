package com.data.model;

import java.io.Serializable;
import java.util.List;

import com.data.bean.Stock;

/**
 * 
 * 按大区分组
 *
 */
public class RegionStockModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4092162927732202424L;

	private String region;
	
	private List<Stock> stockList;

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public List<Stock> getStockList() {
		return stockList;
	}

	public void setStockList(List<Stock> stockList) {
		this.stockList = stockList;
	}
	
	
}
