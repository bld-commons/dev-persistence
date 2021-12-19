/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.model.QueryFilter.java
 */
package bld.commons.reflection.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

// TODO: Auto-generated Javadoc
/**
 * The Class QueryFilter.
 *
 * @param <T>  the generic type
 * @param <ID> the generic type
 */
public class QueryFilter<T, ID> {

	/** The Constant ID. */
	public final static String ID = "id";

	/** The id. */
	private ID id;

	/** The check nullable. */
	private Set<String> nullables;

	/** The map parameters. */
	private Map<String, Object> mapParameters;

	/** The list order by. */
	private List<OrderBy> listOrderBy;

	/** The pageable. */
	private Pageable pageable;

	/** The result class. */
	private Class<T> resultClass;

	/** The parameter filter. */
	private FilterParameter filterParameter;

	/**
	 * Instantiates a new query filter.
	 *
	 * @param id the id
	 */
	public QueryFilter(ID id) {
		init();
		this.id = id;
		this.mapParameters.put("id", id);
		
	}

	/**
	 * Instantiates a new query filter.
	 *
	 * @param filterParameter the filter
	 */
	public QueryFilter(FilterParameter filterParameter) {
		super();
		init();
		this.filterParameter = filterParameter;
		if (filterParameter != null) {
			if (CollectionUtils.isNotEmpty(filterParameter.getOrderBy()))
				this.listOrderBy = filterParameter.getOrderBy();
			if (filterParameter.getPageNumber() != null && filterParameter.getPageSize() != null)
				this.pageable = PageRequest.of(filterParameter.getPageNumber(), filterParameter.getPageSize());
		}

	}

	/**
	 * Instantiates a new query filter.
	 *
	 * @param mapParameters the map parameters
	 */
	public QueryFilter(Map<String, Object> mapParameters) {
		super();
		init();
		this.mapParameters=mapParameters;
	}
	
	/**
	 * Instantiates a new query filter.
	 */
	public QueryFilter() {
		super();
		init();
	}

	/**
	 * Inits the.
	 */
	private void init() {
		this.nullables = new HashSet<>();
		this.mapParameters = new HashMap<>();
		this.listOrderBy = new ArrayList<>();
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
	public Set<String> getNullables() {
		return nullables;
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
	 * Adds the parameter.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void addParameter(String key,Object value) {
		if(key!=null && value!=null)
			this.mapParameters.put(key, value);
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
	 * Gets the result class.
	 *
	 * @return the result class
	 */
	public Class<T> getResultClass() {
		return resultClass;
	}

	
	/**
	 * Sets the result class.
	 *
	 * @param classFilter the new result class
	 */
	public void setResultClass(Class<T> classFilter) {
		this.resultClass = classFilter;
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
