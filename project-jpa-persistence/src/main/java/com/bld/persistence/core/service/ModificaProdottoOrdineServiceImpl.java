package com.bld.persistence.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bld.persistence.core.domain.ModificaProdottoOrdine;
import com.bld.persistence.core.repository.ModificaProdottoOrdineRepository;

import bld.commons.processor.annotations.QueryBuilder;
import bld.commons.service.JpaServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
@QueryBuilder
public  class ModificaProdottoOrdineServiceImpl extends JpaServiceImpl<ModificaProdottoOrdine,Long> implements ModificaProdottoOrdineService{
	@Autowired
    private ModificaProdottoOrdineRepository modificaProdottoOrdineRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<ModificaProdottoOrdine,Long> getJpaRepository() {
        return this.modificaProdottoOrdineRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}