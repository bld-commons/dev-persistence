/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.model.QueryFilter.java
 */
package bld.commons.persistence.reflection.model;

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
	private ParameterFilter parameterFilter;
	
	
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
	 * @param filter the filter
	 */
	public QueryFilter(ParameterFilter filter) {
		super();
		this.checkNullable = new HashSet<>();
		this.mapParameters=new HashMap<>();
		this.parameterFilter=filter;
		if (filter != null) {
			this.sortKey = filter.getSortKey();
			this.sortOrder = filter.getSortOrder();
			if (filter.getPageNumber() != null && filter.getPageSize() != null)
				this.pageable = PageRequest.of(filter.getPageNumber(), filter.getPageSize());
		}

	}

	/**
	 * Instantiates a new query filter.
	 *
	 * @param page          the page
	 * @param size          the size
	 * @param mapParameters the map parameters.
	 * @param checkNullable the check nullable.
	 */
	public QueryFilter(Integer page, Integer size, Map<String, Object> mapParameters, Set<String> checkNullable) {
		super();
		this.checkNullable = checkNullable;
		this.mapParameters=mapParameters;
		if (page != null && size != null)
			this.pageable = PageRequest.of(page, size);
	}

	/**
	 * Instantiates a new query filter.
	 *
	 * @param page          the page
	 * @param size          the size
	 * @param mapParameters the map parameters.
	 */
	public QueryFilter(Integer page, Integer size, Map<String, Object> mapParameters) {
		super();
		this.checkNullable = new HashSet<>();
		this.mapParameters=mapParameters;
		if (page != null && size != null)
			this.pageable = PageRequest.of(page, size);
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
	 * Sets the check nullable.
	 *
	 * @param checkNullable the new check nullable
	 */
	public void setCheckNullable(Set<String> checkNullable) {
		this.checkNullable = checkNullable;
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
	 * Sets the map parameters.
	 *
	 * @param mapParameters the new map parameters
	 */
	public void setMapParameters(Map<String, Object> mapParameters) {
		this.mapParameters = mapParameters;
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
	public ParameterFilter getParameterFilter() {
		return parameterFilter;
	}


	/**
	 * Sets the parameter filter.
	 *
	 * @param parameterFilter the new parameter filter
	 */
	public void setParameterFilter(ParameterFilter parameterFilter) {
		this.parameterFilter = parameterFilter;
	}
	
	
	
	
	
}
