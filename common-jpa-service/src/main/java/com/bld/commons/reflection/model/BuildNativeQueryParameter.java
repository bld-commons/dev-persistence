/*
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.model.BuildNativeQueryParameter.java 
 */
package com.bld.commons.reflection.model;

import java.util.Map;

/**
 * The Class BuildNativeQueryParameter.
 *
 * @param <T> the generic type
 * @param <ID> the generic type
 */
public class BuildNativeQueryParameter<T, ID> extends BuildQueryParameter<T, ID, NativeQueryParameter<T, ID>,Map<String,String>> {

	/**
	 * Instantiates a new builds the native query parameter.
	 *
	 * @param mapConditions the map conditions
	 * @param mapOrders the map orders
	 * @param queryParameter the query parameter
	 * @param sql the sql
	 */
	public BuildNativeQueryParameter(Map<String, Map<String,String>> mapConditions, Map<String, String> mapOrders, NativeQueryParameter<T, ID> queryParameter, String sql) {
		super(mapConditions, mapOrders, queryParameter, sql);
	}

}