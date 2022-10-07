/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.model.BuildQueryFilter.java
 */
package bld.commons.reflection.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class BuildQueryFilter.
 *
 * @param <T> the generic type
 * @param <ID> the generic type
 * @param <QP> the generic type
 */
public abstract class BuildQueryParameter<T, ID,QP extends BaseQueryParameter<T,ID>> {

	/** The map conditions. */
	private Map<String, String> mapConditions;

	/** The query parameter. */
	private QP queryParameter;
	
	/** The sql. */
	private StringBuilder sql;


	/**
	 * Instantiates a new builds the query filter.
	 *
	 * @param mapConditions the map conditions
	 * @param queryParameter the query filter
	 * @param sql the sql
	 */
	public BuildQueryParameter(Map<String, String> mapConditions,QP queryParameter, String sql) {
		super();
		this.mapConditions = mapConditions;
		this.queryParameter = queryParameter;
		this.sql = new StringBuilder(sql);
	}
	
	
	/**
	 * Instantiates a new builds the query filter.
	 */
	public BuildQueryParameter() {
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
	 * Gets the query filter.
	 *
	 * @return the query filter
	 */
	public QP getQueryParameter() {
		return queryParameter;
	}


	/**
	 * Gets the sql.
	 *
	 * @return the sql
	 */
	public StringBuilder getSql() {
		return sql;
	}

	/**
	 * Sets the sql.
	 *
	 * @param sql the new sql
	 */
	public void setSql(StringBuilder sql) {
		this.sql = sql;
	}
	
	

	
	
	
}
