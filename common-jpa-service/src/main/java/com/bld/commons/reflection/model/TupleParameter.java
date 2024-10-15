package com.bld.commons.reflection.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.bld.commons.exception.JpaServiceException;

import io.jsonwebtoken.lang.Arrays;

public class TupleParameter {

	private Set<Object> objects;
	
	private String[] params;

	public TupleParameter(String[] params) {
		super();
		if(params==null || params.length<2)
			throw new JpaServiceException("The params items cannot be less than 2");
		this.params = params;
	}

	public Set<Object> getObjects() {
		return objects;
	}

	public String[] getParams() {
		return params;
	}

	public void setObjects(Object... objects) {
		if(ArrayUtils.isNotEmpty(objects))
		this.objects = new HashSet<>(Arrays.asList(objects));
	}
	
	
	public void setObjects(Collection<Object> objects) {
		if(CollectionUtils.isNotEmpty(objects))
			this.objects=new HashSet<>(objects);
	
	}
	
	
	public String getCondition(int i) {
		String condition="";
		for(String param:params)
			condition+=",:"+param+i;
		return "("+condition.substring(1)+")";

	}
	
}
