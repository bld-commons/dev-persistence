/**************************************************************************
 * 
 * Copyright 2018 (C) DXC Technology
 * 
 * Author      : DXC Technology
 * Project Name: pmg-common
 * Package     : com.bld.pmg.message.common.request
 * File Name   : BaseFilterRequest.java
 *
 ***************************************************************************/
package bld.commons.persistence.reflection.model;

import bld.commons.persistence.reflection.annotations.ExcludeFromMap;

/**
 * The Class BaseFilterRequest.
 *
 */
public abstract class BaseFilterRequest {

	/** The sort key. */
	@ExcludeFromMap
	protected String sortKey;

	/** The sort order. */
	@ExcludeFromMap
	protected String sortOrder;

	/** The page size. */
	@ExcludeFromMap
	protected Integer pageSize;

	/** The page number. */
	@ExcludeFromMap
	protected Integer pageNumber;

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	
	
	
}
