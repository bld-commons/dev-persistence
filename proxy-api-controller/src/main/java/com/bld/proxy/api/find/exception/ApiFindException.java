package com.bld.proxy.api.find.exception;

/**
 * Unchecked exception thrown by the {@code proxy-api-controller} framework
 * when an error occurs during dynamic proxy invocation or query execution.
 *
 * <p>Typical scenarios that produce this exception:</p>
 * <ul>
 *   <li>No compatible mapper method found on the class specified in
 *       {@link com.bld.proxy.api.find.annotations.ApiMapper}</li>
 *   <li>More than one compatible mapper method found and the method name
 *       was not explicitly declared</li>
 *   <li>A required {@link com.bld.proxy.api.find.annotations.ApiFind} annotation
 *       is missing from the controller interface or method</li>
 * </ul>
 *
 * @author Francesco Baldi
 * @see com.bld.proxy.api.find.intecerptor.ApiFindInterceptor
 */
@SuppressWarnings("serial")
public class ApiFindException extends RuntimeException {

	public ApiFindException() {
		super();
	}

	public ApiFindException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ApiFindException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApiFindException(String message) {
		super(message);
	}

	public ApiFindException(Throwable cause) {
		super(cause);
	}

}
