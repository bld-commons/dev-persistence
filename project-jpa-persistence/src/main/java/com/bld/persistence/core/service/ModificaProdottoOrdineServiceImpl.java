package com.bld.persistence.core.service;

import bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import com.bld.persistence.core.repository.ModificaProdottoOrdineRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.persistence.core.domain.ModificaProdottoOrdine;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import bld.commons.processor.annotations.QueryBuilder;

@Service
@Transactional
@QueryBuilder
public  class ModificaProdottoOrdineServiceImpl extends JpaServiceImpl<ModificaProdottoOrdine,Long> implements ModificaProdottoOrdineService{
	@Autowired
    private ModificaProdottoOrdineRepository modificaProdottoOrdineRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }
    
	@Override
    protected  JpaRepository<ModificaProdottoOrdine,Long> getJpaRepository() {
        return this.modificaProdottoOrdineRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}