package com.data.bean;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
/**
 * 销售数据
 * @author Tarro
 *
 */
public class Sale implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -6501670935984328986L;

	private Integer id;
	// 网站编号
    private Integer sysId;
    // 网站名称
    private String sysName;
    // 门店编号
    private String storeCode;
    // 门店名称
    private String storeName;
    // 大区
    private String region;
    // 省区
    private String provinceArea;
    // 归属
    private String ascription;
    // 业绩归属
    private String ascriptionSole;
    // 单品编码
    private String simpleCode;
    // 单品条码
    private String simpleBarCode;
    // 存货编码
    private String stockCode;
    // 单品名称
    private String simpleName;
    // 品牌
    private String brand;
    // 系列
    private String series;
    // 日夜
    private String dayNight;
    // 材质
    private String material;
    // 片数
    private Integer piecesNum;
    // 箱规
    private String boxStandard;
    // 货号
    private String stockNo;
    // 销售数量
    private Integer sellNum;
    // 销售金额
    private Double sellPrice;
    // 日期
    @JSONField(format = "yyyy-MM-dd")
    private Date createTime;
    // 备注
    private String remark;
    // 门店负责人
    private String storeManager;

    public String getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(String storeManager) {
		this.storeManager = storeManager;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSysId() {
        return sysId;
    }

    public void setSysId(Integer sysId) {
        this.sysId = sysId;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName == null ? null : sysName.trim();
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode == null ? null : storeCode.trim();
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName == null ? null : storeName.trim();
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

    public String getSimpleCode() {
        return simpleCode;
    }

    public void setSimpleCode(String simpleCode) {
        this.simpleCode = simpleCode == null ? null : simpleCode.trim();
    }

    public String getSimpleBarCode() {
        return simpleBarCode;
    }

    public void setSimpleBarCode(String simpleBarCode) {
        this.simpleBarCode = simpleBarCode == null ? null : simpleBarCode.trim();
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode == null ? null : stockCode.trim();
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName == null ? null : simpleName.trim();
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand == null ? null : brand.trim();
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series == null ? null : series.trim();
    }

    public String getDayNight() {
        return dayNight;
    }

    public void setDayNight(String dayNight) {
        this.dayNight = dayNight == null ? null : dayNight.trim();
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material == null ? null : material.trim();
    }

    public Integer getPiecesNum() {
        return piecesNum;
    }

    public void setPiecesNum(Integer piecesNum) {
        this.piecesNum = piecesNum;
    }

    public String getBoxStandard() {
        return boxStandard;
    }

    public void setBoxStandard(String boxStandard) {
        this.boxStandard = boxStandard == null ? null : boxStandard.trim();
    }

    public String getStockNo() {
        return stockNo;
    }

    public void setStockNo(String stockNo) {
        this.stockNo = stockNo == null ? null : stockNo.trim();
    }

    public Integer getSellNum() {
        return sellNum;
    }

    public void setSellNum(Integer sellNum) {
        this.sellNum = sellNum;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}