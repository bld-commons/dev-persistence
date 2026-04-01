package com.bld.commons.reflection.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.bld.commons.exception.JpaServiceException;

import io.jsonwebtoken.lang.Arrays;

/**
 * Represents a multi-column tuple comparison used in JPQL or native SQL queries.
 *
 * <p>A {@code TupleParameter} groups two or more named parameters together so that
 * the query engine can generate a row-value comparison such as
 * {@code (col1, col2) IN ((:col10, :col20), (:col11, :col21), ...)}. The minimum
 * number of parameters is two; fewer than two will cause the constructor to throw
 * a {@link com.bld.commons.exception.JpaServiceException}.</p>
 *
 * <p>Use {@link com.bld.commons.reflection.annotations.TupleComparison} on a
 * {@link BaseQueryParameter} field to bind a {@code TupleParameter} automatically.</p>
 *
 * <p><b>Example</b></p>
 * <pre>{@code
 * TupleParameter tp = new TupleParameter(new String[]{"productId", "warehouseId"});
 * tp.setObjects(new Object[]{1L, 10L}, new Object[]{2L, 10L});
 * // produces: (productId0, warehouseId0) or (productId1, warehouseId1)
 * }</pre>
 *
 * @author Francesco Baldi
 * @see com.bld.commons.reflection.annotations.TupleComparison
 */
public class TupleParameter {

	private Set<Object> objects;

	private String[] params;

	/**
	 * Constructs a new {@code TupleParameter} with the specified parameter names.
	 *
	 * @param params the named parameters that form the tuple; must contain at least two elements
	 * @throws com.bld.commons.exception.JpaServiceException if {@code params} is {@code null}
	 *         or has fewer than two elements
	 */
	public TupleParameter(String[] params) {
		super();
		if(params==null || params.length<2)
			throw new JpaServiceException("The params items cannot be less than 2");
		this.params = params;
	}

	/**
	 * Returns the set of tuple objects (rows) to compare against.
	 *
	 * @return the set of objects; may be {@code null} if not yet set
	 */
	public Set<Object> getObjects() {
		return objects;
	}

	/**
	 * Returns the named parameter identifiers that make up the tuple.
	 *
	 * @return the parameter names array; never {@code null}
	 */
	public String[] getParams() {
		return params;
	}

	/**
	 * Sets the tuple objects from a varargs array of values.
	 * Ignored if the array is empty or {@code null}.
	 *
	 * @param objects the objects to add to the comparison set
	 */
	public void setObjects(Object... objects) {
		if(ArrayUtils.isNotEmpty(objects))
			this.objects = new HashSet<>(Arrays.asList(objects));
	}

	/**
	 * Sets the tuple objects from a collection.
	 * Ignored if the collection is empty or {@code null}.
	 *
	 * @param objects the collection of objects to add to the comparison set
	 */
	public void setObjects(Collection<Object> objects) {
		if(CollectionUtils.isNotEmpty(objects))
			this.objects=new HashSet<>(objects);
	}

	/**
	 * Generates a single parameterised tuple condition for use in a SQL {@code IN} clause.
	 *
	 * <p>For index {@code i} and params {@code ["productId", "warehouseId"]} this produces
	 * {@code (:productId0, :warehouseId0)}.</p>
	 *
	 * @param i the zero-based row index used to disambiguate named parameters
	 * @return the tuple condition string, e.g. {@code "(:param0_0, :param1_0)"}
	 */
	public String getCondition(int i) {
		String condition="";
		for(String param:params)
			condition+=",:"+param+i;
		return "("+condition.substring(1)+")";
	}

}
