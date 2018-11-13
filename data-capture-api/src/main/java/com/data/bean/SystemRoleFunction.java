package com.data.bean;

import java.io.Serializable;

/**
 * 角色动作类
 * @author Alex
 *
 */
public class SystemRoleFunction implements Serializable {

	private static final long serialVersionUID = -8657297809360372530L;

	private Integer id;

    private String roleId;

    private String functionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId == null ? null : roleId.trim();
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId == null ? null : functionId.trim();
    }
}