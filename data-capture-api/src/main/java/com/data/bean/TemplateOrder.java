package com.data.bean;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 订单模板实体类
 * @author Alex
 *
 */
public class TemplateOrder implements Serializable {

	private static final long serialVersionUID = 5885036937790616913L;

	private Integer id;

    private String orderId;

    private String no;

    private String contractCode;

    private String goodName;

    private String goodBarCode;

    private String goodCode;

    private String orderType;

    private String goodType;

    private String orderShop;

    private String address;

    private Double sellPrice;

    private Double count;

    private Double simpleCount;

    private Double littleCount;

    private Double realCount;

    private String unit;

    private String stageCount;

    private String orderState;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String operatorName;

    private String companyCode;

    private String brand;

    private Double partContent;

    private Double persentNum;

    private Double containTaxPrice;

    private Double containTaxMoney;

    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no == null ? null : no.trim();
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode == null ? null : contractCode.trim();
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName == null ? null : goodName.trim();
    }

    public String getGoodBarCode() {
        return goodBarCode;
    }

    public void setGoodBarCode(String goodBarCode) {
        this.goodBarCode = goodBarCode == null ? null : goodBarCode.trim();
    }

    public String getGoodCode() {
        return goodCode;
    }

    public void setGoodCode(String goodCode) {
        this.goodCode = goodCode == null ? null : goodCode.trim();
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType == null ? null : orderType.trim();
    }

    public String getGoodType() {
        return goodType;
    }

    public void setGoodType(String goodType) {
        this.goodType = goodType == null ? null : goodType.trim();
    }

    public String getOrderShop() {
        return orderShop;
    }

    public void setOrderShop(String orderShop) {
        this.orderShop = orderShop == null ? null : orderShop.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public Double getSimpleCount() {
        return simpleCount;
    }

    public void setSimpleCount(Double simpleCount) {
        this.simpleCount = simpleCount;
    }

    public Double getLittleCount() {
        return littleCount;
    }

    public void setLittleCount(Double littleCount) {
        this.littleCount = littleCount;
    }

    public Double getRealCount() {
        return realCount;
    }

    public void setRealCount(Double realCount) {
        this.realCount = realCount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit == null ? null : unit.trim();
    }

    public String getStageCount() {
        return stageCount;
    }

    public void setStageCount(String stageCount) {
        this.stageCount = stageCount == null ? null : stageCount.trim();
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState == null ? null : orderState.trim();
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName == null ? null : operatorName.trim();
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode == null ? null : companyCode.trim();
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand == null ? null : brand.trim();
    }

    public Double getPartContent() {
        return partContent;
    }

    public void setPartContent(Double partContent) {
        this.partContent = partContent;
    }

    public Double getPersentNum() {
        return persentNum;
    }

    public void setPersentNum(Double persentNum) {
        this.persentNum = persentNum;
    }

    public Double getContainTaxPrice() {
        return containTaxPrice;
    }

    public void setContainTaxPrice(Double containTaxPrice) {
        this.containTaxPrice = containTaxPrice;
    }

    public Double getContainTaxMoney() {
        return containTaxMoney;
    }

    public void setContainTaxMoney(Double containTaxMoney) {
        this.containTaxMoney = containTaxMoney;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}