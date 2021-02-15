package com.bld.persistence.core.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import com.bld.persistence.core.domain.AssoUtenteTipoRuoloPK;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.AssoUtenteTipoRuoloRepository;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.bld.persistence.core.domain.AssoUtenteTipoRuolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class AssoUtenteTipoRuoloServiceImpl extends JpaServiceImpl<AssoUtenteTipoRuolo,AssoUtenteTipoRuoloPK> implements AssoUtenteTipoRuoloService{


	

	@Autowired
    private AssoUtenteTipoRuoloRepository assoUtenteTipoRuoloRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From AssoUtenteTipoRuolo assoUtenteTipoRuolo join fetch assoUtenteTipoRuolo.tipoRuolo tipoRuolo join fetch assoUtenteTipoRuolo.utente utente left join fetch assoUtenteTipoRuolo.ristorante ristorante where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct assoUtenteTipoRuolo"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(assoUtenteTipoRuolo)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  void mapOneToMany() {
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("idTipoRuolo", " and assoUtenteTipoRuolo.id.idTipoRuolo in (:idTipoRuolo) ");
        map.put("idUtente", " and assoUtenteTipoRuolo.id.idUtente in (:idUtente) ");
        map.put("urlPath", " and assoUtenteTipoRuolo.id.urlPath in (:urlPath) ");
        map.put("idTipoRuolo", " and tipoRuolo.idTipoRuolo in (:idTipoRuolo) ");
        map.put("idUtente", " and utente.idUtente in (:idUtente) ");
        map.put("idRistorante", " and ristorante.idRistorante in (:idRistorante) ");
        return map;
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }
	@Override
    protected  JpaRepository<AssoUtenteTipoRuolo,AssoUtenteTipoRuoloPK> getJpaRepository() {
        return assoUtenteTipoRuoloRepository;
    }
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }

}