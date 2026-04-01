package com.bld.commons.utils;

import java.util.List;

import jakarta.persistence.Tuple;

/**
 * Functional interface for mapping a single JPA {@link Tuple} row to a typed result object.
 *
 * <p>Implement this interface (typically as a lambda) when executing a
 * {@link jakarta.persistence.TypedQuery} that returns {@code Tuple} rows and you
 * need custom logic to populate the target list. The framework invokes
 * {@link #rowMapper(List, Tuple, int)} once for each row in the result set.</p>
 *
 * <p><b>Example</b></p>
 * <pre>{@code
 * JpaRowMapper<ProductSummary> mapper = (list, row, i) -> {
 *     ProductSummary summary = new ProductSummary();
 *     summary.setId(row.get("id", Long.class));
 *     summary.setName(row.get("name", String.class));
 *     list.add(summary);
 * };
 * }</pre>
 *
 * @param <K> the target type to which each tuple row is mapped
 * @author Francesco Baldi
 * @see JdbcRowMapper
 */
@FunctionalInterface
public interface JpaRowMapper<K> {

	/**
	 * Maps a single {@link Tuple} row to an instance of {@code K} and adds it to
	 * the result list.
	 *
	 * @param result the accumulating list of mapped objects; never {@code null}
	 * @param row    the current JPA {@link Tuple} row; never {@code null}
	 * @param i      the zero-based row index within the result set
	 */
	public void rowMapper(List<K> result, Tuple row, int i);
}
