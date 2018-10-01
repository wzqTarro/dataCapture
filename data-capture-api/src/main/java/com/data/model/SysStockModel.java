package com.data.model;

import java.io.Serializable;
import java.util.List;

import com.data.bean.Stock;

/**
 * 
 * 库存按系统分组
 * @author Tarro
 *
 */
public class SysStockModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8704714362481345148L;

	private String sysId;
	
	private String sysName;
	
	private List<Stock> stockList;

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

	public List<Stock> getStockList() {
		return stockList;
	}

	public void setStockList(List<Stock> stockList) {
		this.stockList = stockList;
	}
	
	
}
