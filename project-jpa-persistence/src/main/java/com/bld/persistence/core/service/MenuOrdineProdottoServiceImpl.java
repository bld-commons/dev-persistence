package com.bld.persistence.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bld.persistence.core.domain.MenuOrdineProdotto;
import com.bld.persistence.core.domain.MenuOrdineProdottoPK;
import com.bld.persistence.core.repository.MenuOrdineProdottoRepository;

import bld.commons.processor.annotations.QueryBuilder;
import bld.commons.service.JpaServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
@QueryBuilder
public  class MenuOrdineProdottoServiceImpl extends JpaServiceImpl<MenuOrdineProdotto,MenuOrdineProdottoPK> implements MenuOrdineProdottoService{
	@Autowired
    private MenuOrdineProdottoRepository menuOrdineProdottoRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<MenuOrdineProdotto,MenuOrdineProdottoPK> getJpaRepository() {
        return this.menuOrdineProdottoRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}