package com.data.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单类
 * @author Alex
 *
 */
public class SystemMenu implements Serializable {

	private static final long serialVersionUID = 2014482865431022234L;

	private Integer id;

    private String menuId;

    private String menuName;

    private String menuIcon;

    private String menuUrl;

    private String isParent;

    private String parentId;

    private String isEnable;

    private String isDelete;
    
    private List<SystemMenu> childMenuList;
    
    private List<SystemFunction> functionList;
    
    public List<SystemFunction> getFunctionList() {
		return functionList;
	}

	public void setFunctionList(List<SystemFunction> functionList) {
		this.functionList = functionList;
	}

	public List<SystemMenu> getChildMenuList() {
		return childMenuList;
	}

	public void setChildMenuList(List<SystemMenu> childMenuList) {
		this.childMenuList = childMenuList;
	}

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

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName == null ? null : menuName.trim();
    }

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon == null ? null : menuIcon.trim();
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl == null ? null : menuUrl.trim();
    }

    public String getIsParent() {
        return isParent;
    }

    public void setIsParent(String isParent) {
        this.isParent = isParent == null ? null : isParent.trim();
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId == null ? null : parentId.trim();
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