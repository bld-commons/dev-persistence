package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.TipoRuolo;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bld.persistence.core.domain.TipoRuoloRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class TipoRuoloServiceImpl extends JpaServiceImpl<TipoRuolo,Long> implements TipoRuoloService{


	

	@Autowired
    private TipoRuoloRepository tipoRuoloRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From TipoRuolo tipoRuolo "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct tipoRuolo"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(tipoRuolo)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  JpaRepository<TipoRuolo,Long> getJpaRepository() {
        return tipoRuoloRepository;
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
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }
	@Override
    protected  void mapOneToMany() {
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("desTipoRuolo", " and tipoRuolo.desTipoRuolo like :desTipoRuolo ");
        map.put("selezionabile", " and tipoRuolo.selezionabile= :selezionabile ");
        map.put("idTipoRuolo", " and tipoRuolo.idTipoRuolo in (:idTipoRuolo) ");
        map.put("id", " and tipoRuolo.idTipoRuolo in (:idTipoRuolo) ");
        return map;
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }

}