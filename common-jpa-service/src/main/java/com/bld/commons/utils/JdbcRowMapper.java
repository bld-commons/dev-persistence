package com.bld.commons.utils;

import java.sql.ResultSet;
import java.util.List;

@FunctionalInterface
public interface JdbcRowMapper<K> {

	public void rowMapper(List<K>list,ResultSet row,int i);
}
