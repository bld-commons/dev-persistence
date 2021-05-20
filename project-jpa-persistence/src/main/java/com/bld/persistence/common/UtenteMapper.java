package com.bld.persistence.common;

import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import com.bld.persistence.core.domain.Utente;
import com.bld.persistence.response.UtenteResponse;

@Mapper(componentModel = "spring")
public interface UtenteMapper extends EntityMapper<Utente, UtenteResponse> {

	@Override
	@Mappings({})
	public UtenteResponse entityToResponse(Utente entity);

	
	
}
