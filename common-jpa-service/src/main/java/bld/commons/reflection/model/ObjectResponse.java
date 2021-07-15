package bld.commons.reflection.model;

public class ObjectResponse<T> {

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
