package com.data.bean;

import java.io.Serializable;

/**
 * 角色供应商关联表
 * @author Alex
 *
 */
public class SystemRoleSupply implements Serializable {
    
	private static final long serialVersionUID = -6043748255735201788L;

	private Integer id;

    private String roleId;

    private String sysId;

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

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId == null ? null : sysId.trim();
    }
}