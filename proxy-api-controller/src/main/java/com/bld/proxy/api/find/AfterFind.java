package com.bld.proxy.api.find;

public interface AfterFind<T> {
	
	public T after(T result,Object... args);

}
