package com.bld.persistence.core.service;

import com.bld.commons.service.JpaServiceImpl;
import com.bld.persistence.core.domain.MenuOrdineProdottoPK;
import org.springframework.stereotype.Service;
import com.bld.persistence.core.domain.MenuOrdineProdotto;
import jakarta.persistence.EntityManager;
import com.bld.persistence.core.repository.MenuOrdineProdottoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

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