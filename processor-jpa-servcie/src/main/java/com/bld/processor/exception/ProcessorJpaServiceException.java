package com.bld.processor.exception;

public class ProcessorJpaServiceException extends RuntimeException {

	private static final long serialVersionUID = 3032489226672226960L;

	public ProcessorJpaServiceException() {
		super();
	}

	public ProcessorJpaServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ProcessorJpaServiceException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ProcessorJpaServiceException(String message) {
		super(message);
	}

	public ProcessorJpaServiceException(Throwable cause) {
		super(cause);
	}

	
	
	
}
