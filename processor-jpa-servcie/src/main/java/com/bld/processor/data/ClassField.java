/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class com.bld.processor.data.ClassField.java
 */
package com.bld.processor.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;

/**
 * The Class ClassField.
 */
public class ClassField {

	/** The class name. */
	private String className;

	/** The elements. */
	private Set<Element> elements;

	/** The map element. */
	private Map<String, Element> mapElement;

	/**
	 * Instantiates a new class field.
	 *
	 * @param className the class name
	 */
	public ClassField(String className) {
		super();
		this.className=className;
		this.elements = new HashSet<>();
		this.mapElement=new HashMap<>();
	}

	/**
	 * Gets the class name.
	 *
	 * @return the class name
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Gets the elements.
	 *
	 * @return the elements
	 */
	public Set<Element> getElements() {
		return elements;
	}

	/**
	 * Gets the map element.
	 *
	 * @return the map element
	 */
	public Map<String, Element> getMapElement() {
		return mapElement;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "ClassField [className=" + className + "]";
	}

	
	
}
