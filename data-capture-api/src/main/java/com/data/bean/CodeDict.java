package com.data.bean;

import java.io.Serializable;

/**
 * 字典bean
 * @author Alex
 *
 */
public class CodeDict implements Serializable {
    
	private static final long serialVersionUID = -2567900408669771739L;

	private Integer id;

    private String serviceCode;

    private String codeNo;

    private String codeValue;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode == null ? null : serviceCode.trim();
    }

    public String getCodeNo() {
        return codeNo;
    }

    public void setCodeNo(String codeNo) {
        this.codeNo = codeNo == null ? null : codeNo.trim();
    }

    public String getCodeValue() {
        return codeValue;
    }

    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue == null ? null : codeValue.trim();
    }
}