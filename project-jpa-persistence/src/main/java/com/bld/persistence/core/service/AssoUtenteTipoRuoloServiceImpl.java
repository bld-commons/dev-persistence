package com.bld.persistence.core.service;

import bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import com.bld.persistence.core.domain.AssoUtenteTipoRuolo;
import com.bld.persistence.core.repository.AssoUtenteTipoRuoloRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.persistence.core.domain.AssoUtenteTipoRuoloPK;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import bld.commons.processor.annotations.QueryBuilder;

@Service
@Transactional
@QueryBuilder
public  class AssoUtenteTipoRuoloServiceImpl extends JpaServiceImpl<AssoUtenteTipoRuolo,AssoUtenteTipoRuoloPK> implements AssoUtenteTipoRuoloService{
	@Autowired
    private AssoUtenteTipoRuoloRepository assoUtenteTipoRuoloRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }
    
	@Override
    protected  JpaRepository<AssoUtenteTipoRuolo,AssoUtenteTipoRuoloPK> getJpaRepository() {
        return this.assoUtenteTipoRuoloRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}