package com.bld.persistence.core.service;

import com.bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import com.bld.persistence.core.repository.ModificaProdottoOrdineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import com.bld.persistence.core.domain.ModificaProdottoOrdine;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

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