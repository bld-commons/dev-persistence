package com.bld.persistence.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bld.persistence.core.domain.ProdottoOrdine;
import com.bld.persistence.core.repository.ProdottoOrdineRepository;

import bld.commons.processor.annotations.QueryBuilder;
import bld.commons.service.JpaServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
@QueryBuilder
public  class ProdottoOrdineServiceImpl extends JpaServiceImpl<ProdottoOrdine,Long> implements ProdottoOrdineService{
	@Autowired
    private ProdottoOrdineRepository prodottoOrdineRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<ProdottoOrdine,Long> getJpaRepository() {
        return this.prodottoOrdineRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}