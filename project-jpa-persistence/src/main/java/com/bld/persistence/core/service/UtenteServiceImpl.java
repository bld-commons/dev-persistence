package com.bld.persistence.core.service;

import com.bld.persistence.core.repository.UtenteRepository;
import bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import bld.commons.processor.annotations.QueryBuilder;

@Service
@Transactional
@QueryBuilder
public  class UtenteServiceImpl extends JpaServiceImpl<Utente,Long> implements UtenteService{
	@Autowired
    private UtenteRepository utenteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<Utente,Long> getJpaRepository() {
        return this.utenteRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}