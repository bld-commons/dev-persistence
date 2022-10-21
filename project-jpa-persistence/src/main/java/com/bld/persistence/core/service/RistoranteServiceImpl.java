package com.bld.persistence.core.service;

import bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.Ristorante;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bld.persistence.core.repository.RistoranteRepository;
import org.springframework.transaction.annotation.Transactional;
import bld.commons.processor.annotations.QueryBuilder;

@Service
@Transactional
@QueryBuilder
public  class RistoranteServiceImpl extends JpaServiceImpl<Ristorante,Long> implements RistoranteService{
	@Autowired
    private RistoranteRepository ristoranteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<Ristorante,Long> getJpaRepository() {
        return this.ristoranteRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}