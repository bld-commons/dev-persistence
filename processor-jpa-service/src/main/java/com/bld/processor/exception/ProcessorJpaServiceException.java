/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class com.bld.processor.exception.ProcessorJpaServiceException.java
 */
package com.bld.processor.exception;

/**
 * The Class ProcessorJpaServiceException.
 */
public class ProcessorJpaServiceException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3032489226672226960L;

	/**
	 * Instantiates a new processor jpa service exception.
	 */
	public ProcessorJpaServiceException() {
		super();
	}

	/**
	 * Instantiates a new processor jpa service exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public ProcessorJpaServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * Instantiates a new processor jpa service exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public ProcessorJpaServiceException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new processor jpa service exception.
	 *
	 * @param message the message
	 */
	public ProcessorJpaServiceException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new processor jpa service exception.
	 *
	 * @param cause the cause
	 */
	public ProcessorJpaServiceException(Throwable cause) {
		super(cause);
	}

	
	
	
}
