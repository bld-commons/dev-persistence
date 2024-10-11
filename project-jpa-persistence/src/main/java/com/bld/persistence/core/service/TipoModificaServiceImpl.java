package com.bld.persistence.core.service;

import com.bld.persistence.core.repository.TipoModificaRepository;
import com.bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import com.bld.persistence.core.domain.TipoModifica;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

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