package com.data.model;

import java.io.Serializable;
import java.util.List;

import com.data.bean.Stock;

/**
 * 按省区分组
 * @author Administrator
 *
 */
public class ProvinceAreaStockModel implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1972335818011842048L;

	// 省区
	private String provinceArea;
	
	private List<Stock> stockList;

	public String getProvinceArea() {
		return provinceArea;
	}

	public void setProvinceArea(String provinceArea) {
		this.provinceArea = provinceArea;
	}

	public List<Stock> getStockList() {
		return stockList;
	}

	public void setStockList(List<Stock> stockList) {
		this.stockList = stockList;
	}
	
	
}
