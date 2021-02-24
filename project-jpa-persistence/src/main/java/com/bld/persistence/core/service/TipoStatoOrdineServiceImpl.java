package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.TipoStatoOrdine;
import com.bld.persistence.core.domain.TipoStatoOrdineRepository;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class TipoStatoOrdineServiceImpl extends JpaServiceImpl<TipoStatoOrdine,Long> implements TipoStatoOrdineService{


	

	@Autowired
    private TipoStatoOrdineRepository tipoStatoOrdineRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From TipoStatoOrdine tipoStatoOrdine "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct tipoStatoOrdine"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(tipoStatoOrdine)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }
	@Override
    protected  void mapOneToMany() {
        addJoinOneToMany("idOrdine", "  join fetch tipoStatoOrdine.ordines ordines ");
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
    }
	@Override
    protected  JpaRepository<TipoStatoOrdine,Long> getJpaRepository() {
        return tipoStatoOrdineRepository;
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("desTipoStatoOrdine", " and tipoStatoOrdine.desTipoStatoOrdine like :desTipoStatoOrdine ");
        map.put("idOrdine", " and ordines.idOrdine in (:ordines) ");
        map.put("idTipoStatoOrdine", " and tipoStatoOrdine.idTipoStatoOrdine in (:idTipoStatoOrdine) ");
        map.put("id", " and tipoStatoOrdine.idTipoStatoOrdine in (:idTipoStatoOrdine) ");
        return map;
    }

}