package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.TipoUtente;
import com.bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import com.bld.persistence.core.repository.TipoUtenteRepository;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@QueryBuilder
public  class TipoUtenteServiceImpl extends JpaServiceImpl<TipoUtente,Long> implements TipoUtenteService{
	@Autowired
    private TipoUtenteRepository tipoUtenteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<TipoUtente,Long> getJpaRepository() {
        return this.tipoUtenteRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}