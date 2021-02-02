package bld.commons.persistence.base.service;

import java.util.List;
import java.util.Map;

import bld.commons.persistence.reflection.model.BuildQueryFilter;
import bld.commons.persistence.reflection.model.QueryFilter;

public abstract class JdbcServiceImpl<T, ID> extends JpaServiceImpl<T, ID> implements JdbcService<T, ID> {

	
	

	@Override
	public <K, I> List<K> jdbcSelect(QueryFilter<K, I> queryFilter,String sql) throws Exception {
		BuildQueryFilter<K, I> buildQueryFilter = getBuildQueryFilter(queryFilter, sql);
		return super.jdbcSelect(buildQueryFilter);
	}

	private <I, K> BuildQueryFilter<K, I> getBuildQueryFilter(QueryFilter<K, I> queryFilter, String sql) {
		BuildQueryFilter<K, I>buildQueryFilter=new BuildQueryFilter<>();
		buildQueryFilter.setQueryFilter(queryFilter);
		buildQueryFilter.setSql(sql);
		return buildQueryFilter;
	}

	@Override
	public <K, I> List<K> jdbcSelectByFilter(QueryFilter<K, I> queryFilter,Map<String,String>mapConditions,String sql) throws Exception {
		BuildQueryFilter<K, I> buildQueryFilter = getBuildQueryFilter(queryFilter, sql);
		buildQueryFilter.setMapConditions(mapConditions);
		return super.jdbcSelectByFilter(buildQueryFilter);
	}

}
