package com.data.bean;

import java.io.Serializable;

public class Product implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -4165714932752567317L;

	private Integer id;
	// 商品编号
    private String productId;
    // 网站名
    private String sysName;
    // 单品编号
    private String simpleCode;
    // 条码
    private String simpleBarCode;
    // 存货编码
    private String stockCode;
    // 单品名称
    private String simpleName;
    // 标准名称
    private String standardName;
    // 品牌
    private String brand;
    // 分类
    private String classify;
    // 系列
    private String series;
    // 功能
    private String func;
    // 材质
    private String material;
    // 包装数量
    private Integer packNum;
    // 箱装规格
    private String boxStandard;
    // 货号
    private String stockNo;
    // 零售价格
    private Double sellPrice;
    // 不含税供价
    private Double excludeTaxPrice;
    // 含税供价
    private Double includeTaxPrice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId == null ? null : productId.trim();
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName == null ? null : sysName.trim();
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

    public String getStandardName() {
        return standardName;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName == null ? null : standardName.trim();
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand == null ? null : brand.trim();
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify == null ? null : classify.trim();
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series == null ? null : series.trim();
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func == null ? null : func.trim();
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material == null ? null : material.trim();
    }

    public Integer getPackNum() {
        return packNum;
    }

    public void setPackNum(Integer packNum) {
        this.packNum = packNum;
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

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Double getExcludeTaxPrice() {
        return excludeTaxPrice;
    }

    public void setExcludeTaxPrice(Double excludeTaxPrice) {
        this.excludeTaxPrice = excludeTaxPrice;
    }

    public Double getIncludeTaxPrice() {
        return includeTaxPrice;
    }

    public void setIncludeTaxPrice(Double includeTaxPrice) {
        this.includeTaxPrice = includeTaxPrice;
    }
}