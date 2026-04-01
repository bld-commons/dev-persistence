package com.bld.commons.reflection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a field in a {@link com.bld.commons.reflection.model.BaseQueryParameter}
 * subclass as a multi-column tuple comparison for JPQL or native SQL queries.
 *
 * <p>The annotated field must be of type
 * {@link com.bld.commons.reflection.model.TupleParameter}. The reflection engine
 * uses the parameter names declared in the {@code TupleParameter} together with
 * the field's current value to generate a row-value {@code IN} clause such as
 * {@code (col1, col2) IN ((:col10, :col20), (:col11, :col21))}.</p>
 *
 * <p><b>Example</b></p>
 * <pre>{@code
 * public class InventoryFilter extends BaseQueryParameter<Inventory, Long> {
 *
 *     @TupleComparison({"productId", "warehouseId"})
 *     private TupleParameter productWarehouse;
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 * @see com.bld.commons.reflection.model.TupleParameter
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface TupleComparison {

	/**
	 * The named parameters that form the tuple, in the same order as the
	 * corresponding database columns.
	 *
	 * @return the parameter names; must contain at least two elements
	 */
	public String[] value();
}
