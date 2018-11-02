package com.data.bean;

import java.io.Serializable;

/**
 * 菜单动作关联类
 * @author Alex
 *
 */
public class SystemMenuFunction implements Serializable {

	private static final long serialVersionUID = 5967716663166504490L;

	private Integer id;

    private String menuId;

    private String functionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId == null ? null : menuId.trim();
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId == null ? null : functionId.trim();
    }
}