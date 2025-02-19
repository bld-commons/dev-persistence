/*
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.model.ConditionsZoneModel.java 
 */
package com.bld.commons.reflection.model;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.bld.commons.exception.JpaServiceException;
import com.bld.commons.reflection.annotations.ConditionsZone;

/**
 * The Class ConditionsZoneModel.
 */
public class ConditionsZoneModel {

	/** The key. */
	private String key;

	/** The where. */
	private String where;

	/** The parameters. */
	private Map<String, Object> parameters;

	/** The nullables. */
	private Set<String> nullables;

	private Map<String, TupleParameter> tupleParamenters;

	/**
	 * Inits the.
	 */
	private void init() {
		this.where = "";
		this.parameters = new HashMap<>();
		this.nullables = new HashSet<>();
		this.tupleParamenters = new HashMap<>();
	}

	/**
	 * Instantiates a new conditions zone model.
	 *
	 * @param key the key
	 */
	public ConditionsZoneModel(String key) {
		super();
		this.key = key;
		this.init();
	}

	/**
	 * Instantiates a new conditions zone model.
	 *
	 * @param conditionsZone the conditions zone
	 */
	public ConditionsZoneModel(ConditionsZone conditionsZone) {
		super();
		this.key = conditionsZone.key();
		this.init();
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Gets the where.
	 *
	 * @return the where
	 */
	public String getWhere() {
		return where;
	}

	/**
	 * Sets the where.
	 *
	 * @param conditionsZone the new where
	 */
	public void setWhere(ConditionsZone conditionsZone) {
		if (conditionsZone != null)
			this.where = StringUtils.isNotBlank(this.where) || conditionsZone.initWhere() ? " where 1=1\n" : "";
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	public Map<String, TupleParameter> getTupleParamenters() {
		return tupleParamenters;
	}

	/**
	 * Adds the parameter.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	public void addParameter(String key, Object value) {
		if (StringUtils.isNotBlank(key))
			this.parameters.put(key, value);
	}

	public void addParameter(String key, TupleParameter tupleParameter) {
		if (key != null && tupleParameter != null)
			this.tupleParamenters.put(key, tupleParameter);
	}

	public void mergeParameters() {
		if(MapUtils.isNotEmpty(tupleParamenters)) {
			for(Entry<String, TupleParameter> entry:this.tupleParamenters.entrySet()) {
				TupleParameter tupleParameter=entry.getValue();
				int i=0;
				for(Object object:tupleParameter.getObjects()) {
					for(String param:tupleParameter.getParams()) {
						try {
							Object value=PropertyUtils.getProperty(object, param);
							this.addParameter(param+i, value);
						} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
							throw new JpaServiceException("The tuple with the name: \""+param+"\" is not found");
						}
					}			
					i++;
				}
			}
		}
	}

	/**
	 * Gets the nullables.
	 *
	 * @return the nullables
	 */
	public Set<String> getNullables() {
		return nullables;
	}

	/**
	 * Adds the nullables.
	 *
	 * @param nullable the nullable
	 */
	public void addNullables(String nullable) {
		if (StringUtils.isNotBlank(nullable))
			this.nullables.add(nullable);
	}

}
