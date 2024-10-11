package com.bld.persistence.core.service;

import com.bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.persistence.core.repository.IngredienteRepository;
import com.bld.commons.processor.annotations.QueryBuilder;
import com.bld.persistence.core.domain.Ingrediente;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@QueryBuilder
public  class IngredienteServiceImpl extends JpaServiceImpl<Ingrediente,Long> implements IngredienteService{
	@Autowired
    private IngredienteRepository ingredienteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<Ingrediente,Long> getJpaRepository() {
        return this.ingredienteRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}