package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.TipoContattoRepository;
import org.springframework.stereotype.Service;
import com.bld.persistence.core.domain.TipoContatto;
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
public  class TipoContattoServiceImpl extends JpaServiceImpl<TipoContatto,Long> implements TipoContattoService{


	

	@Autowired
    private TipoContattoRepository tipoContattoRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From TipoContatto tipoContatto "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct tipoContatto"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(tipoContatto)"+FROM_BY_FILTER;
    
	
    
    
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("contatto", " and contattoClientes.contatto in (:contattoClientes) ");
        map.put("idTipoContatto", " and tipoContatto.idTipoContatto in (:idTipoContatto) ");
        map.put("id", " and tipoContatto.idTipoContatto in (:idTipoContatto) ");
        map.put("desTipoContatto", " and tipoContatto.desTipoContatto like :desTipoContatto ");
        return map;
    }
	@Override
    protected  JpaRepository<TipoContatto,Long> getJpaRepository() {
        return tipoContattoRepository;
    }
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
	@Override
    protected  void mapOneToMany() {
        addJoinOneToMany("contatto", "  join fetch tipoContatto.contattoClientes contattoClientes ");
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
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