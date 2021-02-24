package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.TipoToponimo;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import com.bld.persistence.core.domain.TipoToponimoRepository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class TipoToponimoServiceImpl extends JpaServiceImpl<TipoToponimo,Long> implements TipoToponimoService{


	

	@Autowired
    private TipoToponimoRepository tipoToponimoRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From TipoToponimo tipoToponimo "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct tipoToponimo"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(tipoToponimo)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }
	@Override
    protected  void mapOneToMany() {
        addJoinOneToMany("idRistorante", "  join fetch tipoToponimo.ristorantes ristorantes ");
        addJoinOneToMany("idCliente", "  join fetch tipoToponimo.clientes clientes ");
    }
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
    }
	@Override
    protected  JpaRepository<TipoToponimo,Long> getJpaRepository() {
        return tipoToponimoRepository;
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("desTipoToponimo", " and tipoToponimo.desTipoToponimo like :desTipoToponimo ");
        map.put("idTipoToponimo", " and tipoToponimo.idTipoToponimo in (:idTipoToponimo) ");
        map.put("id", " and tipoToponimo.idTipoToponimo in (:idTipoToponimo) ");
        map.put("codTipoToponimo", " and tipoToponimo.codTipoToponimo like :codTipoToponimo ");
        map.put("idRistorante", " and ristorantes.idRistorante in (:ristorantes) ");
        map.put("idCliente", " and clientes.idCliente in (:clientes) ");
        return map;
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }

}