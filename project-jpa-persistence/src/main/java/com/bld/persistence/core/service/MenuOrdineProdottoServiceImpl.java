package com.bld.persistence.core.service;

import bld.commons.service.JpaServiceImpl;
import com.bld.persistence.core.domain.MenuOrdineProdottoPK;
import org.springframework.stereotype.Service;
import com.bld.persistence.core.domain.MenuOrdineProdotto;
import com.bld.persistence.core.repository.MenuOrdineProdottoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import bld.commons.processor.annotations.QueryBuilder;

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