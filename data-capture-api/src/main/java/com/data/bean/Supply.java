package com.data.bean;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class Supply implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8086505396834955857L;

	private Integer id;

    private String region;

    private String sysName;

    private String url;

    private String loginUserName;

    private String loginPassword;

    private String companyCode;
    // 是否开通
    private Boolean isVal;
    
    @JSONField(serialize = false)
    private String controllerName;

    public Boolean getIsVal() {
		return isVal;
	}

	public void setIsVal(Boolean isVal) {
		this.isVal = isVal;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region == null ? null : region.trim();
    }

    public String getSysName() {
		return sysName;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getLoginUserName() {
        return loginUserName;
    }

    public void setLoginUserName(String loginUserName) {
        this.loginUserName = loginUserName == null ? null : loginUserName.trim();
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword == null ? null : loginPassword.trim();
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode == null ? null : companyCode.trim();
    }

	public String getControllerName() {
		return controllerName;
	}

	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}
}