package com.bld.persistence.parameter;

import java.util.List;

import com.bld.commons.reflection.annotations.ConditionsZone;
import com.bld.commons.reflection.annotations.ConditionsZones;
import com.bld.commons.reflection.annotations.TupleComparison;
import com.bld.commons.reflection.model.BaseParameter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
public class NativeGenereParameter extends BaseParameter {

	@ConditionsZones({ @ConditionsZone(key = "zone1"),@ConditionsZone(key = "zone2") })
	@TupleComparison({ "idGenere","idPostazioneCucina" })
	private List<GenereTuple> genereTuple;
	
	
}
