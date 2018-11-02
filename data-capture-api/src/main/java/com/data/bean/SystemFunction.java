package com.data.bean;

import java.io.Serializable;

/**
 * 按钮动作类
 * @author Alex
 *
 */
public class SystemFunction implements Serializable {

	private static final long serialVersionUID = -1959828511921263341L;

	private Integer id;

    private String functionId;

    private String functionName;

    private String functionIcon;

    private String functionUrl;

    private String functionAuth;

    private String isEnable;

    private String isDelete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId == null ? null : functionId.trim();
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName == null ? null : functionName.trim();
    }

    public String getFunctionIcon() {
        return functionIcon;
    }

    public void setFunctionIcon(String functionIcon) {
        this.functionIcon = functionIcon == null ? null : functionIcon.trim();
    }

    public String getFunctionUrl() {
        return functionUrl;
    }

    public void setFunctionUrl(String functionUrl) {
        this.functionUrl = functionUrl == null ? null : functionUrl.trim();
    }

    public String getFunctionAuth() {
        return functionAuth;
    }

    public void setFunctionAuth(String functionAuth) {
        this.functionAuth = functionAuth == null ? null : functionAuth.trim();
    }

    public String getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(String isEnable) {
        this.isEnable = isEnable == null ? null : isEnable.trim();
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete == null ? null : isDelete.trim();
    }
}