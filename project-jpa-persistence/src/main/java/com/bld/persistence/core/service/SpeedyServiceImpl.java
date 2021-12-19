package com.bld.persistence.core.service;

import com.bld.persistence.core.repository.SpeedyRepository;
import bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.persistence.core.domain.Speedy;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import bld.commons.processor.annotations.QueryBuilder;

@Service
@Transactional
@QueryBuilder
public  class SpeedyServiceImpl extends JpaServiceImpl<Speedy,Long> implements SpeedyService{
	@Autowired
    private SpeedyRepository speedyRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
	@Override
    protected  JpaRepository<Speedy,Long> getJpaRepository() {
        return this.speedyRepository;
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