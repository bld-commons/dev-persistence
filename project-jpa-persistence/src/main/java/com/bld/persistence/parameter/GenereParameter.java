package com.bld.persistence.parameter;

import java.util.Date;
import java.util.List;

import com.bld.commons.reflection.annotations.DateFilter;
import com.bld.commons.reflection.annotations.LikeString;
import com.bld.commons.reflection.model.BaseParameter;
import com.bld.commons.utils.json.annotations.DateTimeZone;
import com.bld.commons.utils.types.UpperLowerType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
public class GenereParameter extends BaseParameter {

	@LikeString(upperLowerType = UpperLowerType.UPPER)
	private String desGenere;

	@LikeString(upperLowerType = UpperLowerType.UPPER)
	private String desPosizioneCucina;

	private List<Long> idGenere;

	private List<Long> idPostazioneCucina;

	private List<Long> idRistorante;
	
	@DateTimeZone(format = "yyyy-MM-dd")
	@DateFilter(addDay = 1)
	private Date createTimestampTo;

}
