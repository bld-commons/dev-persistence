package com.bld.proxy.api.find.exception;

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
