package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.TipoToponimo;
import bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import com.bld.persistence.core.repository.TipoToponimoRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import bld.commons.processor.annotations.QueryBuilder;

@Service
@Transactional
@QueryBuilder
public  class TipoToponimoServiceImpl extends JpaServiceImpl<TipoToponimo,Long> implements TipoToponimoService{
	@Autowired
    private TipoToponimoRepository tipoToponimoRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
	@Override
    protected  JpaRepository<TipoToponimo,Long> getJpaRepository() {
        return this.tipoToponimoRepository;
    }
    
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}