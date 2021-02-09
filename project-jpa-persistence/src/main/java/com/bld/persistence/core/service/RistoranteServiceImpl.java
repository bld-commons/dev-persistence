package com.bld.persistence.core.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.RistoranteRepository;
import com.bld.persistence.core.domain.Ristorante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class RistoranteServiceImpl extends JpaServiceImpl<Ristorante,Long> implements RistoranteService{


	

	@Autowired
    private RistoranteRepository ristoranteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From Ristorante ristorante join fetch ristorante.tipoToponimo tipoToponimo "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct ristorante"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(ristorante)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
    }
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }
	@Override
    protected  void mapOneToMany() {
        addJoinOneToMany("idOrdine", "  join fetch ristorante.ordines ordines ");
        addJoinOneToMany("idPostazioneCucina", "  join fetch ristorante.postazioneCucinas postazioneCucinas ");
        addJoinOneToMany("idSpeedy", "  join fetch ristorante.speedies speedies ");
        addJoinOneToMany("idIngrediente", "  join fetch ristorante.ingredientes ingredientes ");
    }
	@Override
    protected  JpaRepository<Ristorante,Long> getJpaRepository() {
        return ristoranteRepository;
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("idOrdine", " and ordines.idOrdine in (:ordines) ");
        map.put("idTipoToponimo", " and tipoToponimo.idTipoToponimo in (:idTipoToponimo) ");
        map.put("idRistorante", " and ristorante.idRistorante in (:idRistorante) ");
        map.put("id", " and ristorante.idRistorante in (:idRistorante) ");
        map.put("cap", " and ristorante.cap like :cap ");
        map.put("idPostazioneCucina", " and postazioneCucinas.idPostazioneCucina in (:postazioneCucinas) ");
        map.put("indirizzo", " and ristorante.indirizzo like :indirizzo ");
        map.put("idSpeedy", " and speedies.idSpeedy in (:speedies) ");
        map.put("nome", " and ristorante.nome like :nome ");
        map.put("numCivico", " and ristorante.numCivico like :numCivico ");
        map.put("idIngrediente", " and ingredientes.idIngrediente in (:ingredientes) ");
        return map;
    }

}