/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.model.NativeQueryParameter.java
 */
package bld.commons.reflection.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import bld.commons.reflection.annotations.ConditionsZone;
import bld.commons.reflection.annotations.ConditionsZones;
import bld.commons.service.BaseJpaService;

/**
 * The Class NativeQueryParameter.
 *
 * @param <T>  the generic type
 * @param <ID> the generic type
 */
@SuppressWarnings("serial")
public class NativeQueryParameter<T, ID> extends BaseQueryParameter<T, ID> {

	/** The result class. */
	private Class<T> resultClass;

	private Map<String,StringBuilder> emptyZones;
	
	private Map<String,ConditionsZoneModel> mapConditionsZone;

	/**
	 * Instantiates a new native query parameter.
	 *
	 * @param resultClass the result class
	 */
	public NativeQueryParameter(Class<T> resultClass) {
		super();
		this.init();
		this.resultClass = resultClass;
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
	 * @param resultClass   the result class
	 * @param mapParameters the map parameters
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

	protected void init() {
		super.init();
		this.mapConditionsZone=new HashMap<>();
		this.emptyZones=new HashMap<>();
	}

	/**
	 * Adds the parameter.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	private void addParameters(String key, Object value, ConditionsZone zone) {
		if (key != null && value != null) {
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
	
	public void addEmptyZones(ConditionsZones conditionsZones) {
		for(ConditionsZone zone:conditionsZones.value())
			this.emptyZones.put(zone.key(),new StringBuilder(""));
			
	}

	public void addParameter(String key, Object value, ConditionsZones conditionsZones) {
		if(conditionsZones==null)
			this.addParameter(key, value);
		else 
			for(ConditionsZone conditionsZone:conditionsZones.value())
				this.addParameters(key, value, conditionsZone);
		
		
	}


	public void addParameter(String key, Object value) {
		this.addParameters(key, value, null);
	}

	public void addNullable(String nullable,  ConditionsZones zones ) {
		if(zones==null)
			this.addNullable(nullable);
		else 
			for(ConditionsZone conditionsZone:zones.value())
				this.addNullables(nullable, conditionsZone);
	}


	public void addNullable(String nullable) {
		this.addNullables(nullable, null);
	}

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

	public Map<String, ConditionsZoneModel> getMapConditionsZone() {
		return mapConditionsZone;
	}

	public Map<String,StringBuilder> getEmptyZones() {
		emptyZones.remove(BaseJpaService.JOIN_ZONE);
		for(String key:this.mapConditionsZone.keySet())
			emptyZones.remove(key);
		return emptyZones;
	}

}
