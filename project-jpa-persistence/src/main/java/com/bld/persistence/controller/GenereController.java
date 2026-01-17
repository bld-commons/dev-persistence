package com.bld.persistence.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bld.commons.controller.SearchController;
import com.bld.commons.reflection.model.NativeQueryParameter;
import com.bld.persistence.common.GenereMapper;
import com.bld.persistence.core.domain.Genere;
import com.bld.persistence.core.service.GenereService;
import com.bld.persistence.parameter.GenereParameter;
import com.bld.persistence.parameter.NativeGenereParameter;
import com.bld.persistence.response.GenereModel;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/genere")
@Slf4j
public class GenereController extends SearchController<Genere, Long, GenereModel, GenereParameter>{

	@Autowired
	private GenereService genereService;
	
	@Autowired
	private GenereMapper genereMapper;
	
	private final static String NATIVE_QUERY_TEST="select g.id_genere,\n"
			+ "g.des_genere,pc.des_postazione_cucina,r.nome \n"
			+ "from \n"
			+ "genere g \n"
			+ "join postazione_cucina pc on g.id_postazione_cucina=pc.id_postazione_cucina \n"
			+ "join ristorante r on pc.id_ristorante=r.id_ristorante \n"
			+ "${zone1}\n"
			+ "union\n"
			+ "select \n"
			+ "g.id_genere,g.des_genere,pc.des_postazione_cucina,r.nome \n"
			+ "from \n"
			+ "genere g \n"
			+ "join postazione_cucina pc on g.id_postazione_cucina=pc.id_postazione_cucina \n"
			+ "join ristorante r on pc.id_ristorante=r.id_ristorante \n"
			+ "${zone2}\n";
	
	@PostMapping(path="/find",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
	public List<GenereModel> findNative(@RequestBody NativeGenereParameter parameter){
		NativeQueryParameter< GenereModel, Long> nativeQueryParameter=new NativeQueryParameter<>(GenereModel.class, parameter);
		Date start=new Date();
		List<GenereModel>list=this.genereService.findByFilter(nativeQueryParameter, NATIVE_QUERY_TEST);
		Date end=new Date();
		log.info("Time1: {}ms",(end.getTime()-start.getTime()));
		start=new Date();
		list=this.genereService.findByFilter(nativeQueryParameter, NATIVE_QUERY_TEST,this.genereMapper::rowMapper);
		end=new Date();
		log.info("Time2: {}ms",(end.getTime()-start.getTime()));
		return list;
	}
}
