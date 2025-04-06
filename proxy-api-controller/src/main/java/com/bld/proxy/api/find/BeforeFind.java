package com.bld.proxy.api.find;

import com.bld.commons.reflection.model.BaseQueryParameter;

public interface BeforeFind<E,ID> {

	public void before(BaseQueryParameter<E,ID> parameters,  Object... args);
	
}
