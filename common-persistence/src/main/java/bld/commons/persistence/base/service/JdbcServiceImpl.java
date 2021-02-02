package bld.commons.persistence.base.service;

import java.util.List;
import java.util.Map;

import bld.commons.persistence.reflection.model.BuildQueryFilter;
import bld.commons.persistence.reflection.model.QueryFilter;

// TODO: Auto-generated Javadoc
/**
 * The Class JdbcServiceImpl.
 *
 * @param <T> the generic type
 * @param <ID> the generic type
 */
public abstract class JdbcServiceImpl<T, ID> extends JpaServiceImpl<T, ID> implements JdbcService<T, ID> {

	
	

	/**
	 * Jdbc select.
	 *
	 * @param <K> the key type
	 * @param <I> the generic type
	 * @param queryFilter the query filter
	 * @param sql the sql
	 * @return the list
	 * @throws Exception the exception
	 */
	@Override
	public <K, I> List<K> jdbcSelect(QueryFilter<K, I> queryFilter,String sql) throws Exception {
		BuildQueryFilter<K, I> buildQueryFilter = getBuildQueryFilter(queryFilter, sql);
		return super.jdbcSelect(buildQueryFilter);
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
	 * @throws Exception the exception
	 */
	@Override
	public <K, I> List<K> jdbcSelectByFilter(QueryFilter<K, I> queryFilter,Map<String,String>mapConditions,String sql) throws Exception {
		BuildQueryFilter<K, I> buildQueryFilter = getBuildQueryFilter(queryFilter, sql);
		buildQueryFilter.setMapConditions(mapConditions);
		return super.jdbcSelectByFilter(buildQueryFilter);
	}

}
