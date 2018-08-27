package com.data.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单类
 * @author Alex
 *
 */
public class Order implements Serializable {

	private static final long serialVersionUID = -7993999981089975803L;

	private Integer id;

    private String system;

    private String mainArea;

    private String provinceArea;

    private String departmentName;

    private String receiptCode;

    private String productCode;

    private String productBarCode;

    private String productStoreCode;

    private String productName;

    private Long taxRate;

    private Integer standard;

    private Long productAmount;

    private Long buyingPriceWithRate;

    private Long buyingPrice;

    private Date deliverStartDate;

    private Date deliverEndDate;

    private String deliverAddress;

    private Long discountPrice;

    private Date discountStartDate;

    private Date discountEndDate;

    private String orderEffectiveJudge;

    private String balanceWay;

    private Long diffPriceDiscount;

    private Long diffPriceDiscountTotal;

    private String discountAlarmFlag;

    private Long contractPrice;

    private Long diffPriceContract;

    private Long diffPriceContractTotal;

    private String contractAlarmFlag;

    private Long diffPrice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system == null ? null : system.trim();
    }

    public String getMainArea() {
        return mainArea;
    }

    public void setMainArea(String mainArea) {
        this.mainArea = mainArea == null ? null : mainArea.trim();
    }

    public String getProvinceArea() {
        return provinceArea;
    }

    public void setProvinceArea(String provinceArea) {
        this.provinceArea = provinceArea == null ? null : provinceArea.trim();
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName == null ? null : departmentName.trim();
    }

    public String getReceiptCode() {
        return receiptCode;
    }

    public void setReceiptCode(String receiptCode) {
        this.receiptCode = receiptCode == null ? null : receiptCode.trim();
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode == null ? null : productCode.trim();
    }

    public String getProductBarCode() {
        return productBarCode;
    }

    public void setProductBarCode(String productBarCode) {
        this.productBarCode = productBarCode == null ? null : productBarCode.trim();
    }

    public String getProductStoreCode() {
        return productStoreCode;
    }

    public void setProductStoreCode(String productStoreCode) {
        this.productStoreCode = productStoreCode == null ? null : productStoreCode.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public Long getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Long taxRate) {
        this.taxRate = taxRate;
    }

    public Integer getStandard() {
        return standard;
    }

    public void setStandard(Integer standard) {
        this.standard = standard;
    }

    public Long getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(Long productAmount) {
        this.productAmount = productAmount;
    }

    public Long getBuyingPriceWithRate() {
        return buyingPriceWithRate;
    }

    public void setBuyingPriceWithRate(Long buyingPriceWithRate) {
        this.buyingPriceWithRate = buyingPriceWithRate;
    }

    public Long getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(Long buyingPrice) {
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

    public Long getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Long discountPrice) {
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

    public Long getDiffPriceDiscount() {
        return diffPriceDiscount;
    }

    public void setDiffPriceDiscount(Long diffPriceDiscount) {
        this.diffPriceDiscount = diffPriceDiscount;
    }

    public Long getDiffPriceDiscountTotal() {
        return diffPriceDiscountTotal;
    }

    public void setDiffPriceDiscountTotal(Long diffPriceDiscountTotal) {
        this.diffPriceDiscountTotal = diffPriceDiscountTotal;
    }

    public String getDiscountAlarmFlag() {
        return discountAlarmFlag;
    }

    public void setDiscountAlarmFlag(String discountAlarmFlag) {
        this.discountAlarmFlag = discountAlarmFlag == null ? null : discountAlarmFlag.trim();
    }

    public Long getContractPrice() {
        return contractPrice;
    }

    public void setContractPrice(Long contractPrice) {
        this.contractPrice = contractPrice;
    }

    public Long getDiffPriceContract() {
        return diffPriceContract;
    }

    public void setDiffPriceContract(Long diffPriceContract) {
        this.diffPriceContract = diffPriceContract;
    }

    public Long getDiffPriceContractTotal() {
        return diffPriceContractTotal;
    }

    public void setDiffPriceContractTotal(Long diffPriceContractTotal) {
        this.diffPriceContractTotal = diffPriceContractTotal;
    }

    public String getContractAlarmFlag() {
        return contractAlarmFlag;
    }

    public void setContractAlarmFlag(String contractAlarmFlag) {
        this.contractAlarmFlag = contractAlarmFlag == null ? null : contractAlarmFlag.trim();
    }

    public Long getDiffPrice() {
        return diffPrice;
    }

    public void setDiffPrice(Long diffPrice) {
        this.diffPrice = diffPrice;
    }
}