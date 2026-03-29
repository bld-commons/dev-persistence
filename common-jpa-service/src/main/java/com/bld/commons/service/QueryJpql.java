/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.service.QueryJpql.java
 */
package com.bld.commons.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;

/**
 * Abstract holder for all the pre-built JPQL/SQL strings and condition maps
 * associated with a specific JPA entity.
 *
 * <p>Concrete subclasses are generated at compile time by the
 * {@code processor-jpa-service} annotation processor when it encounters
 * a service interface annotated with {@code @QueryBuilder}. The generated class
 * (e.g., {@code ProductQueryJpqlImpl}) contains:
 * <ul>
 *   <li>Static {@code SELECT}, {@code COUNT}, {@code DELETE}, and
 *       {@code SELECT id} JPQL strings.</li>
 *   <li>A static condition map ({@code Map<String, String>}) where each entry
 *       maps a parameter name to its JPQL condition fragment (e.g.,
 *       {@code "name" → "AND e.name = :name"}).</li>
 *   <li>A static order map ({@code Map<String, String>}) for sort key resolution.</li>
 *   <li>A one-to-many join map that instructs the service to add JOIN FETCH
 *       clauses only when the corresponding filter parameter is present.</li>
 * </ul>
 *
 * <p>Application code does not interact with this class directly;
 * it is autowired into {@link JpaServiceImpl} and invoked internally.</p>
 *
 * @param <T> the JPA entity type this query provider belongs to
 * @author Francesco Baldi
 * @see JpaServiceImpl
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
