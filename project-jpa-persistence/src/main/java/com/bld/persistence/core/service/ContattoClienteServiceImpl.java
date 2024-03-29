package com.bld.persistence.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bld.persistence.core.domain.ContattoCliente;
import com.bld.persistence.core.repository.ContattoClienteRepository;

import bld.commons.processor.annotations.QueryBuilder;
import bld.commons.service.JpaServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
@QueryBuilder
public  class ContattoClienteServiceImpl extends JpaServiceImpl<ContattoCliente,String> implements ContattoClienteService{
	@Autowired
    private ContattoClienteRepository contattoClienteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<ContattoCliente,String> getJpaRepository() {
        return this.contattoClienteRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}