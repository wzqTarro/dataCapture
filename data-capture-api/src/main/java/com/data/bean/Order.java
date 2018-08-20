package com.data.bean;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

public class Order implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -6727601445060057901L;

	private Integer id;

	private String orderId;//订单编号
    private String no;//序号
    private String contractCode;//合同号
    private String goodName;//商品名称
    private String goodBarCode;//商品条码
    private String goodCode;//商品编码
    private String orderType;//订单类型
    private String goodType;//商品类型
    private String orderShop;//订单类型
    private String address;//收货地址

    private Double sellPrice;//销售价格

    private Double count;//件数
    private Double simpleCount;//零散数
    private Double littleCount;//细数
    private Double realCount;//实收数
    private String unit;//单位
    private String stageCount;//场次
    private String orderState;//订单状态
    @JSONField(format = "yyyy-MM-dd")
    private Date endTime;//有效截止时间
    @JSONField(format = "yyyy-MM-dd")
    private Date createTime;//创建时间
    private String operatorName;//审核人
    private String companyCode;//企业编号
    
    private String brand;//品牌

    private Double partContent;//件含量

    private Double persentNum;//赠品数量

    private Double containTaxPrice;//含税进价

    private Double containTaxMoney;//含税进价金额

    private String remark;//备注

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