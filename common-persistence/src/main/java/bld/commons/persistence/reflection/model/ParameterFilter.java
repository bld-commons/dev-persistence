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

import bld.commons.persistence.reflection.annotations.IgnoreMapping;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseFilterRequest.
 *
 */
public abstract class ParameterFilter {

	/** The sort key. */
	@IgnoreMapping
	protected String sortKey;

	/** The sort order. */
	@IgnoreMapping
	protected String sortOrder;

	/** The page size. */
	@IgnoreMapping
	protected Integer pageSize;

	/** The page number. */
	@IgnoreMapping
	protected Integer pageNumber;

	/**
	 * Gets the sort key.
	 *
	 * @return the sort key
	 */
	public String getSortKey() {
		return sortKey;
	}

	/**
	 * Sets the sort key.
	 *
	 * @param sortKey the new sort key
	 */
	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	/**
	 * Gets the sort order.
	 *
	 * @return the sort order
	 */
	public String getSortOrder() {
		return sortOrder;
	}

	/**
	 * Sets the sort order.
	 *
	 * @param sortOrder the new sort order
	 */
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	/**
	 * Gets the page size.
	 *
	 * @return the page size
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/**
	 * Sets the page size.
	 *
	 * @param pageSize the new page size
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Gets the page number.
	 *
	 * @return the page number
	 */
	public Integer getPageNumber() {
		return pageNumber;
	}

	/**
	 * Sets the page number.
	 *
	 * @param pageNumber the new page number
	 */
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	
	
	
}
