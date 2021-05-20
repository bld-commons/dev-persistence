package com.bld.persistence.common;

public interface EntityMapper<E,R> {

	
	public R entityToResponse(E entity);
}
