package com.bld.commons.utils;

import java.util.List;

import jakarta.persistence.Tuple;

@FunctionalInterface
public interface JpaRowMapper<K> {

	public void rowMapper(List<K> result,Tuple row,int i);
}
