/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.model.ParameterFilter.java
 */
package com.bld.commons.reflection.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bld.commons.reflection.annotations.IgnoreMapping;
import com.bld.commons.reflection.type.OrderType;

import jakarta.validation.Valid;

/**
 * Base class for all typed filter objects used with {@link QueryParameter}.
 *
 * <p>Extend this class to create a filter DTO whose non-null fields are
 * automatically translated into JPQL WHERE conditions at runtime.
 * Each field may be annotated with reflection annotations to customise
 * how it is mapped:
 * <ul>
 *   <li>{@link com.bld.commons.reflection.annotations.DateFilter} – shifts the date value
 *       by a configurable offset before binding it as a parameter.</li>
 *   <li>{@link com.bld.commons.reflection.annotations.LikeString} – wraps the value in
 *       SQL {@code LIKE} wildcards.</li>
 *   <li>{@link com.bld.commons.reflection.annotations.ListFilter} – maps a collection to
 *       an SQL {@code IN (…)} clause.</li>
 *   <li>{@link com.bld.commons.reflection.annotations.IgnoreMapping} – prevents a field
 *       from being added to the parameter map (e.g., pagination fields).</li>
 *   <li>{@link com.bld.commons.reflection.annotations.FieldMapping} – overrides the
 *       default parameter name with a custom one.</li>
 * </ul>
 * </p>
 *
 * <p>Pagination and sorting are built in:
 * use {@link #setPageSize(Integer)} / {@link #setPageNumber(Integer)} for paging
 * and {@link #addOrderBy(String, com.bld.commons.reflection.type.OrderType)} for sorting.
 * These fields are annotated with {@code @IgnoreMapping} so they are not treated
 * as query conditions.</p>
 *
 * <h3>Example</h3>
 * <pre>{@code
 * public class ProductFilter extends BaseParameter {
 *
 *     private String name;
 *
 *     @LikeString(likeType = LikeType.LEFT_RIGHT)
 *     private String description;
 *
 *     @DateFilter(addDay = 1)
 *     private Date expiresAfter;
 *
 *     @ListFilter
 *     private List<Long> categoryIds;
 *
 *     // getters/setters ...
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 * @see QueryParameter
 * @see com.bld.commons.reflection.annotations.DateFilter
 * @see com.bld.commons.reflection.annotations.LikeString
 * @see com.bld.commons.reflection.annotations.ListFilter
 */
@SuppressWarnings("serial")
public abstract class BaseParameter implements Serializable{
	
	

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
	
	/**
	 * Instantiates a new filter parameter.
	 */
	public BaseParameter() {
		super();
		this.orderBy = new ArrayList<>();
	
	}

	/**
	 * Gets the order by.
	 *
	 * @return the order by
	 */
	public List<OrderBy> getOrderBy() {
		return orderBy;
	}

	/**
	 * Sets the order by.
	 *
	 * @param orderBy the new order by
	 */
	public void setOrderBy(List<OrderBy> orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * Adds the order by.
	 *
	 * @param sortKey the sort key
	 * @param orderType the order type
	 */
	public void addOrderBy(String sortKey, OrderType orderType) {
		if (StringUtils.isNotBlank(sortKey))
			this.orderBy.add(OrderBy.of(sortKey, orderType));
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
