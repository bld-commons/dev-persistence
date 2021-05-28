/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.model.ParameterFilter.java
 */
package bld.commons.reflection.model;

import java.util.List;

import javax.validation.Valid;

import bld.commons.reflection.annotations.IgnoreMapping;

/**
 * The Class BaseFilterRequest.
 *
 */
public abstract class FilterParameter {

	/** The sort key. */
	@IgnoreMapping
	@Valid
	private List<OrderBy> orderBy;

	/** The page size. */
	@IgnoreMapping
	private Integer pageSize;

	/** The page number. */
	@IgnoreMapping
	private Integer pageNumber;

	

	public List<OrderBy> getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(List<OrderBy> orderBy) {
		this.orderBy = orderBy;
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
