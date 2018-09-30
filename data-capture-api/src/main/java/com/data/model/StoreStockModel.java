package com.data.model;

import java.io.Serializable;
import java.util.List;

import com.data.bean.Stock;
/**
 * 库存按门店分组
 * @author HCH
 *
 */
public class StoreStockModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5648491728669752342L;

	String storeCode;
	
	String storeName;
	
	List<Stock> stockList;

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public List<Stock> getStockList() {
		return stockList;
	}

	public void setStockList(List<Stock> stockList) {
		this.stockList = stockList;
	}
	
	
}
