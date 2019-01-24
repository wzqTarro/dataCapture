package com.data.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class Reject implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8105484584545060883L;

	private Integer id;

    private String sysId;

    private String sysName;

    // 退货机构编号
    private String rejectDepartmentId;

    private String region;

    private String provinceArea;

    // 退货机构名称
    private String rejectDepartmentName;

    private String receiptCode;

    private String simpleCode;

    private String simpleBarCode;

    private String stockCode;

    private String simpleName;

    private BigDecimal taxRate;

    private Long simpleAmount;

    private BigDecimal rejectPriceWithRate;

    private BigDecimal rejectPrice;

    // 退货日期
    @JSONField(format = "yyyy-MM-dd")
    private Date rejectDate;

    private BigDecimal discountPrice;

    // 促销供价生效日期
    @JSONField(format = "yyyy-MM-dd")
    private Date discountStartDate;

    // 促销供价结束日期
    @JSONField(format = "yyyy-MM-dd")
    private Date discountEndDate;

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

    public String getRejectDepartmentId() {
        return rejectDepartmentId;
    }

    public void setRejectDepartmentId(String rejectDepartmentId) {
        this.rejectDepartmentId = rejectDepartmentId == null ? null : rejectDepartmentId.trim();
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

    public Long getSimpleAmount() {
        return simpleAmount;
    }

    public void setSimpleAmount(Long simpleAmount) {
        this.simpleAmount = simpleAmount;
    }

    public BigDecimal getRejectPriceWithRate() {
        return rejectPriceWithRate;
    }

    public void setRejectPriceWithRate(BigDecimal rejectPriceWithRate) {
        this.rejectPriceWithRate = rejectPriceWithRate;
    }

    public BigDecimal getRejectPrice() {
        return rejectPrice;
    }

    public void setRejectPrice(BigDecimal rejectPrice) {
        this.rejectPrice = rejectPrice;
    }

    public Date getRejectDate() {
        return rejectDate;
    }

    public void setRejectDate(Date rejectDate) {
        this.rejectDate = rejectDate;
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