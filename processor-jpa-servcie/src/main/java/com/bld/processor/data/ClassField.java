package com.bld.processor.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;

public class ClassField {

	private String className;

	private Set<Element> elements;

	private Map<String, Element> mapElement;

	public ClassField(String className) {
		super();
		this.className=className;
		this.elements = new HashSet<>();
		this.mapElement=new HashMap<>();
	}

	public String getClassName() {
		return className;
	}

	public Set<Element> getElements() {
		return elements;
	}

	public Map<String, Element> getMapElement() {
		return mapElement;
	}

}
