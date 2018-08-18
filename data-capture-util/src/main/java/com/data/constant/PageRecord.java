package com.data.constant;

import java.io.Serializable;
import java.util.List;

/**
 * 分页bean
 * @author Alex
 * @update Tarro 2018年8月18日
 *
 */
public class PageRecord<T> implements Serializable {
	
	private static final long serialVersionUID = -4381908614220670259L;
	
	private int pageNum;
	private int pageSize;
	private long pageTotal;
	private List<T> list;
	
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public long getPageTotal() {
		return pageTotal;
	}
	public void setPageTotal(long pageTotal) {
		this.pageTotal = pageTotal;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	
}
