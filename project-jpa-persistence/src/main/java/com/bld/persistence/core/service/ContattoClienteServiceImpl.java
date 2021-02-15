package com.bld.persistence.core.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.bld.persistence.core.domain.ContattoCliente;
import com.bld.persistence.core.domain.ContattoClienteRepository;
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
public  class ContattoClienteServiceImpl extends JpaServiceImpl<ContattoCliente,String> implements ContattoClienteService{


	

	@Autowired
    private ContattoClienteRepository contattoClienteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From ContattoCliente contattoCliente join fetch contattoCliente.cliente cliente join fetch contattoCliente.tipoContatto tipoContatto where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct contattoCliente"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(contattoCliente)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
	@Override
    protected  JpaRepository<ContattoCliente,String> getJpaRepository() {
        return contattoClienteRepository;
    }
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("contatto", " and contattoCliente.contatto in (:contatto) ");
        map.put("id", " and contattoCliente.contatto in (:contatto) ");
        map.put("idCliente", " and cliente.idCliente in (:idCliente) ");
        map.put("idTipoContatto", " and tipoContatto.idTipoContatto in (:idTipoContatto) ");
        return map;
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
    }
	@Override
    protected  void mapOneToMany() {
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }

}