package com.bld.persistence.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bld.persistence.core.domain.AssoMdfProdottoOrdineIngrediente;
import com.bld.persistence.core.domain.AssoMdfProdottoOrdineIngredientePK;
import com.bld.persistence.core.repository.AssoMdfProdottoOrdineIngredienteRepository;

import bld.commons.processor.annotations.QueryBuilder;
import bld.commons.service.JpaServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
@QueryBuilder
public  class AssoMdfProdottoOrdineIngredienteServiceImpl extends JpaServiceImpl<AssoMdfProdottoOrdineIngrediente,AssoMdfProdottoOrdineIngredientePK> implements AssoMdfProdottoOrdineIngredienteService{
	@Autowired
    private AssoMdfProdottoOrdineIngredienteRepository assoMdfProdottoOrdineIngredienteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<AssoMdfProdottoOrdineIngrediente,AssoMdfProdottoOrdineIngredientePK> getJpaRepository() {
        return this.assoMdfProdottoOrdineIngredienteRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}