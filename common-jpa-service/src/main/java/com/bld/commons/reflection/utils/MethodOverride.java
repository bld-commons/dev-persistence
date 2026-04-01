package com.bld.commons.reflection.utils;

import java.util.Arrays;

/**
 * Value object that uniquely identifies a method by its name and parameter types.
 *
 * <p>Used by the reflection utilities to track methods that have already been
 * processed or that should override a parent-class definition, avoiding duplicate
 * handling when walking the class hierarchy.</p>
 *
 * <p>Equality and hashing are based on both the method name and its parameter
 * type array, making this class safe for use in {@link java.util.Set} and as a
 * {@link java.util.Map} key.</p>
 *
 * @author Francesco Baldi
 */
public class MethodOverride {

	private String name;

	private Class<?>[] parameterTypes;

	/**
	 * Constructs a new {@code MethodOverride} descriptor.
	 *
	 * @param name           the method name; must not be {@code null}
	 * @param parameterTypes the parameter types of the method; may be empty
	 */
	public MethodOverride(String name, Class<?>[] parameterTypes) {
		super();
		this.name = name;
		this.parameterTypes = parameterTypes;
	}

	/**
	 * Returns the method name.
	 *
	 * @return the method name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the method name.
	 *
	 * @param name the method name; must not be {@code null}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the parameter types of the method.
	 *
	 * @return the parameter type array; may be empty but never {@code null}
	 */
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	/**
	 * Sets the parameter types of the method.
	 *
	 * @param parameterTypes the parameter type array; may be empty
	 */
	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Arrays.hashCode(parameterTypes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof MethodOverride)) {
			return false;
		}
		MethodOverride other = (MethodOverride) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (!Arrays.equals(parameterTypes, other.parameterTypes)) {
			return false;
		}
		return true;
	}

	


	
	
}
