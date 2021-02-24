package com.bld.persistence.core.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.MenuOrdineProdottoRepository;
import com.bld.persistence.core.domain.MenuOrdineProdottoPK;
import org.springframework.stereotype.Service;
import com.bld.persistence.core.domain.MenuOrdineProdotto;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class MenuOrdineProdottoServiceImpl extends JpaServiceImpl<MenuOrdineProdotto,MenuOrdineProdottoPK> implements MenuOrdineProdottoService{


	

	@Autowired
    private MenuOrdineProdottoRepository menuOrdineProdottoRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From MenuOrdineProdotto menuOrdineProdotto join fetch menuOrdineProdotto.ordine ordine join fetch menuOrdineProdotto.prodotto1 prodotto1 join fetch menuOrdineProdotto.prodotto2 prodotto2 where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct menuOrdineProdotto"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(menuOrdineProdotto)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  JpaRepository<MenuOrdineProdotto,MenuOrdineProdottoPK> getJpaRepository() {
        return menuOrdineProdottoRepository;
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
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
    }
	@Override
    protected  void mapOneToMany() {
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("idOrdine", " and ordine.idOrdine in (:idOrdine) ");
        map.put("idOrdine", " and menuOrdineProdotto.id.idOrdine in (:idOrdine) ");
        map.put("idProdotto", " and menuOrdineProdotto.id.idProdotto in (:idProdotto) ");
        map.put("idMenu", " and menuOrdineProdotto.id.idMenu in (:idMenu) ");
        map.put("quantita", " and menuOrdineProdotto.quantita in (:quantita) ");
        map.put("idProdotto", " and prodotto1.idProdotto in (:idProdotto) ");
        map.put("idProdotto", " and prodotto2.idProdotto in (:idProdotto) ");
        return map;
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }

}