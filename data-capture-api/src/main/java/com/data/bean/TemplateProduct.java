package com.data.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品模板实体类
 * @author Alex
 *
 */
public class TemplateProduct implements Serializable {
	
	private static final long serialVersionUID = -4931532837429985816L;

	private Integer id;

    private String productId;

    private String sysId;
    
    private String sysName;

    private String simpleCode;

    private String simpleBarCode;

    private String stockCode;

    private String simpleName;

    private String standardName;

    private String brand;

    private String classify;

    private String series;

    private String func;

    private String material;

    // 包装数量（片数）
    private Integer piecesNum;

    private String boxStandard;

    private String stockNo;

    private BigDecimal sellPrice;

    private BigDecimal excludeTaxPrice;

    private BigDecimal includeTaxPrice;

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

    public BigDecimal getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
    }

    public BigDecimal getExcludeTaxPrice() {
        return excludeTaxPrice;
    }

    public void setExcludeTaxPrice(BigDecimal excludeTaxPrice) {
        this.excludeTaxPrice = excludeTaxPrice;
    }

    public BigDecimal getIncludeTaxPrice() {
        return includeTaxPrice;
    }

    public void setIncludeTaxPrice(BigDecimal includeTaxPrice) {
        this.includeTaxPrice = includeTaxPrice;
    }
}