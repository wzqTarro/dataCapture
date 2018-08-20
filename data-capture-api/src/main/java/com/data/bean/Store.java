package com.data.bean;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class Store implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5897200404726879461L;

	private Integer id;
	// 门店编码
    private String storeCode;
    // 供应链订单店称
    private String orderStoreName;
    // 供应链退单店称
    private String returnStoreName;
    // 供应链销量店称
    private String saleStoreName;
    // 百亚标准门店名称
    private String standardStoreName;
    // 门店所在市场
    private String storeMarket;
    // 门店所在城市
    private String storeCity;
    // 网站名称
    private String sysName;
    // 门店负责人
    private String storeUser;
    // 物流模式
    private String logisticsModel;
    // 开业时间
    @JSONField(format = "yyyy-MM-dd")
    private Date practiceTime;
    // 配送商编码
    private String distributionCode;
    // 配送商名称
    private String distributionName;
    // 配送商负责人
    private String distributionUser;
    // 大区
    private String region;
    // 省区
    private String provinceArea;
    // 归属
    private String ascription;
    // 业绩归属
    private String ascriptionSole;

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

    public String getStoreUser() {
        return storeUser;
    }

    public void setStoreUser(String storeUser) {
        this.storeUser = storeUser == null ? null : storeUser.trim();
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