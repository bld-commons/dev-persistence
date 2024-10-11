/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.exception.JpaServiceException.java
 */
package com.bld.commons.exception;

/**
 * The Class JpaServiceException.
 */
@SuppressWarnings("serial")
public class JpaServiceException extends RuntimeException{

	/**
	 * Instantiates a new jpa service exception.
	 *
	 * @param message the message
	 */
	public JpaServiceException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new jpa service exception.
	 *
	 * @param e the e
	 */
	public JpaServiceException(Throwable e) {
		super(e);
	}
	
}
