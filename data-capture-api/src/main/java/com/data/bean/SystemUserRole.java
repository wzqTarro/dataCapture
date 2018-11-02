package com.data.bean;

import java.io.Serializable;

/**
 * 用户角色关联类
 * @author Alex
 *
 */
public class SystemUserRole implements Serializable {
    
	private static final long serialVersionUID = -4421863623768548204L;

	private Integer id;

    private String workNo;

    private String roleId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo == null ? null : workNo.trim();
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId == null ? null : roleId.trim();
    }
}