/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.model.NativeQueryParameter.java
 */
package com.bld.commons.reflection.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bld.commons.reflection.annotations.ConditionsZone;
import com.bld.commons.reflection.annotations.ConditionsZones;
import com.bld.commons.service.BaseJpaService;

/**
 * The Class NativeQueryParameter.
 *
 * @param <T>  the generic type
 * @param <ID> the generic type
 */
@SuppressWarnings("serial")
public class NativeQueryParameter<T, ID> extends BaseQueryParameter<T, ID> {

	private static final String DEFAULT = "default";

	/** The result class. */
	private Class<T> resultClass;

	/** The empty zones. */
	private Map<String,StringBuilder> emptyZones;
	
	/** The map conditions zone. */
	private Map<String,ConditionsZoneModel> mapConditionsZone;
	
	private String key;
	
	private NamedParameterJdbcTemplate jdbcTemplate;

	/**
	 * Instantiates a new native query parameter.
	 *
	 * @param resultClass the result class
	 */
	public NativeQueryParameter(Class<T> resultClass) {
		super();
		this.init();
		this.resultClass = resultClass;
		this.key=DEFAULT;
	}

	/**
	 * Instantiates a new native query parameter.
	 *
	 * @param resultClass     the result class
	 * @param filterParameter the filter parameter
	 */
	public NativeQueryParameter(Class<T> resultClass, BaseParameter filterParameter) {
		super(filterParameter);
		this.init();
		this.resultClass = resultClass;
	}

	/**
	 * Instantiates a new native query parameter.
	 *
	 * @param resultClass the result class
	 * @param id          the id
	 */
	public NativeQueryParameter(Class<T> resultClass, ID id) {
		super(id);
		this.init();
		this.resultClass = resultClass;
	}


	/**
	 * Instantiates a new native query parameter.
	 *
	 * @param resultClass the result class
	 * @param mapConditionsZone the map conditions zone
	 */
	public NativeQueryParameter(Class<T> resultClass, Map<String,ConditionsZoneModel> mapConditionsZone) {
		super();
		this.init();
		this.resultClass = resultClass;
		this.mapConditionsZone = mapConditionsZone;
	}

	/**
	 * Gets the result class.
	 *
	 * @return the result class
	 */
	public Class<T> getResultClass() {
		return resultClass;
	}

	/**
	 * Inits the.
	 */
	protected void init() {
		super.init();
		this.mapConditionsZone=new HashMap<>();
		this.emptyZones=new HashMap<>();
		this.key=DEFAULT;
	}

	/**
	 * Adds the parameter.
	 *
	 * @param key   the key
	 * @param value the value
	 * @param zone the zone
	 */
	private void addParameters(String key, Object value, ConditionsZone zone) {
		if (key != null ) {
			if(zone!=null) {
				if (!this.mapConditionsZone.containsKey(zone.key()))
					this.mapConditionsZone.put(zone.key(), new ConditionsZoneModel(zone));
				this.mapConditionsZone.get(zone.key()).setWhere(zone);
				this.mapConditionsZone.get(zone.key()).addParameter(key, value);
			}else {
				if (!this.mapConditionsZone.containsKey(BaseJpaService.JOIN_ZONE))
					this.mapConditionsZone.put(BaseJpaService.JOIN_ZONE, new ConditionsZoneModel(BaseJpaService.JOIN_ZONE));
				this.mapConditionsZone.get(BaseJpaService.JOIN_ZONE).addParameter(key, value);
			}
			
			
		}
	}
	
	
	private void addParameters(String key, TupleParameter value, ConditionsZone zone) {
		if (key != null ) {
			if(zone!=null) {
				if (!this.mapConditionsZone.containsKey(zone.key()))
					this.mapConditionsZone.put(zone.key(), new ConditionsZoneModel(zone));
				this.mapConditionsZone.get(zone.key()).setWhere(zone);
				this.mapConditionsZone.get(zone.key()).addParameter(key, value);
			}else {
				if (!this.mapConditionsZone.containsKey(BaseJpaService.JOIN_ZONE))
					this.mapConditionsZone.put(BaseJpaService.JOIN_ZONE, new ConditionsZoneModel(BaseJpaService.JOIN_ZONE));
				this.mapConditionsZone.get(BaseJpaService.JOIN_ZONE).addParameter(key, value);
			}
			
			
		}
	}
	
	/**
	 * Adds the empty zones.
	 *
	 * @param conditionsZones the conditions zones
	 */
	public void addEmptyZones(ConditionsZones conditionsZones) {
		for(ConditionsZone zone:conditionsZones.value())
			this.emptyZones.put(zone.key(),new StringBuilder(""));
			
	}

	/**
	 * Adds the parameter.
	 *
	 * @param key the key
	 * @param value the value
	 * @param conditionsZones the conditions zones
	 */
	public void addParameter(String key, Object value, ConditionsZones conditionsZones) {
		if(conditionsZones==null)
			this.addParameter(key, value);
		else 
			for(ConditionsZone conditionsZone:conditionsZones.value())
				this.addParameters(key, value, conditionsZone);
		
		
	}
	
	public void addParameter(String key, TupleParameter value, ConditionsZones conditionsZones) {
		if(conditionsZones==null)
			this.addParameter(key, value);
		else 
			for(ConditionsZone conditionsZone:conditionsZones.value())
				this.addParameters(key, value, conditionsZone);
		
		
	}


	/**
	 * Adds the parameter.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void addParameter(String key, Object value) {
		this.addParameters(key, value, null);
	}

	/**
	 * Adds the nullable.
	 *
	 * @param nullable the nullable
	 * @param zones the zones
	 */
	public void addNullable(String nullable,  ConditionsZones zones ) {
		if(zones==null)
			this.addNullable(nullable);
		else 
			for(ConditionsZone conditionsZone:zones.value())
				this.addNullables(nullable, conditionsZone);
	}


	/**
	 * Adds the nullable.
	 *
	 * @param nullable the nullable
	 */
	public void addNullable(String nullable) {
		this.addNullables(nullable, null);
	}

	/**
	 * Adds the nullables.
	 *
	 * @param nullable the nullable
	 * @param zone the zone
	 */
	public void addNullables(String nullable,  ConditionsZone zone) {
		if (StringUtils.isNotBlank(nullable)) {
			if(zone!=null) {
				if (!this.mapConditionsZone.containsKey(zone.key()))
					this.mapConditionsZone.put(zone.key(), new ConditionsZoneModel(zone));
				this.mapConditionsZone.get(zone.key()).setWhere(zone);
				this.mapConditionsZone.get(zone.key()).addNullables(nullable);
			}else {
				if (!this.mapConditionsZone.containsKey(BaseJpaService.JOIN_ZONE))
					this.mapConditionsZone.put(BaseJpaService.JOIN_ZONE, new ConditionsZoneModel(BaseJpaService.JOIN_ZONE));
				this.mapConditionsZone.get(BaseJpaService.JOIN_ZONE).addNullables(nullable);
			}
			
			
		}
		
	}

	/**
	 * Gets the map conditions zone.
	 *
	 * @return the map conditions zone
	 */
	public Map<String, ConditionsZoneModel> getMapConditionsZone() {
		return mapConditionsZone;
	}

	/**
	 * Gets the empty zones.
	 *
	 * @return the empty zones
	 */
	public Map<String,StringBuilder> getEmptyZones() {
		emptyZones.remove(BaseJpaService.JOIN_ZONE);
		for(String key:this.mapConditionsZone.keySet())
			emptyZones.remove(key);
		return emptyZones;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public NamedParameterJdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


}
