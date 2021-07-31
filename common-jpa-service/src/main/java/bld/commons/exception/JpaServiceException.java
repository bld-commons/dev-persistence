package bld.commons.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

@SuppressWarnings("serial")
public class JpaServiceException extends JsonProcessingException{

	public JpaServiceException(String message) {
		super(message);
	}

	public JpaServiceException(Throwable e) {
		super(e);
	}
	
}
