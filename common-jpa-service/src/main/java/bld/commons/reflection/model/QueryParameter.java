/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.model.QueryFilter.java
 */
package bld.commons.reflection.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * The Class QueryFilter.
 *
 * @param <T>  the generic type
 * @param <ID> the generic type
 */
@SuppressWarnings("serial")
public class QueryParameter<T, ID> implements Serializable{

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

	/** The parameter filter. */
	private BaseParameter baseParameter;

	/**
	 * Instantiates a new query filter.
	 *
	 * @param id the id
	 */
	public QueryParameter(ID id) {
		init();
		this.id = id;
		this.mapParameters.put("id", id);
		
	}


	/**
	 * Instantiates a new query parameter.
	 *
	 * @param baseParameter the base parameter
	 */
	public QueryParameter(BaseParameter baseParameter) {
		super();
		init();
		this.baseParameter = baseParameter;
		if (baseParameter != null) {
			if (CollectionUtils.isNotEmpty(baseParameter.getOrderBy()))
				this.listOrderBy = baseParameter.getOrderBy();
			if (baseParameter.getPageNumber() != null && baseParameter.getPageSize() != null)
				this.pageable = PageRequest.of(baseParameter.getPageNumber(), baseParameter.getPageSize());
		}

	}

	/**
	 * Instantiates a new query filter.
	 *
	 * @param mapParameters the map parameters
	 */
	public QueryParameter(Map<String, Object> mapParameters) {
		super();
		init();
		this.mapParameters=mapParameters;
	}
	
	/**
	 * Instantiates a new query filter.
	 */
	public QueryParameter() {
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
	}

}
