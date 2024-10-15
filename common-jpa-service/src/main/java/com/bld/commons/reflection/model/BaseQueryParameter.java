/*
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.model.BaseQueryParameter.java 
 */
package com.bld.commons.reflection.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.bld.commons.reflection.type.OrderType;

/**
 * The Class BaseQueryParameter.
 *
 * @param <T>  the generic type
 * @param <ID> the generic type
 */
@SuppressWarnings("serial")
public abstract class BaseQueryParameter<T, ID> implements Serializable {

	
	
	/** The Constant ID. */
	public final static String ID = "id";

	/** The id. */
	private ID id;

	/** The list order by. */
	private List<OrderBy> listOrderBy;

	/** The pageable. */
	private Pageable pageable;

	/** The parameter filter. */
	private BaseParameter baseParameter;

	/**
	 * Instantiates a new base query parameter.
	 */
	protected BaseQueryParameter() {
		super();
	}

	/**
	 * Instantiates a new base query parameter.
	 *
	 * @param id the id
	 */
	protected BaseQueryParameter(ID id) {
		super();
		this.id = id;
	}

	/**
	 * Instantiates a new query parameter.
	 *
	 * @param baseParameter the base parameter
	 */
	protected BaseQueryParameter(BaseParameter baseParameter) {
		super();
		this.setBaseParameter(baseParameter);
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public ID getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(ID id) {
		this.id = id;
	}

	/**
	 * Inits the.
	 */
	protected void init() {
		if (CollectionUtils.isEmpty(this.listOrderBy))
			this.listOrderBy = new ArrayList<>();
	}

	/**
	 * Gets the list order by.
	 *
	 * @return the list order by
	 */
	public List<OrderBy> getListOrderBy() {
		return listOrderBy;
	}

	/**
	 * Sets the list order by.
	 *
	 * @param listOrderBy the new list order by
	 */
	public void setListOrderBy(List<OrderBy> listOrderBy) {
		this.listOrderBy = listOrderBy;
	}

	/**
	 * Gets the pageable.
	 *
	 * @return the pageable
	 */
	public Pageable getPageable() {
		return pageable;
	}

	/**
	 * Sets the pageable.
	 *
	 * @param pageable the new pageable
	 */
	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	/**
	 * Gets the base parameter.
	 *
	 * @return the base parameter
	 */
	public BaseParameter getBaseParameter() {
		return baseParameter;
	}

	/**
	 * Sets the base parameter.
	 *
	 * @param baseParameter the new base parameter
	 */
	public void setBaseParameter(BaseParameter baseParameter) {
		this.baseParameter = baseParameter;
		if (baseParameter != null) {
			if (CollectionUtils.isNotEmpty(baseParameter.getOrderBy()))
				this.listOrderBy = baseParameter.getOrderBy();
			if (baseParameter.getPageNumber() != null && baseParameter.getPageSize() != null)
				this.pageable = PageRequest.of(baseParameter.getPageNumber(), baseParameter.getPageSize());
		}
	}

	/**
	 * Sets the pageable.
	 *
	 * @param page the page
	 * @param size the size
	 */
	public void setPageable(Integer page, Integer size) {
		if (page != null && size != null)
			this.pageable = PageRequest.of(page, size);
	}


	/**
	 * Adds the order by.
	 *
	 * @param listOrderBy the list order by
	 */
	public void addOrderBy(OrderBy... listOrderBy) {
		if (ArrayUtils.isNotEmpty(listOrderBy))
			for (OrderBy orderBy : listOrderBy)
				this.listOrderBy.add(orderBy);
	}

	/**
	 * Adds the order by.
	 *
	 * @param field      the field
	 * @param ordertType the ordert type
	 */
	public void addOrderBy(String field, OrderType ordertType) {
		if (StringUtils.isNotBlank(field))
			this.listOrderBy.add(OrderBy.of(field, ordertType));
	}
	
	/**
	 * Adds the parameter.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public abstract void addParameter(String key,Object value);
	
}
