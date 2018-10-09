package com.data.model;

import java.io.Serializable;

/**
 *  订单接收model
 * @author Alex
 *
 */
public class OrderModel implements Serializable {

	private static final long serialVersionUID = -4580505642033780622L;
	
	private String sysId;
	
	private String sysName;
	
	private String region;
	
	private String provinceArea;
	
	private String storeCode;
	
	private String storeName;
	
	private String simpleCode;
	
	private String simpleBarCode;
	
	private String simpleName;
	
	private String receiptCode;

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

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getProvinceArea() {
		return provinceArea;
	}

	public void setProvinceArea(String provinceArea) {
		this.provinceArea = provinceArea;
	}

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

	public String getSimpleCode() {
		return simpleCode;
	}

	public void setSimpleCode(String simpleCode) {
		this.simpleCode = simpleCode;
	}

	public String getSimpleBarCode() {
		return simpleBarCode;
	}

	public void setSimpleBarCode(String simpleBarCode) {
		this.simpleBarCode = simpleBarCode;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	public String getReceiptCode() {
		return receiptCode;
	}

	public void setReceiptCode(String receiptCode) {
		this.receiptCode = receiptCode;
	}
}
