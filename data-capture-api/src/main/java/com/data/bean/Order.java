package com.data.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class Order implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3604219584476636865L;

	private Integer id;

    private String sysId;

    private String sysName;

    private String region;

    private String provinceArea;

    private String storeCode;

    private String storeName;

    private String receiptCode;

    private String simpleCode;

    private String simpleBarCode;

    private String stockCode;

    private String simpleName;

    // 税率
    private BigDecimal taxRate;

    private String boxStandard;

    private Long simpleAmount;

    private BigDecimal buyingPriceWithRate;

    private BigDecimal buyingPrice;

    // 送货日期
    @JSONField(format = "yyyy-MM-dd")
    private Date deliverStartDate;

    // 送货截止日期
    @JSONField(format = "yyyy-MM-dd")
    private Date deliverEndDate;

    private String deliverAddress;

    private BigDecimal discountPrice;

    // 促销供价开始日期
    @JSONField(format = "yyyy-MM-dd")
    private Date discountStartDate;

    // 促销供价结束日期
    @JSONField(format = "yyyy-MM-dd")
    private Date discountEndDate;

    // 订单有效性判断
    private String orderEffectiveJudge;

    // 补差方式
    private String balanceWay;

    // 促销供价差异
    private BigDecimal diffPriceDiscount;

    // 促销供价差异汇总
    private BigDecimal diffPriceDiscountTotal;

    // 促销供价报警标志
    private String discountAlarmFlag;

    // 合同供价
    private BigDecimal contractPrice;

    // 合同供价差异
    private BigDecimal diffPriceContract;

    // 合同供价差异汇总
    private BigDecimal diffPriceContractTotal;

    // 合同供价报警标志
    private String contractAlarmFlag;

    // 汇总供价差异
    private BigDecimal diffPrice;
    
    private String remark;
    
    // 是否补全
    private Integer status;

    public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

    public String getReceiptCode() {
        return receiptCode;
    }

    public void setReceiptCode(String receiptCode) {
        this.receiptCode = receiptCode == null ? null : receiptCode.trim();
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

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getBoxStandard() {
        return boxStandard;
    }

    public void setBoxStandard(String boxStandard) {
        this.boxStandard = boxStandard == null ? null : boxStandard.trim();
    }

    public Long getSimpleAmount() {
        return simpleAmount;
    }

    public void setSimpleAmount(Long simpleAmount) {
        this.simpleAmount = simpleAmount;
    }

    public BigDecimal getBuyingPriceWithRate() {
        return buyingPriceWithRate;
    }

    public void setBuyingPriceWithRate(BigDecimal buyingPriceWithRate) {
        this.buyingPriceWithRate = buyingPriceWithRate;
    }

    public BigDecimal getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(BigDecimal buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public Date getDeliverStartDate() {
        return deliverStartDate;
    }

    public void setDeliverStartDate(Date deliverStartDate) {
        this.deliverStartDate = deliverStartDate;
    }

    public Date getDeliverEndDate() {
        return deliverEndDate;
    }

    public void setDeliverEndDate(Date deliverEndDate) {
        this.deliverEndDate = deliverEndDate;
    }

    public String getDeliverAddress() {
        return deliverAddress;
    }

    public void setDeliverAddress(String deliverAddress) {
        this.deliverAddress = deliverAddress == null ? null : deliverAddress.trim();
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Date getDiscountStartDate() {
        return discountStartDate;
    }

    public void setDiscountStartDate(Date discountStartDate) {
        this.discountStartDate = discountStartDate;
    }

    public Date getDiscountEndDate() {
        return discountEndDate;
    }

    public void setDiscountEndDate(Date discountEndDate) {
        this.discountEndDate = discountEndDate;
    }

    public String getOrderEffectiveJudge() {
        return orderEffectiveJudge;
    }

    public void setOrderEffectiveJudge(String orderEffectiveJudge) {
        this.orderEffectiveJudge = orderEffectiveJudge == null ? null : orderEffectiveJudge.trim();
    }

    public String getBalanceWay() {
        return balanceWay;
    }

    public void setBalanceWay(String balanceWay) {
        this.balanceWay = balanceWay == null ? null : balanceWay.trim();
    }

    public BigDecimal getDiffPriceDiscount() {
        return diffPriceDiscount;
    }

    public void setDiffPriceDiscount(BigDecimal diffPriceDiscount) {
        this.diffPriceDiscount = diffPriceDiscount;
    }

    public BigDecimal getDiffPriceDiscountTotal() {
        return diffPriceDiscountTotal;
    }

    public void setDiffPriceDiscountTotal(BigDecimal diffPriceDiscountTotal) {
        this.diffPriceDiscountTotal = diffPriceDiscountTotal;
    }

    public String getDiscountAlarmFlag() {
        return discountAlarmFlag;
    }

    public void setDiscountAlarmFlag(String discountAlarmFlag) {
        this.discountAlarmFlag = discountAlarmFlag == null ? null : discountAlarmFlag.trim();
    }

    public BigDecimal getContractPrice() {
        return contractPrice;
    }

    public void setContractPrice(BigDecimal contractPrice) {
        this.contractPrice = contractPrice;
    }

    public BigDecimal getDiffPriceContract() {
        return diffPriceContract;
    }

    public void setDiffPriceContract(BigDecimal diffPriceContract) {
        this.diffPriceContract = diffPriceContract;
    }

    public BigDecimal getDiffPriceContractTotal() {
        return diffPriceContractTotal;
    }

    public void setDiffPriceContractTotal(BigDecimal diffPriceContractTotal) {
        this.diffPriceContractTotal = diffPriceContractTotal;
    }

    public String getContractAlarmFlag() {
        return contractAlarmFlag;
    }

    public void setContractAlarmFlag(String contractAlarmFlag) {
        this.contractAlarmFlag = contractAlarmFlag == null ? null : contractAlarmFlag.trim();
    }

    public BigDecimal getDiffPrice() {
        return diffPrice;
    }

    public void setDiffPrice(BigDecimal diffPrice) {
        this.diffPrice = diffPrice;
    }
}