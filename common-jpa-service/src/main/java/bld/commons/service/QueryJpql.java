/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.service.QueryJpql.java
 */
package bld.commons.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;

/**
 * The Class QueryJpql.
 *
 * @param <T> the generic type
 */
public abstract class QueryJpql<T> {

	
	/** The map one to many. */
	protected Map<String, LinkedHashSet<String>> mapOneToMany;
	
	
	
	/**
	 * Instantiates a new query jpql.
	 */
	public QueryJpql() {
		super();
		this.mapOneToMany=new HashMap<>();
	}

	/**
	 * Map one to many.
	 */
	public abstract void mapOneToMany();
	
	/**
	 * Select by filter.
	 *
	 * @return the string
	 */
	public abstract String selectByFilter();
	
	public abstract String selectIdByFilter();

	/**
	 * Count by filter.
	 *
	 * @return the string
	 */
	public abstract String countByFilter();
	
	
	/**
	 * Delete by filter.
	 *
	 * @return the string
	 */
	public abstract String deleteByFilter();


	/**
	 * Map condizioni.
	 *
	 * @return the map
	 */
	public abstract Map<String, String> mapConditions();

	/**
	 * Map delete conditions.
	 *
	 * @return the map
	 */
	public abstract Map<String, String> mapDeleteConditions();
	
	
	/**
	 * Map native conditions.
	 *
	 * @return the map
	 */
	public abstract Map<String, Map<String,String>> mapNativeConditions();
	
	/**
	 * Map native orders.
	 *
	 * @return the map
	 */
	public abstract Map<String, String> mapNativeOrders();
	
	/**
	 * Map jpa orders.
	 *
	 * @return the map
	 */
	public abstract Map<String, String> mapJpaOrders();
	
	
	/**
	 * Adds the join one to many.
	 *
	 * @param key the key
	 * @param join the join
	 */
	protected void addJoinOneToMany(String key, String... join) {
		if (!this.mapOneToMany.containsKey(key)) 
			this.mapOneToMany.put(key, new LinkedHashSet<>());
		this.mapOneToMany.get(key).addAll(new LinkedHashSet<>(Arrays.asList(join)));
	}

	/**
	 * Gets the map one to many.
	 *
	 * @return the map one to many
	 */
	public Map<String, LinkedHashSet<String>> getMapOneToMany() {
		if(MapUtils.isEmpty(this.mapOneToMany))
			this.mapOneToMany();
		return mapOneToMany;
	}
	
	
	
	
}
