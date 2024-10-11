package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.TipoStatoOrdine;
import com.bld.persistence.core.repository.TipoStatoOrdineRepository;
import com.bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@QueryBuilder
public  class TipoStatoOrdineServiceImpl extends JpaServiceImpl<TipoStatoOrdine,Long> implements TipoStatoOrdineService{
	@Autowired
    private TipoStatoOrdineRepository tipoStatoOrdineRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<TipoStatoOrdine,Long> getJpaRepository() {
        return this.tipoStatoOrdineRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}