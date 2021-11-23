package com.bld.persistence.core.service;

import com.bld.persistence.core.repository.ConsegnaCapRepository;
import com.bld.persistence.core.domain.ConsegnaCap;
import bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.persistence.core.domain.ConsegnaCapPK;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import bld.commons.processor.annotations.QueryBuilder;

@Service
@Transactional
@QueryBuilder
public  class ConsegnaCapServiceImpl extends JpaServiceImpl<ConsegnaCap,ConsegnaCapPK> implements ConsegnaCapService{
	@Autowired
    private ConsegnaCapRepository consegnaCapRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }
    
	@Override
    protected  JpaRepository<ConsegnaCap,ConsegnaCapPK> getJpaRepository() {
        return this.consegnaCapRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}