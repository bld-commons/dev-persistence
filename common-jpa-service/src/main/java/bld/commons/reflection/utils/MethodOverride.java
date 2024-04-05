package bld.commons.reflection.utils;

import java.util.Arrays;

public class MethodOverride {

	private String name;
	
	private Class<?>[] parameterTypes;

	public MethodOverride(String name, Class<?>[] parameterTypes) {
		super();
		this.name = name;
		this.parameterTypes = parameterTypes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

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
