package com.data.bean;

import java.io.Serializable;
import java.util.Date;

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

    private Date supplyPriceStartDate;

    private Date supplyPriceEndDate;

    private Date sellPriceStartDate;

    private Date sellPriceEndDate;

    private String supplyName;

    private String supplyType;

    private String controlType;

    private String compensationType;

    private String compensationCost;

    private String productCode;

    private Long originPrice;

    private Long normalSupplyPrice;

    private Long supplyPrice;

    private String supplyOrderType;

    private Long normalSellPrice;

    private Long supplySellPrice;

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

    public Long getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(Long originPrice) {
        this.originPrice = originPrice;
    }

    public Long getNormalSupplyPrice() {
        return normalSupplyPrice;
    }

    public void setNormalSupplyPrice(Long normalSupplyPrice) {
        this.normalSupplyPrice = normalSupplyPrice;
    }

    public Long getSupplyPrice() {
        return supplyPrice;
    }

    public void setSupplyPrice(Long supplyPrice) {
        this.supplyPrice = supplyPrice;
    }

    public String getSupplyOrderType() {
        return supplyOrderType;
    }

    public void setSupplyOrderType(String supplyOrderType) {
        this.supplyOrderType = supplyOrderType == null ? null : supplyOrderType.trim();
    }

    public Long getNormalSellPrice() {
        return normalSellPrice;
    }

    public void setNormalSellPrice(Long normalSellPrice) {
        this.normalSellPrice = normalSellPrice;
    }

    public Long getSupplySellPrice() {
        return supplySellPrice;
    }

    public void setSupplySellPrice(Long supplySellPrice) {
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