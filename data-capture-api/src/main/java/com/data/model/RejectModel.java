package com.data.model;

import java.io.Serializable;

/**
 * 退单接收model
 * @author Alex
 *
 */
public class RejectModel implements Serializable {

	private static final long serialVersionUID = 8154398323411288688L;
	
	private String sysId;
	
	private String sysName;
	
	private String region;
	
	private String provinceArea;
	
	private String rejectDepartmentId;
	
	private String rejectDepartmentName;
	
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

	public String getRejectDepartmentId() {
		return rejectDepartmentId;
	}

	public void setRejectDepartmentId(String rejectDepartmentId) {
		this.rejectDepartmentId = rejectDepartmentId;
	}

	public String getRejectDepartmentName() {
		return rejectDepartmentName;
	}

	public void setRejectDepartmentName(String rejectDepartmentName) {
		this.rejectDepartmentName = rejectDepartmentName;
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
