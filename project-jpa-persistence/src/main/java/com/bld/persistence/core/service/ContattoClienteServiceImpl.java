package com.bld.persistence.core.service;

import bld.commons.service.JpaServiceImpl;
import com.bld.persistence.core.repository.ContattoClienteRepository;
import org.springframework.stereotype.Service;
import com.bld.persistence.core.domain.ContattoCliente;
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
public  class ContattoClienteServiceImpl extends JpaServiceImpl<ContattoCliente,String> implements ContattoClienteService{
	@Autowired
    private ContattoClienteRepository contattoClienteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }
    
	@Override
    protected  JpaRepository<ContattoCliente,String> getJpaRepository() {
        return this.contattoClienteRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}