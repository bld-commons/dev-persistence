package com.bld.persistence.core.service;

import com.bld.persistence.core.repository.StoricoOrdineRepository;
import bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.persistence.core.domain.StoricoOrdine;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.StoricoOrdinePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import bld.commons.processor.annotations.QueryBuilder;

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