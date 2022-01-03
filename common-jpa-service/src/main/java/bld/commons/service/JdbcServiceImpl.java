/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.base.service.JdbcServiceImpl.java
 */
package bld.commons.service;

import java.util.List;
import java.util.Map;

import bld.commons.reflection.model.BuildQueryFilter;
import bld.commons.reflection.model.QueryFilter;

// TODO: Auto-generated Javadoc
/**
 * The Class JdbcServiceImpl.
 *
 * @param <T> the generic type
 * @param <ID> the generic type
 */
public abstract class JdbcServiceImpl<T, ID> extends JpaServiceImpl<T, ID> implements JdbcService<T, ID> {

	
	


	/**
	 * Jdbc select by filter.
	 *
	 * @param <K> the key type
	 * @param <I> the generic type
	 * @param queryFilter the query filter
	 * @param sql the sql
	 * @return the list
	 */
	@Override
	public <K, I> List<K> jdbcSelectByFilter(QueryFilter<K, I> queryFilter,String sql) {
		return this.jdbcSelectByFilter(queryFilter,super.queryJpl.mapNativeConditions(),sql);
	}


	/**
	 * Jdbc count by filter.
	 *
	 * @param <K> the key type
	 * @param <I> the generic type
	 * @param queryFilter the query filter
	 * @param count the count
	 * @return the long
	 */
	@Override
	public <K,I>Long jdbcCountByFilter(QueryFilter<K, I> queryFilter,String count) {
		BuildQueryFilter<K, I> buildQueryFilter = getBuildQueryFilter(queryFilter, count);
		return this.jdbcCountNativeQuery(buildQueryFilter);
	}
	
	
	/**
	 * Gets the builds the query filter.
	 *
	 * @param <I> the generic type
	 * @param <K> the key type
	 * @param queryFilter the query filter
	 * @param sql the sql
	 * @return the builds the query filter
	 */
	private <I, K> BuildQueryFilter<K, I> getBuildQueryFilter(QueryFilter<K, I> queryFilter, String sql) {
		BuildQueryFilter<K, I>buildQueryFilter=new BuildQueryFilter<>();
		if(queryFilter.getFilterParameter()!=null)
			queryFilter=reflectionUtils.dataToMap(queryFilter);
		buildQueryFilter.setQueryFilter(queryFilter);
		buildQueryFilter.setSql(sql);
		return buildQueryFilter;
	}


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
	@Override
	public <K, I> List<K> jdbcSelectByFilter(QueryFilter<K, I> queryFilter,Map<String,String>mapConditions,String sql){
		BuildQueryFilter<K, I> buildQueryFilter = getBuildQueryFilter(queryFilter, sql);
		buildQueryFilter.setMapConditions(mapConditions);
		return super.jdbcSelectByFilter(buildQueryFilter);
	}

}
