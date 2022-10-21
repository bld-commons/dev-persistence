package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.TipoStatoOrdine;
import com.bld.persistence.core.repository.TipoStatoOrdineRepository;
import bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import bld.commons.processor.annotations.QueryBuilder;

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