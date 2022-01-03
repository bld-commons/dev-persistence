/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.base.service.JdbcService.java
 */
package bld.commons.service;

import java.util.List;
import java.util.Map;

import bld.commons.reflection.model.QueryFilter;

// TODO: Auto-generated Javadoc
/**
 * The Interface JdbcService.
 *
 * @param <T> the generic type
 * @param <ID> the generic type
 */
public interface JdbcService<T, ID> extends JpaService<T, ID> {


	/**
	 * Jdbc select by filter.
	 *
	 * @param <K> the key type
	 * @param <I> the generic type
	 * @param queryFilter the query filter
	 * @param sql the sql
	 * @return the list
	 */
	public <K, I> List<K> jdbcSelectByFilter(QueryFilter<K, I> queryFilter,String sql);


	/**
	 * Jdbc select by filter.
	 *
	 * @param <K> the key type
	 * @param <I> the generic type
	 * @param queryFilter the query filter
	 * @param mapConditions the map conditions
	 * @param sql the sql
	 * @return the list
	 */
	public <K, I> List<K> jdbcSelectByFilter(QueryFilter<K, I> queryFilter,Map<String,String>mapConditions,String sql);


	/**
	 * Jdbc count by filter.
	 *
	 * @param <K> the key type
	 * @param <I> the generic type
	 * @param queryFilter the query filter
	 * @param count the count
	 * @return the long
	 */
	public <K, I> Long jdbcCountByFilter(QueryFilter<K, I> queryFilter, String count);
}
