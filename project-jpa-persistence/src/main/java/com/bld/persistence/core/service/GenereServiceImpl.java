package com.bld.persistence.core.service;

import bld.commons.service.JpaServiceImpl;
import com.bld.persistence.core.repository.GenereRepository;
import com.bld.persistence.core.domain.Genere;
import org.springframework.stereotype.Service;
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
public  class GenereServiceImpl extends JpaServiceImpl<Genere,Long> implements GenereService{
	@Autowired
    private GenereRepository genereRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
	@Override
    protected  JpaRepository<Genere,Long> getJpaRepository() {
        return this.genereRepository;
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