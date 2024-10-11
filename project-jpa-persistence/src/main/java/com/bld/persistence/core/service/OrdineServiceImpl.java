package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.Ordine;
import com.bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bld.persistence.core.repository.OrdineRepository;
import org.springframework.transaction.annotation.Transactional;

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