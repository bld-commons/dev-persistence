package com.bld.persistence.core.service;

import com.bld.commons.service.JpaServiceImpl;
import com.bld.persistence.core.repository.ContattoClienteRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import com.bld.persistence.core.domain.ContattoCliente;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

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