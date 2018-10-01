package com.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * 大区菜单
 * @author Administrator
 *
 */
public class RegionMenu implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2022485504416143366L;

	// 大区
	String region;
	
	// 省区
	List<String> provinceAreaList;

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public List<String> getProvinceAreaList() {
		return provinceAreaList;
	}

	public void setProvinceAreaList(List<String> provinceAreaList) {
		this.provinceAreaList = provinceAreaList;
	}


	
}
