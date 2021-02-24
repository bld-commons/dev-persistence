package com.bld.persistence.core.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.bld.persistence.core.domain.ProdottoOrdine;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import com.bld.persistence.core.domain.ProdottoOrdineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class ProdottoOrdineServiceImpl extends JpaServiceImpl<ProdottoOrdine,Long> implements ProdottoOrdineService{


	

	@Autowired
    private ProdottoOrdineRepository prodottoOrdineRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From ProdottoOrdine prodottoOrdine join fetch prodottoOrdine.prodotto prodotto join fetch prodottoOrdine.ordine ordine "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct prodottoOrdine"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(prodottoOrdine)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("idProdottoOrdine", " and prodottoOrdine.idProdottoOrdine in (:idProdottoOrdine) ");
        map.put("id", " and prodottoOrdine.idProdottoOrdine in (:idProdottoOrdine) ");
        map.put("createUser", " and prodottoOrdine.createUser like :createUser ");
        map.put("idModificaProdottoOrdine", " and modificaProdottoOrdines.idModificaProdottoOrdine in (:modificaProdottoOrdines) ");
        map.put("quantita", " and prodottoOrdine.quantita in (:quantita) ");
        map.put("riduzionePrezzo", " and prodottoOrdine.riduzionePrezzo in (:riduzionePrezzo) ");
        map.put("idProdotto", " and prodotto.idProdotto in (:idProdotto) ");
        map.put("flagValido", " and prodottoOrdine.flagValido= :flagValido ");
        map.put("updateUser", " and prodottoOrdine.updateUser like :updateUser ");
        map.put("updateTimestampBeforeEqual", " and prodottoOrdine.updateTimestamp<=:updateTimestampBeforeEqual ");
        map.put("updateTimestampAfterEqual", " and prodottoOrdine.updateTimestamp>=:updateTimestampAfterEqual ");
        map.put("updateTimestampBefore", " and prodottoOrdine.updateTimestamp<:updateTimestampBefore ");
        map.put("updateTimestampAfter", " and prodottoOrdine.updateTimestamp>:updateTimestampAfter ");
        map.put("updateTimestamp", " and prodottoOrdine.updateTimestamp=:updateTimestamp ");
        map.put("createTimestampBeforeEqual", " and prodottoOrdine.createTimestamp<=:createTimestampBeforeEqual ");
        map.put("createTimestampAfterEqual", " and prodottoOrdine.createTimestamp>=:createTimestampAfterEqual ");
        map.put("createTimestampBefore", " and prodottoOrdine.createTimestamp<:createTimestampBefore ");
        map.put("createTimestampAfter", " and prodottoOrdine.createTimestamp>:createTimestampAfter ");
        map.put("createTimestamp", " and prodottoOrdine.createTimestamp=:createTimestamp ");
        map.put("idOrdine", " and ordine.idOrdine in (:idOrdine) ");
        return map;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
    }
	@Override
    protected  JpaRepository<ProdottoOrdine,Long> getJpaRepository() {
        return prodottoOrdineRepository;
    }
	@Override
    protected  void mapOneToMany() {
        addJoinOneToMany("idModificaProdottoOrdine", "  join fetch prodottoOrdine.modificaProdottoOrdines modificaProdottoOrdines ");
    }

}