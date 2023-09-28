package com.bld.proxy.api.find.data;

import java.lang.reflect.Parameter;

public class ParameterDetails {

	private Parameter parameter;
	
	private Object value;
	
	private Integer index;
	
	

	public ParameterDetails(Parameter parameter, Object value, Integer index) {
		super();
		this.parameter = parameter;
		this.value = value;
		this.index = index;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public Object getValue() {
		return value;
	}

	public Integer getIndex() {
		return index;
	}

	
}
