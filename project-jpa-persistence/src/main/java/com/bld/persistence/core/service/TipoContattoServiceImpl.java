package com.bld.persistence.core.service;

import com.bld.persistence.core.repository.TipoContattoRepository;
import com.bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import com.bld.persistence.core.domain.TipoContatto;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@QueryBuilder
public  class TipoContattoServiceImpl extends JpaServiceImpl<TipoContatto,Long> implements TipoContattoService{
	@Autowired
    private TipoContattoRepository tipoContattoRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<TipoContatto,Long> getJpaRepository() {
        return this.tipoContattoRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}