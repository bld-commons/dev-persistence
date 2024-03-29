package com.bld.persistence.common;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.bld.persistence.core.domain.Genere;
import com.bld.persistence.response.GenereModel;

import bld.commons.controller.mapper.ModelMapper;

@Mapper(componentModel = "spring")
public abstract class Genere2Mapper{

	@Mappings({
		@Mapping(target = "id",source = "idGenere"),
		@Mapping(target = "desPostazioneCucina",source = "postazioneCucina.desPostazioneCucina"),
		@Mapping(target = "ristorante",source = "postazioneCucina.ristorante.nome"),
		
	})
	public abstract GenereModel convertToGenereModel(Genere entity);

	
	 
}
