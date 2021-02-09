package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.TipoUtente;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bld.persistence.core.domain.TipoUtenteRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class TipoUtenteServiceImpl extends JpaServiceImpl<TipoUtente,Long> implements TipoUtenteService{


	

	@Autowired
    private TipoUtenteRepository tipoUtenteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From TipoUtente tipoUtente "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct tipoUtente"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(tipoUtente)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  void mapOneToMany() {
        addJoinOneToMany("idUtente", "  join fetch tipoUtente.utentes utentes ");
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
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("idUtente", " and utentes.idUtente in (:utentes) ");
        map.put("idTipoUtente", " and tipoUtente.idTipoUtente in (:idTipoUtente) ");
        map.put("id", " and tipoUtente.idTipoUtente in (:idTipoUtente) ");
        map.put("desTipoUtente", " and tipoUtente.desTipoUtente like :desTipoUtente ");
        return map;
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }
	@Override
    protected  JpaRepository<TipoUtente,Long> getJpaRepository() {
        return tipoUtenteRepository;
    }

}