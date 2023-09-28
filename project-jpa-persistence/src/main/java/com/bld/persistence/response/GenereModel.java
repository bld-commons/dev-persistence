package com.bld.persistence.response;

import com.bld.commons.utils.data.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
public class GenereModel extends BaseModel<Long>{

	
	private String desGenere;

	private String desPostazioneCucina;
	
	private String ristorante;
	
	
	
}
