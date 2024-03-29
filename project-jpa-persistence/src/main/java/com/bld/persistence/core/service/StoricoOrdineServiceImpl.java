package com.bld.persistence.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bld.persistence.core.domain.StoricoOrdine;
import com.bld.persistence.core.domain.StoricoOrdinePK;
import com.bld.persistence.core.repository.StoricoOrdineRepository;

import bld.commons.processor.annotations.QueryBuilder;
import bld.commons.service.JpaServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
@QueryBuilder
public  class StoricoOrdineServiceImpl extends JpaServiceImpl<StoricoOrdine,StoricoOrdinePK> implements StoricoOrdineService{
	@Autowired
    private StoricoOrdineRepository storicoOrdineRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<StoricoOrdine,StoricoOrdinePK> getJpaRepository() {
        return this.storicoOrdineRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}