package com.data.bean;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 销售数据
 * @author Tarro
 * 创建时间：2018年8月2日
 */
public class Sale1 implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8358908390938980600L;
	
	private String id;
	//抓取系统编号
	private String sysId;
	//系统名
	private String sysName;
	//门店编号
	private String storeCode;
	//门店名称
	private String storeName;
	//大区
	private String region;
	//省区
	private String provinceArea;
	//归属
	private String ascription;
	//业绩归属
	private String ascriptionSole;
	//单品编码
	private String simpleCode;
	//单品条码
	private String simpleBarCode;
	//存货编码
	private String stockBarCode;
	//单品名称
	private String simpleGoodName;
	//品牌
	private String brand;
	//分类
	private String classify;
	//系列
	private String series;
	//日夜
	private String dayNight;
	//材质
	private String material;
	//片数
	private Integer piecesNum;
	//箱规 
	private Integer boxStandard;
	//货号
	private String stockCode;
	//销售数量
	private Integer sellNum;
	//销售金额
	private Double sellPrice;
	//日期
	private Long date;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
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
	public String getStoreCode() {
		return storeCode;
	}
	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getProvinceArea() {
		return provinceArea;
	}
	public void setProvinceArea(String provinceArea) {
		this.provinceArea = provinceArea;
	}
	public String getAscription() {
		return ascription;
	}
	public void setAscription(String ascription) {
		this.ascription = ascription;
	}
	public String getAscriptionSole() {
		return ascriptionSole;
	}
	public void setAscriptionSole(String ascriptionSole) {
		this.ascriptionSole = ascriptionSole;
	}
	public String getSimpleCode() {
		return simpleCode;
	}
	public void setSimpleCode(String simpleCode) {
		this.simpleCode = simpleCode;
	}
	public String getSimpleBarCode() {
		return simpleBarCode;
	}
	public void setSimpleBarCode(String simpleBarCode) {
		this.simpleBarCode = simpleBarCode;
	}
	public String getStockBarCode() {
		return stockBarCode;
	}
	public void setStockBarCode(String stockBarCode) {
		this.stockBarCode = stockBarCode;
	}
	public String getSimpleGoodName() {
		return simpleGoodName;
	}
	public void setSimpleGoodName(String simpleGoodName) {
		this.simpleGoodName = simpleGoodName;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getClassify() {
		return classify;
	}
	public void setClassify(String classify) {
		this.classify = classify;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public String getDayNight() {
		return dayNight;
	}
	public void setDayNight(String dayNight) {
		this.dayNight = dayNight;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public Integer getPiecesNum() {
		return piecesNum;
	}
	public void setPiecesNum(Integer piecesNum) {
		this.piecesNum = piecesNum;
	}
	public Integer getBoxStandard() {
		return boxStandard;
	}
	public void setBoxStandard(Integer boxStandard) {
		this.boxStandard = boxStandard;
	}
	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	public Integer getSellNum() {
		return sellNum;
	}
	public void setSellNum(Integer sellNum) {
		this.sellNum = sellNum;
	}
	public Double getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(Double sellPrice) {
		this.sellPrice = sellPrice;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	
	
	
}
