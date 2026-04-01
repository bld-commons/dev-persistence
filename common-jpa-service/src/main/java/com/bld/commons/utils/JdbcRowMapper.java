package com.bld.commons.utils;

import java.sql.ResultSet;
import java.util.List;

/**
 * Functional interface for mapping a single JDBC {@link ResultSet} row to a typed result object.
 *
 * <p>Implement this interface (typically as a lambda) when executing a native SQL
 * query via JDBC and you need custom logic to populate the target list. The framework
 * invokes {@link #rowMapper(List, ResultSet, int)} once for each row returned by
 * the query.</p>
 *
 * <p><b>Example</b></p>
 * <pre>{@code
 * JdbcRowMapper<ProductSummary> mapper = (list, rs, i) -> {
 *     ProductSummary summary = new ProductSummary();
 *     summary.setId(rs.getLong("id"));
 *     summary.setName(rs.getString("name"));
 *     list.add(summary);
 * };
 * }</pre>
 *
 * @param <K> the target type to which each result-set row is mapped
 * @author Francesco Baldi
 * @see JpaRowMapper
 */
@FunctionalInterface
public interface JdbcRowMapper<K> {

	/**
	 * Maps a single JDBC {@link ResultSet} row to an instance of {@code K} and adds
	 * it to the result list.
	 *
	 * @param list the accumulating list of mapped objects; never {@code null}
	 * @param row  the current {@link ResultSet} positioned at the row to map; never {@code null}
	 * @param i    the zero-based row index within the result set
	 */
	public void rowMapper(List<K> list, ResultSet row, int i);
}
