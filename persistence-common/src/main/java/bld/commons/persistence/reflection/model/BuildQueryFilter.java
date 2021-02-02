package bld.commons.persistence.reflection.model;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class BuildQueryFilter<T, ID> {

	private Map<String, String> mapConditions;

	private Map<String, LinkedHashSet<String>> mapOneToMany;

	private QueryFilter<T, ID> queryFilter;
	
	private String sql;

	public BuildQueryFilter(Map<String, String> mapConditions, Map<String, LinkedHashSet<String>> mapOneToMany,QueryFilter<T, ID> queryFilter, String sql) {
		super();
		this.mapConditions = mapConditions;
		this.mapOneToMany = mapOneToMany;
		this.queryFilter = queryFilter;
		this.sql = sql;
	}

	public BuildQueryFilter() {
		super();
		this.mapConditions=new HashMap<>();
		this.mapOneToMany=new HashMap<>();		
	}

	public Map<String, String> getMapConditions() {
		return mapConditions;
	}

	public void setMapConditions(Map<String, String> mapConditions) {
		this.mapConditions = mapConditions;
	}

	public Map<String, LinkedHashSet<String>> getMapOneToMany() {
		return mapOneToMany;
	}

	public void setMapOneToMany(Map<String, LinkedHashSet<String>> mapOneToMany) {
		this.mapOneToMany = mapOneToMany;
	}

	public QueryFilter<T, ID> getQueryFilter() {
		return queryFilter;
	}

	public void setQueryFilter(QueryFilter<T, ID> queryFilter) {
		this.queryFilter = queryFilter;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	

	
	
	
}
