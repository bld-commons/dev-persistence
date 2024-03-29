package com.bld.persistence.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bld.persistence.core.domain.AssoUtenteTipoRuolo;
import com.bld.persistence.core.domain.AssoUtenteTipoRuoloPK;
import com.bld.persistence.core.repository.AssoUtenteTipoRuoloRepository;

import bld.commons.processor.annotations.QueryBuilder;
import bld.commons.service.JpaServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
@QueryBuilder
public  class AssoUtenteTipoRuoloServiceImpl extends JpaServiceImpl<AssoUtenteTipoRuolo,AssoUtenteTipoRuoloPK> implements AssoUtenteTipoRuoloService{
	@Autowired
    private AssoUtenteTipoRuoloRepository assoUtenteTipoRuoloRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<AssoUtenteTipoRuolo,AssoUtenteTipoRuoloPK> getJpaRepository() {
        return this.assoUtenteTipoRuoloRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}