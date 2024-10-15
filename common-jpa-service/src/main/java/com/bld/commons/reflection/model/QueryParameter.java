/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.model.QueryFilter.java
 */
package com.bld.commons.reflection.model;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.bld.commons.exception.JpaServiceException;

/**
 * The Class QueryFilter.
 *
 * @param <T>  the generic type
 * @param <ID> the generic type
 */
@SuppressWarnings("serial")
public class QueryParameter<T, ID> extends BaseQueryParameter<T, ID> {

	/** The map parameters. */
	private Map<String, Object> parameters;
	
	private Map<String, TupleParameter> tupleParamenters;
	
	/** The nullables. */
	private Set<String> nullables;

	
	
	/**
	 * Instantiates a new query parameter.
	 */
	public QueryParameter() {
		super();
		init();
	}

	/**
	 * Instantiates a new query parameter.
	 *
	 * @param baseParameter the base parameter
	 */
	public QueryParameter(BaseParameter baseParameter) {
		super(baseParameter);
		init();
	}

	/**
	 * Inits the.
	 */
	protected void init() {
		super.init();
		this.nullables = new HashSet<>();
		this.parameters = new HashMap<>();
		this.tupleParamenters=new HashMap<>();
	}

	/**
	 * Instantiates a new query parameter.
	 *
	 * @param id the id
	 */
	public QueryParameter(ID id) {
		super(id);
		init();
		this.parameters.put(ID, id);
	}

	/**
	 * Gets the check nullable.
	 *
	 * @return the check nullable
	 */
	public Set<String> getNullables() {
		return nullables;
	}

	/**
	 * Adds the parameter.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	public void addParameter(String key, Object value) {
		if (key != null && value != null)
			this.parameters.put(key, value);
	}
	
	/**
	 * Adds the nullable.
	 *
	 * @param nullable the nullable
	 */
	public void addNullable(String nullable) {
		if (StringUtils.isNotBlank(nullable))
			this.nullables.add(nullable);
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public Map<String, Object> getParameters() {
		return parameters;
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
	
	public Map<String, TupleParameter> getTupleParamenters() {
		return tupleParamenters;
	}

}
