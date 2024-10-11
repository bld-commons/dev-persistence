package com.bld.persistence.core.service;

import com.bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import com.bld.persistence.core.repository.AssoMdfProdottoOrdineIngredienteRepository;
import jakarta.persistence.EntityManager;
import com.bld.persistence.core.domain.AssoMdfProdottoOrdineIngredientePK;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import com.bld.persistence.core.domain.AssoMdfProdottoOrdineIngrediente;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

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