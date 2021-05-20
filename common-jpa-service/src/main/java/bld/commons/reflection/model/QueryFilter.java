/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.model.QueryFilter.java
 */
package bld.commons.reflection.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * The Class QueryFilter.
 *
 * @param <T> the generic type
 * @param <ID> the generic type
 */
public class QueryFilter <T, ID>{
	
	/** The Constant ID. */
	public final static String ID = "id";

	/** The id. */
	private ID id;

	/** The check nullable. */
	private Set<String> checkNullable;
	
	/** The map parameters. */
	private Map<String, Object> mapParameters;

	/** The sort key. */
	private String sortKey;

	/** The sort order. */
	private String sortOrder;

	/** The pageable. */
	private Pageable pageable;

	/** The class filter. */
	private Class<T> classFilter;
	
	/** The parameter filter. */
	private FilterParameter filterParameter;
	
	
	/**
	 * Instantiates a new query filter.
	 *
	 * @param id the id
	 */
	public QueryFilter(ID id) {
		this.id = id;
	}

	/**
	 * Instantiates a new query filter.
	 *
	 * @param filterParameter the filter
	 */
	public QueryFilter(FilterParameter filterParameter) {
		super();
		this.checkNullable = new HashSet<>();
		this.mapParameters=new HashMap<>();
		this.filterParameter=filterParameter;
		if (filterParameter != null) {
			this.sortKey = filterParameter.getSortKey();
			this.sortOrder = filterParameter.getSortOrder();
			if (filterParameter.getPageNumber() != null && filterParameter.getPageSize() != null)
				this.pageable = PageRequest.of(filterParameter.getPageNumber(), filterParameter.getPageSize());
		}

	}

	

	public QueryFilter() {
		super();
		this.checkNullable = new HashSet<>();
		this.mapParameters=new HashMap<>();
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
	 * Gets the check nullable.
	 *
	 * @return the check nullable
	 */
	public Set<String> getCheckNullable() {
		return checkNullable;
	}
	
	/**
	 * Gets the map parameters.
	 *
	 * @return the map parameters
	 */
	public Map<String, Object> getMapParameters() {
		return mapParameters;
	}


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

	
	public void setPageable(Integer page, Integer size) {
		if (page != null && size != null)
			this.pageable = PageRequest.of(page, size);
	}
	
	/**
	 * Gets the class filter.
	 *
	 * @return the class filter
	 */
	public Class<T> getClassFilter() {
		return classFilter;
	}

	/**
	 * Sets the class filter.
	 *
	 * @param classFilter the new class filter
	 */
	public void setClassFilter(Class<T> classFilter) {
		this.classFilter = classFilter;
	}


	/**
	 * Gets the parameter filter.
	 *
	 * @return the parameter filter
	 */
	public FilterParameter getFilterParameter() {
		return filterParameter;
	}


	/**
	 * Sets the parameter filter.
	 *
	 * @param parameterFilter the new parameter filter
	 */
	public void setFilterParameter(FilterParameter parameterFilter) {
		this.filterParameter = parameterFilter;
	}
	
	
	
	
	
}
