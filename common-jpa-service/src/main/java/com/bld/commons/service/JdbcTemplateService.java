package com.bld.commons.service;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.bld.commons.reflection.model.NativeQueryParameter;

// TODO: Auto-generated Javadoc
/**
 * The Interface JdbcTemplateService.
 *
 * @param <T> the generic type
 * @param <ID> the generic type
 */
public interface JdbcTemplateService<T, ID> extends JpaService<T, ID> {

	/**
	 * Jdbc find by filter.
	 *
	 * @param <K> the key type
	 * @param queryParameter the query parameter
	 * @param sql the sql
	 * @return the list
	 */
	public <K> List<K> jdbcFindByFilter(NativeQueryParameter<K, ID> queryParameter, String sql);

	/**
	 * Jdbc single result by filter.
	 *
	 * @param <K> the key type
	 * @param queryParameter the query parameter
	 * @param sql the sql
	 * @return the k
	 */
	public <K> K jdbcSingleResultByFilter(NativeQueryParameter<K, ID> queryParameter, String sql);

	public <K> List<K> jdbcFindByFilter(NativeQueryParameter<K, ID> queryParameter, String sql, RowMapper<K> mapper);

}
