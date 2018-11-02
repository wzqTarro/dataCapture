package com.data.bean;

import java.io.Serializable;

/**
 * 角色菜单关联类
 * @author Alex
 *
 */
public class SystemRoleMenu implements Serializable {
    
	private static final long serialVersionUID = -8282002620837383636L;

	private Integer id;

    private String roleId;

    private String menuId;

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

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId == null ? null : menuId.trim();
    }
}