package com.data.bean;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 退单类
 * @author Alex
 *
 */
public class Reject implements Serializable {
	
	private static final long serialVersionUID = 1708394571891256261L;

	private Integer id;

	private String sysId;
	
    private String sysName;

    private String rejectDepartmentId;

    private String mainArea;

    private String provinceArea;

    private String rejectDepartmentName;

    private String receiptCode;

    private String productCode;

    private String productBarCode;

    private String productStoreCode;

    private String productName;

    private Long taxRate;

    private Long productAmount;

    private Long rejectPriceWithRate;

    private Long rejectPrice;

    private Date rejectDate;

    private Long discountPrice;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date discountStartDate;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date discountEndDate;

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

   

    public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

	public String getSysName() {
		return sysName;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public String getRejectDepartmentId() {
        return rejectDepartmentId;
    }

    public void setRejectDepartmentId(String rejectDepartmentId) {
        this.rejectDepartmentId = rejectDepartmentId == null ? null : rejectDepartmentId.trim();
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

    public String getRejectDepartmentName() {
        return rejectDepartmentName;
    }

    public void setRejectDepartmentName(String rejectDepartmentName) {
        this.rejectDepartmentName = rejectDepartmentName == null ? null : rejectDepartmentName.trim();
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

    public Long getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(Long productAmount) {
        this.productAmount = productAmount;
    }

    public Long getRejectPriceWithRate() {
        return rejectPriceWithRate;
    }

    public void setRejectPriceWithRate(Long rejectPriceWithRate) {
        this.rejectPriceWithRate = rejectPriceWithRate;
    }

    public Long getRejectPrice() {
        return rejectPrice;
    }

    public void setRejectPrice(Long rejectPrice) {
        this.rejectPrice = rejectPrice;
    }

    public Date getRejectDate() {
        return rejectDate;
    }

    public void setRejectDate(Date rejectDate) {
        this.rejectDate = rejectDate;
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