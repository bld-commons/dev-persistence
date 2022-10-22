package bld.commons.reflection.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import bld.commons.reflection.type.OrderType;

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

	protected BaseQueryParameter() {
		super();
	}

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
		this.baseParameter = baseParameter;
		if (baseParameter != null) {
			if (CollectionUtils.isNotEmpty(baseParameter.getOrderBy()))
				this.listOrderBy = baseParameter.getOrderBy();
			if (baseParameter.getPageNumber() != null && baseParameter.getPageSize() != null)
				this.pageable = PageRequest.of(baseParameter.getPageNumber(), baseParameter.getPageSize());
		}

	}

	public ID getId() {
		return id;
	}

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

	public List<OrderBy> getListOrderBy() {
		return listOrderBy;
	}

	public void setListOrderBy(List<OrderBy> listOrderBy) {
		this.listOrderBy = listOrderBy;
	}

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	public BaseParameter getBaseParameter() {
		return baseParameter;
	}

	public void setBaseParameter(BaseParameter baseParameter) {
		this.baseParameter = baseParameter;
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

	public void addOrderBy(OrderBy orderBy) {
		if (orderBy != null)
			this.listOrderBy.add(orderBy);
	}
	
	public void addOrderBy(String field,OrderType ordertType) {
		if (StringUtils.isNotBlank(field))
			this.listOrderBy.add(new OrderBy(field, ordertType));
	}

}
