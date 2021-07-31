package bld.commons.reflection.model;

import javax.validation.Valid;

public class ObjectResponse<T> {

	@Valid
	private T data;
	
	public ObjectResponse() {
		super();
	}
	
	public ObjectResponse(T data) {
		super();
		this.data = data;
	}




	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
	
	
}
