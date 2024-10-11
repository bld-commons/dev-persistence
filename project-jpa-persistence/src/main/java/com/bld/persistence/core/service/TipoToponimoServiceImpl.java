package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.TipoToponimo;
import com.bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import com.bld.persistence.core.repository.TipoToponimoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@QueryBuilder
public  class TipoToponimoServiceImpl extends JpaServiceImpl<TipoToponimo,Long> implements TipoToponimoService{
	@Autowired
    private TipoToponimoRepository tipoToponimoRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<TipoToponimo,Long> getJpaRepository() {
        return this.tipoToponimoRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}