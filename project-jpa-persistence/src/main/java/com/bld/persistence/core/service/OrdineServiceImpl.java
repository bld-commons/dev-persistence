package com.bld.persistence.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bld.persistence.core.domain.Ordine;
import com.bld.persistence.core.repository.OrdineRepository;

import bld.commons.processor.annotations.QueryBuilder;
import bld.commons.service.JpaServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
@QueryBuilder
public  class OrdineServiceImpl extends JpaServiceImpl<Ordine,Long> implements OrdineService{
	@Autowired
    private OrdineRepository ordineRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<Ordine,Long> getJpaRepository() {
        return this.ordineRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}