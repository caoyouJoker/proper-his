
package com.javahis.web.bean;

import java.io.Serializable;
import java.util.List;

public class Page implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 跳转到第几页
	 */
	private int toPage;

	/**
	 * 总共记录数
	 */
	private int totalRecords;

	/**
	 * 每页显示记录数
	 */
	private int pageSize;

	/**
	 * 总共页数
	 */
	private int totalPage;

	/**
	 * 起始记录数
	 */
	private int beginIndex;

	/**
	 * 是否有下页
	 */
	@SuppressWarnings("unused")
	private boolean nextPage;

	/**
	 * 是否有下上页
	 */
	@SuppressWarnings("unused")
	private boolean prevPage;
	
	/**
	 * 要返回的某一页的记录列表 
	 */
	@SuppressWarnings("unchecked")
	private List list;   
 

	/**
	 * 不含参数的构造函数构造函数
	 */
	public Page() {
	}

	
	public Page(int toPage,int pageSize){
		if (toPage == 0) {
			this.toPage = 1;
		} else {
			this.toPage = toPage;
		}
		this.pageSize = pageSize;
	}
	
	/**
	 * 说明： 计算起始记录数 
	 * 
	 * @return
	 */
	public int getBeginIndex() {
		beginIndex = (toPage - 1) * pageSize;
		return beginIndex;
	}

	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}

	/**
	 * 说明： 判断是否有下一页  
	 * 
	 * @return
	 */
	public boolean isNextPage() {
		if (this.getToPage() > 0) {
			if (this.getToPage() >= getTotalPage()) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * 说明： 判断是否有上页  
	 * 
	 * @return
	 */
	public boolean isPrevPage() {
		if (this.getToPage() > 1) {
			return true;
		}
		return false;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getToPage() {
		return toPage;
	}

	public void setToPage(int toPage) {
		this.toPage = toPage;
	}

	/**
	 * 说明： 计算页数 
	 * 
	 * @return
	 */
	public int getTotalPage() {
		totalPage = totalRecords / pageSize;
		if (totalRecords % pageSize != 0) {
			totalPage++;
		}
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	@SuppressWarnings("unchecked")
	public List getList() {
		return list;
	}

	@SuppressWarnings("unchecked")
	public void setList(List list) {
		this.list = list;
	}
	 
}