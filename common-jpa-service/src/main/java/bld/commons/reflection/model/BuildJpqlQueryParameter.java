/*
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.model.BuildJpqlQueryParameter.java 
 */
package bld.commons.reflection.model;

import java.util.Map;

/**
 * The Class BuildJpqlQueryParameter.
 *
 * @param <T> the generic type
 * @param <ID> the generic type
 */
public class BuildJpqlQueryParameter<T, ID> extends BuildQueryParameter<T, ID, QueryParameter<T, ID>> {

	/**
	 * Instantiates a new builds the jpql query parameter.
	 *
	 * @param mapConditions the map conditions
	 * @param mapOrders the map orders
	 * @param queryParameter the query parameter
	 * @param sql the sql
	 */
	public BuildJpqlQueryParameter(Map<String, String> mapConditions, Map<String, String> mapOrders, QueryParameter<T, ID> queryParameter, String sql) {
		super(mapConditions, mapOrders, queryParameter, sql);
	}

}
