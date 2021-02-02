package bld.commons.persistence.base.service;

import java.util.List;
import java.util.Map;

import bld.commons.persistence.reflection.model.QueryFilter;

public interface JdbcService<T, ID> extends JpaService<T, ID> {


	public <K, I> List<K> jdbcSelect(QueryFilter<K, I> queryFilter,String sql) throws Exception;

	public <K, I> List<K> jdbcSelectByFilter(QueryFilter<K, I> queryFilter,Map<String,String>mapConditions,String sql) throws Exception;
}
