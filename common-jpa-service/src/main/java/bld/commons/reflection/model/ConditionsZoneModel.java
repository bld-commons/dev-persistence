package bld.commons.reflection.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import bld.commons.reflection.annotations.ConditionsZone;

public class ConditionsZoneModel {

	private String key;

	private String where;

	private Map<String, Object> parameters;
	
	private Set<String> nullables;
	
	

	private void init() {
		this.where = "";
		this.parameters = new HashMap<>();
		this.nullables=new HashSet<>();
	}

	public ConditionsZoneModel(String key) {
		super();
		this.key = key;
		this.init();
	}
	
	public ConditionsZoneModel(ConditionsZone conditionsZone) {
		super();
		this.key = conditionsZone.key();
		this.init();
	}

	public String getKey() {
		return key;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(ConditionsZone conditionsZone) {
		if (conditionsZone != null)
			this.where = StringUtils.isNotBlank(this.where) || conditionsZone.initWhere() ? " where 1=1\n" : "";
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void addParameter(String key, Object value) {
		if (StringUtils.isNotBlank(key) && value != null)
			this.parameters.put(key, value);
	}

	public Set<String> getNullables() {
		return nullables;
	}
	
	public void addNullables(String nullable) {
		if (StringUtils.isNotBlank(nullable))
			this.nullables.add(nullable);
	}

	

}
