package com.bld.persistence.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bld.persistence.core.domain.ConsegnaCap;
import com.bld.persistence.core.domain.ConsegnaCapPK;
import com.bld.persistence.core.repository.ConsegnaCapRepository;

import bld.commons.processor.annotations.QueryBuilder;
import bld.commons.service.JpaServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
@QueryBuilder
public  class ConsegnaCapServiceImpl extends JpaServiceImpl<ConsegnaCap,ConsegnaCapPK> implements ConsegnaCapService{
	@Autowired
    private ConsegnaCapRepository consegnaCapRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<ConsegnaCap,ConsegnaCapPK> getJpaRepository() {
        return this.consegnaCapRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}