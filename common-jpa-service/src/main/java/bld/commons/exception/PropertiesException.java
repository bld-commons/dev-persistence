/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.exception.PropertiesException.java
 */
package bld.commons.exception;

/**
 * The Class PropertiesException.
 */
public class PropertiesException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new properties exception.
	 */
	public PropertiesException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new properties exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression the enable suppression
	 * @param writableStackTrace the writable stack trace
	 */
	public PropertiesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new properties exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public PropertiesException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new properties exception.
	 *
	 * @param message the message
	 */
	public PropertiesException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new properties exception.
	 *
	 * @param cause the cause
	 */
	public PropertiesException(Throwable cause) {
		super(cause);
	}

	
	
	
}
