package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.TipoModificaRepository;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.TipoModifica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class TipoModificaServiceImpl extends JpaServiceImpl<TipoModifica,Long> implements TipoModificaService{


	

	@Autowired
    private TipoModificaRepository tipoModificaRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From TipoModifica tipoModifica "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct tipoModifica"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(tipoModifica)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
    }
	@Override
    protected  JpaRepository<TipoModifica,Long> getJpaRepository() {
        return tipoModificaRepository;
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("desTipoModifica", " and tipoModifica.desTipoModifica like :desTipoModifica ");
        map.put("idTipoModifica", " and tipoModifica.idTipoModifica in (:idTipoModifica) ");
        map.put("id", " and tipoModifica.idTipoModifica in (:idTipoModifica) ");
        return map;
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }
	@Override
    protected  void mapOneToMany() {
    }

}