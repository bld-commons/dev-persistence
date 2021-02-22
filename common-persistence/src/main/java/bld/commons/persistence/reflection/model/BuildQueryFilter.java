/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.model.BuildQueryFilter.java
 */
package bld.commons.persistence.reflection.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class BuildQueryFilter.
 *
 * @param <T> the generic type
 * @param <ID> the generic type
 */
public class BuildQueryFilter<T, ID> {

	/** The map conditions. */
	private Map<String, String> mapConditions;

	/** The query filter. */
	private QueryFilter<T, ID> queryFilter;
	
	/** The sql. */
	private String sql;


	/**
	 * Instantiates a new builds the query filter.
	 *
	 * @param mapConditions the map conditions
	 * @param queryFilter the query filter
	 * @param sql the sql
	 */
	public BuildQueryFilter(Map<String, String> mapConditions,QueryFilter<T, ID> queryFilter, String sql) {
		super();
		this.mapConditions = mapConditions;
		this.queryFilter = queryFilter;
		this.sql = sql;
	}

	/**
	 * Instantiates a new builds the query filter.
	 */
	public BuildQueryFilter() {
		super();
		this.mapConditions=new HashMap<>();
	}

	/**
	 * Gets the map conditions.
	 *
	 * @return the map conditions
	 */
	public Map<String, String> getMapConditions() {
		return mapConditions;
	}

	/**
	 * Sets the map conditions.
	 *
	 * @param mapConditions the new map conditions
	 */
	public void setMapConditions(Map<String, String> mapConditions) {
		this.mapConditions = mapConditions;
	}

	/**
	 * Gets the query filter.
	 *
	 * @return the query filter
	 */
	public QueryFilter<T, ID> getQueryFilter() {
		return queryFilter;
	}

	/**
	 * Sets the query filter.
	 *
	 * @param queryFilter the new query filter
	 */
	public void setQueryFilter(QueryFilter<T, ID> queryFilter) {
		this.queryFilter = queryFilter;
	}

	/**
	 * Gets the sql.
	 *
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * Sets the sql.
	 *
	 * @param sql the new sql
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	

	
	
	
}
