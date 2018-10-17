package com.data.model;

import java.io.Serializable;
import java.util.List;

import com.data.bean.Sale;

public class StoreSaleModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3586660465251676953L;

	private String storeCode;
	
	private String storeName;
	
	private List<Sale> saleList;

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

	public List<Sale> getSaleList() {
		return saleList;
	}

	public void setSaleList(List<Sale> saleList) {
		this.saleList = saleList;
	}
	
}
