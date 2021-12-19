/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.model.ObjectResponse.java
 */
package bld.commons.reflection.model;

import javax.validation.Valid;

/**
 * The Class ObjectResponse.
 *
 * @param <T> the generic type
 */
public class ObjectResponse<T> {

	/** The data. */
	@Valid
	private T data;
	
	/**
	 * Instantiates a new object response.
	 */
	public ObjectResponse() {
		super();
	}
	
	/**
	 * Instantiates a new object response.
	 *
	 * @param data the data
	 */
	public ObjectResponse(T data) {
		super();
		this.data = data;
	}




	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public T getData() {
		return data;
	}

	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(T data) {
		this.data = data;
	}
	
	
	
}
