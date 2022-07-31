package bld.commons.reflection.model;

import java.util.Map;

public class BuildJpqlQueryParameter<T, ID> extends BuildQueryParameter<T, ID,QueryParameter<T, ID>>{

	
	public BuildJpqlQueryParameter(Map<String, String> mapConditions, QueryParameter<T, ID> queryParameter, String sql) {
		super(mapConditions, queryParameter, sql);
	}

	
	
}
