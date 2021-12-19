package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.TipoRuolo;
import bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import com.bld.persistence.core.repository.TipoRuoloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import bld.commons.processor.annotations.QueryBuilder;

@Service
@Transactional
@QueryBuilder
public  class TipoRuoloServiceImpl extends JpaServiceImpl<TipoRuolo,Long> implements TipoRuoloService{
	@Autowired
    private TipoRuoloRepository tipoRuoloRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }
    
	@Override
    protected  JpaRepository<TipoRuolo,Long> getJpaRepository() {
        return this.tipoRuoloRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}