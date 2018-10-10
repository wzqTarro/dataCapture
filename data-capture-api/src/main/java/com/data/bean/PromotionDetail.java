package com.data.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 促销活动明细
 * @author Alex
 *
 */
public class PromotionDetail implements Serializable {

	private static final long serialVersionUID = 3644586983750605367L;

	private Integer id;

    private String sysId;

    private String sysName;

    private String storeName;

    @JSONField(format = "yyyy-MM-dd")
    private Date supplyPriceStartDate;

    @JSONField(format = "yyyy-MM-dd")
    private Date supplyPriceEndDate;

    @JSONField(format = "yyyy-MM-dd")
    private Date sellPriceStartDate;

    @JSONField(format = "yyyy-MM-dd")
    private Date sellPriceEndDate;

    private String supplyName;

    private String supplyType;

    private String controlType;

    private String compensationType;

    private String compensationCost;

    private String productCode;

    private BigDecimal originPrice;

    private BigDecimal normalSupplyPrice;

    private BigDecimal supplyPrice;

    private String supplyOrderType;

    private BigDecimal normalSellPrice;

    private BigDecimal supplySellPrice;

    private Double profit;

    private Double discount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId == null ? null : sysId.trim();
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName == null ? null : sysName.trim();
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName == null ? null : storeName.trim();
    }

    public Date getSupplyPriceStartDate() {
        return supplyPriceStartDate;
    }

    public void setSupplyPriceStartDate(Date supplyPriceStartDate) {
        this.supplyPriceStartDate = supplyPriceStartDate;
    }

    public Date getSupplyPriceEndDate() {
        return supplyPriceEndDate;
    }

    public void setSupplyPriceEndDate(Date supplyPriceEndDate) {
        this.supplyPriceEndDate = supplyPriceEndDate;
    }

    public Date getSellPriceStartDate() {
        return sellPriceStartDate;
    }

    public void setSellPriceStartDate(Date sellPriceStartDate) {
        this.sellPriceStartDate = sellPriceStartDate;
    }

    public Date getSellPriceEndDate() {
        return sellPriceEndDate;
    }

    public void setSellPriceEndDate(Date sellPriceEndDate) {
        this.sellPriceEndDate = sellPriceEndDate;
    }

    public String getSupplyName() {
        return supplyName;
    }

    public void setSupplyName(String supplyName) {
        this.supplyName = supplyName == null ? null : supplyName.trim();
    }

    public String getSupplyType() {
        return supplyType;
    }

    public void setSupplyType(String supplyType) {
        this.supplyType = supplyType == null ? null : supplyType.trim();
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType == null ? null : controlType.trim();
    }

    public String getCompensationType() {
        return compensationType;
    }

    public void setCompensationType(String compensationType) {
        this.compensationType = compensationType == null ? null : compensationType.trim();
    }

    public String getCompensationCost() {
        return compensationCost;
    }

    public void setCompensationCost(String compensationCost) {
        this.compensationCost = compensationCost == null ? null : compensationCost.trim();
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode == null ? null : productCode.trim();
    }

    public BigDecimal getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(BigDecimal originPrice) {
        this.originPrice = originPrice;
    }

    public BigDecimal getNormalSupplyPrice() {
        return normalSupplyPrice;
    }

    public void setNormalSupplyPrice(BigDecimal normalSupplyPrice) {
        this.normalSupplyPrice = normalSupplyPrice;
    }

    public BigDecimal getSupplyPrice() {
        return supplyPrice;
    }

    public void setSupplyPrice(BigDecimal supplyPrice) {
        this.supplyPrice = supplyPrice;
    }

    public String getSupplyOrderType() {
        return supplyOrderType;
    }

    public void setSupplyOrderType(String supplyOrderType) {
        this.supplyOrderType = supplyOrderType == null ? null : supplyOrderType.trim();
    }

    public BigDecimal getNormalSellPrice() {
        return normalSellPrice;
    }

    public void setNormalSellPrice(BigDecimal normalSellPrice) {
        this.normalSellPrice = normalSellPrice;
    }

    public BigDecimal getSupplySellPrice() {
        return supplySellPrice;
    }

    public void setSupplySellPrice(BigDecimal supplySellPrice) {
        this.supplySellPrice = supplySellPrice;
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }
}