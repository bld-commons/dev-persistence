package com.bld.persistence.core.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import com.bld.persistence.core.domain.AssoMdfProdottoOrdineIngredientePK;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.bld.persistence.core.domain.AssoMdfProdottoOrdineIngrediente;
import com.bld.persistence.core.domain.AssoMdfProdottoOrdineIngredienteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class AssoMdfProdottoOrdineIngredienteServiceImpl extends JpaServiceImpl<AssoMdfProdottoOrdineIngrediente,AssoMdfProdottoOrdineIngredientePK> implements AssoMdfProdottoOrdineIngredienteService{


	

	@Autowired
    private AssoMdfProdottoOrdineIngredienteRepository assoMdfProdottoOrdineIngredienteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From AssoMdfProdottoOrdineIngrediente assoMdfProdottoOrdineIngrediente join fetch assoMdfProdottoOrdineIngrediente.ingrediente ingrediente join fetch assoMdfProdottoOrdineIngrediente.tipoModifica tipoModifica join fetch assoMdfProdottoOrdineIngrediente.modificaProdottoOrdine modificaProdottoOrdine where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct assoMdfProdottoOrdineIngrediente"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(assoMdfProdottoOrdineIngrediente)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }
	@Override
    protected  void mapOneToMany() {
    }
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("idIngrediente", " and ingrediente.idIngrediente in (:idIngrediente) ");
        map.put("idTipoModifica", " and tipoModifica.idTipoModifica in (:idTipoModifica) ");
        map.put("idModificaProdottoOrdine", " and modificaProdottoOrdine.idModificaProdottoOrdine in (:idModificaProdottoOrdine) ");
        map.put("idModificaProdottoOrdine", " and assoMdfProdottoOrdineIngrediente.id.idModificaProdottoOrdine in (:idModificaProdottoOrdine) ");
        map.put("idIngrediente", " and assoMdfProdottoOrdineIngrediente.id.idIngrediente in (:idIngrediente) ");
        return map;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
    }
	@Override
    protected  JpaRepository<AssoMdfProdottoOrdineIngrediente,AssoMdfProdottoOrdineIngredientePK> getJpaRepository() {
        return assoMdfProdottoOrdineIngredienteRepository;
    }

}