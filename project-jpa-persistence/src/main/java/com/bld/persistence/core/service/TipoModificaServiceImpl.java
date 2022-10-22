package com.bld.persistence.core.service;

import com.bld.persistence.core.repository.TipoModificaRepository;
import bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.TipoModifica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import bld.commons.processor.annotations.QueryBuilder;

@Service
@Transactional
@QueryBuilder
public  class TipoModificaServiceImpl extends JpaServiceImpl<TipoModifica,Long> implements TipoModificaService{
	@Autowired
    private TipoModificaRepository tipoModificaRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<TipoModifica,Long> getJpaRepository() {
        return this.tipoModificaRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}