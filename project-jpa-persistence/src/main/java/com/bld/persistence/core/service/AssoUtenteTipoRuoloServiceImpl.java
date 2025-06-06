package com.bld.persistence.core.service;

import com.bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import com.bld.persistence.core.domain.AssoUtenteTipoRuolo;
import com.bld.persistence.core.repository.AssoUtenteTipoRuoloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.persistence.core.domain.AssoUtenteTipoRuoloPK;
import com.bld.commons.processor.annotations.QueryBuilder;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

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