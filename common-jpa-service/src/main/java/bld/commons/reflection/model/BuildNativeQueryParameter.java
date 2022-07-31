package bld.commons.reflection.model;

import java.util.Map;

public class BuildNativeQueryParameter<T, ID> extends BuildQueryParameter<T, ID,NativeQueryParameter<T, ID>>{

	public BuildNativeQueryParameter(Map<String, String> mapConditions, NativeQueryParameter<T, ID> queryParameter, String sql) {
		super(mapConditions, queryParameter, sql);
	}

	
	
}
