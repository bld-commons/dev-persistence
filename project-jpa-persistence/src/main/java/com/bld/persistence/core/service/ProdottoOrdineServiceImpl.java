package com.bld.persistence.core.service;

import com.bld.commons.service.JpaServiceImpl;
import com.bld.persistence.core.repository.ProdottoOrdineRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import com.bld.persistence.core.domain.ProdottoOrdine;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

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