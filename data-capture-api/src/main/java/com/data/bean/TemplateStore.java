package com.data.bean;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 库存模板实体类
 * @author Alex
 *
 */
public class TemplateStore implements Serializable {

	private static final long serialVersionUID = -3480747372133854332L;

	private Integer id;

    private String storeCode;

    private String orderStoreName;

    private String returnStoreName;

    private String saleStoreName;

    private String standardStoreName;

    private String storeMarket;

    private String storeCity;

    private String sysName;
    
    private String sysId;

    // 门店负责人
    private String storeManager;

    private String logisticsModel;

    @JSONField(format = "yyyy-MM-dd")
    private Date practiceTime;

    private String distributionCode;

    private String distributionName;

    private String distributionUser;

    private String region;

    private String provinceArea;

    private String ascription;

    private String ascriptionSole;

    public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode == null ? null : storeCode.trim();
    }

    public String getOrderStoreName() {
        return orderStoreName;
    }

    public void setOrderStoreName(String orderStoreName) {
        this.orderStoreName = orderStoreName == null ? null : orderStoreName.trim();
    }

    public String getReturnStoreName() {
        return returnStoreName;
    }

    public void setReturnStoreName(String returnStoreName) {
        this.returnStoreName = returnStoreName == null ? null : returnStoreName.trim();
    }

    public String getSaleStoreName() {
        return saleStoreName;
    }

    public void setSaleStoreName(String saleStoreName) {
        this.saleStoreName = saleStoreName == null ? null : saleStoreName.trim();
    }

    public String getStandardStoreName() {
        return standardStoreName;
    }

    public void setStandardStoreName(String standardStoreName) {
        this.standardStoreName = standardStoreName == null ? null : standardStoreName.trim();
    }

    public String getStoreMarket() {
        return storeMarket;
    }

    public void setStoreMarket(String storeMarket) {
        this.storeMarket = storeMarket == null ? null : storeMarket.trim();
    }

    public String getStoreCity() {
        return storeCity;
    }

    public void setStoreCity(String storeCity) {
        this.storeCity = storeCity == null ? null : storeCity.trim();
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName == null ? null : sysName.trim();
    }

    

    public String getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(String storeManager) {
		this.storeManager = storeManager;
	}

	public String getLogisticsModel() {
        return logisticsModel;
    }

    public void setLogisticsModel(String logisticsModel) {
        this.logisticsModel = logisticsModel == null ? null : logisticsModel.trim();
    }

    public Date getPracticeTime() {
        return practiceTime;
    }

    public void setPracticeTime(Date practiceTime) {
        this.practiceTime = practiceTime;
    }

    public String getDistributionCode() {
        return distributionCode;
    }

    public void setDistributionCode(String distributionCode) {
        this.distributionCode = distributionCode == null ? null : distributionCode.trim();
    }

    public String getDistributionName() {
        return distributionName;
    }

    public void setDistributionName(String distributionName) {
        this.distributionName = distributionName == null ? null : distributionName.trim();
    }

    public String getDistributionUser() {
        return distributionUser;
    }

    public void setDistributionUser(String distributionUser) {
        this.distributionUser = distributionUser == null ? null : distributionUser.trim();
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region == null ? null : region.trim();
    }

    public String getProvinceArea() {
        return provinceArea;
    }

    public void setProvinceArea(String provinceArea) {
        this.provinceArea = provinceArea == null ? null : provinceArea.trim();
    }

    public String getAscription() {
        return ascription;
    }

    public void setAscription(String ascription) {
        this.ascription = ascription == null ? null : ascription.trim();
    }

    public String getAscriptionSole() {
        return ascriptionSole;
    }

    public void setAscriptionSole(String ascriptionSole) {
        this.ascriptionSole = ascriptionSole == null ? null : ascriptionSole.trim();
    }
}