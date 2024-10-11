package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.Prodotto;
import com.bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import com.bld.persistence.core.repository.ProdottoRepository;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@QueryBuilder
public  class ProdottoServiceImpl extends JpaServiceImpl<Prodotto,Long> implements ProdottoService{
	@Autowired
    private ProdottoRepository prodottoRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<Prodotto,Long> getJpaRepository() {
        return this.prodottoRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}