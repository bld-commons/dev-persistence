package com.bld.persistence.common;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.bld.commons.controller.mapper.ModelMapper;
import com.bld.persistence.core.domain.Genere;
import com.bld.persistence.response.GenereModel;

import jakarta.persistence.Tuple;

@Mapper(componentModel = "spring")
public abstract class GenereMapper implements ModelMapper<Genere, GenereModel>{

	@Mappings({
		@Mapping(target = "id",source = "idGenere"),
		@Mapping(target = "desPostazioneCucina",source = "postazioneCucina.desPostazioneCucina"),
		@Mapping(target = "ristorante",source = "postazioneCucina.ristorante.nome"),
		
	})
	public abstract GenereModel convertToModel(Genere entity);

	
	public void rowMapper(List<GenereModel> result,Tuple row,int i) {
		GenereModel model=new GenereModel();
		model.setId(row.get("id_genere",Long.class));
		model.setDesGenere(row.get("des_genere", String.class));
		model.setDesPostazioneCucina(row.get("des_postazione_cucina", String.class));
		model.setRistorante(row.get("nome", String.class));
		result.add(model);
	}
}
