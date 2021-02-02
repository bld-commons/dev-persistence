package bld.commons.persistence.reflection.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class InfoClass.
 */
public class InfoClass {

	/** The classe. */
	private Class<?> classe;

	/** The map method. */
	private Map<String, List<Method>> mapMethod;

	/** The list method. */
	private List<Method> listMethod;

	/** The list only set method. */
	private List<Method> listOnlySetMethod;

	/** The map field. */
	private Map<String, Field> mapField;

	/**
	 * Instantiates a new info class.
	 *
	 * @param classe the classe
	 */
	public InfoClass(Class<?> classe) {
		super();
		this.classe = classe;
		this.listMethod = new ArrayList<Method>();
		this.listOnlySetMethod = new ArrayList<Method>();
		this.mapMethod = new HashMap<String, List<Method>>();
		this.mapField = new HashMap<String, Field>();
		Class<?> classApp = classe;
		do {
			setAllInformation(classApp.getMethods(), classApp.getDeclaredFields());
			classApp = classApp.getSuperclass();
		} while (classApp.getSuperclass() != null && !classApp.getName().equals(Object.class.getName()));
	}
	

	
	/**
	 * Sets the all information.
	 *
	 * @param listMethod the list method
	 * @param listField the list field
	 */
	private void setAllInformation(Method[] listMethod, Field[] listField) {
		for (Method method : listMethod) {
			if (!mapMethod.containsKey(method.getName()))
				mapMethod.put(method.getName(), new ArrayList<Method>());
			mapMethod.get(method.getName()).add(method);
			this.listMethod.add(method);
			if (method.getName().startsWith("set"))
				this.listOnlySetMethod.add(method);
		}
		for (Field field : listField)
			if (!mapField.containsKey(field.getName()))
				mapField.put(field.getName(), field);
	}

	/**
	 * Gets the classe.
	 *
	 * @return the classe
	 */
	public Class<?> getClasse() {
		return classe;
	}

	/**
	 * Gets the map method.
	 *
	 * @return the mapMethod
	 */
	public Map<String, List<Method>> getMapMethod() {
		return mapMethod;
	}

	/**
	 * Gets the list method.
	 *
	 * @return the listMethod
	 */
	public List<Method> getListMethod() {
		return listMethod;
	}

	/**
	 * Gets the map field.
	 *
	 * @return the mapField
	 */
	public Map<String, Field> getMapField() {
		return mapField;
	}

	/**
	 * Sets the classe.
	 *
	 * @param classe the classe to set
	 */
	public void setClasse(Class<?> classe) {
		this.classe = classe;
	}

	/**
	 * Sets the map method.
	 *
	 * @param mapMethod the mapMethod to set
	 */
	public void setMapMethod(Map<String, List<Method>> mapMethod) {
		this.mapMethod = mapMethod;
	}

	/**
	 * Sets the list method.
	 *
	 * @param listMethod the listMethod to set
	 */
	public void setListMethod(List<Method> listMethod) {
		this.listMethod = listMethod;
	}

	/**
	 * Sets the map field.
	 *
	 * @param mapField the mapField to set
	 */
	public void setMapField(Map<String, Field> mapField) {
		this.mapField = mapField;
	}

	/**
	 * Gets the list only set method.
	 *
	 * @return the lsitOnlySetMethod
	 */
	public List<Method> getListOnlySetMethod() {
		return listOnlySetMethod;
	}

	/**
	 * Sets the list only set method.
	 *
	 * @param lsitOnlySetMethod the lsitOnlySetMethod to set
	 */
	public void setListOnlySetMethod(List<Method> lsitOnlySetMethod) {
		this.listOnlySetMethod = lsitOnlySetMethod;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classe == null) ? 0 : classe.hashCode());
		result = prime * result + ((listMethod == null) ? 0 : listMethod.hashCode());
		result = prime * result + ((listOnlySetMethod == null) ? 0 : listOnlySetMethod.hashCode());
		result = prime * result + ((mapField == null) ? 0 : mapField.hashCode());
		result = prime * result + ((mapMethod == null) ? 0 : mapMethod.hashCode());
		return result;
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InfoClass other = (InfoClass) obj;
		if (classe == null) {
			if (other.classe != null)
				return false;
		} else if (!classe.equals(other.classe))
			return false;
		if (listMethod == null) {
			if (other.listMethod != null)
				return false;
		} else if (!listMethod.equals(other.listMethod))
			return false;
		if (listOnlySetMethod == null) {
			if (other.listOnlySetMethod != null)
				return false;
		} else if (!listOnlySetMethod.equals(other.listOnlySetMethod))
			return false;
		if (mapField == null) {
			if (other.mapField != null)
				return false;
		} else if (!mapField.equals(other.mapField))
			return false;
		if (mapMethod == null) {
			if (other.mapMethod != null)
				return false;
		} else if (!mapMethod.equals(other.mapMethod))
			return false;
		return true;
	}

}
