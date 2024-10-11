package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.Cliente;
import com.bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import jakarta.persistence.PersistenceContext;
import com.bld.persistence.core.repository.ClienteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@QueryBuilder
public  class ClienteServiceImpl extends JpaServiceImpl<Cliente,Long> implements ClienteService{
	@Autowired
    private ClienteRepository clienteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<Cliente,Long> getJpaRepository() {
        return this.clienteRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}