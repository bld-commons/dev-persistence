/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.model.NativeQueryParameter.java
 */
package bld.commons.reflection.model;

import java.util.Map;

/**
 * The Class NativeQueryParameter.
 *
 * @param <T> the generic type
 * @param <ID> the generic type
 */
@SuppressWarnings("serial")
public class NativeQueryParameter<T, ID> extends QueryParameter<T, ID> {

	/** The result class. */
	private Class<T> resultClass;

	/**
	 * Instantiates a new native query parameter.
	 *
	 * @param resultClass the result class
	 */
	public NativeQueryParameter(Class<T> resultClass) {
		super();
		this.resultClass = resultClass;
	}

	/**
	 * Instantiates a new native query parameter.
	 *
	 * @param resultClass the result class
	 * @param filterParameter the filter parameter
	 */
	public NativeQueryParameter(Class<T> resultClass, BaseParameter filterParameter) {
		super(filterParameter);
		this.resultClass = resultClass;
	}

	/**
	 * Instantiates a new native query parameter.
	 *
	 * @param resultClass the result class
	 * @param id the id
	 */
	public NativeQueryParameter(Class<T> resultClass, ID id) {
		super(id);
		this.resultClass = resultClass;
	}

	/**
	 * Instantiates a new native query parameter.
	 *
	 * @param resultClass the result class
	 * @param mapParameters the map parameters
	 */
	public NativeQueryParameter(Class<T> resultClass, Map<String, Object> mapParameters) {
		super(mapParameters);
		this.resultClass = resultClass;
	}

	/**
	 * Gets the result class.
	 *
	 * @return the result class
	 */
	public Class<T> getResultClass() {
		return resultClass;
	}

}
