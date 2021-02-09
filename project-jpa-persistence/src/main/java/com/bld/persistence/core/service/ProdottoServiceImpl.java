package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.Prodotto;
import com.bld.persistence.core.domain.ProdottoRepository;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class ProdottoServiceImpl extends JpaServiceImpl<Prodotto,Long> implements ProdottoService{


	

	@Autowired
    private ProdottoRepository prodottoRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From Prodotto prodotto join fetch prodotto.genere genere "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct prodotto"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(prodotto)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  JpaRepository<Prodotto,Long> getJpaRepository() {
        return prodottoRepository;
    }
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
    }
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
	@Override
    protected  void mapOneToMany() {
        addJoinOneToMany("idConfiguraMenu", "  join fetch prodotto.configuraMenus1 configuraMenus1 ");
        addJoinOneToMany("idProdottoOrdine", "  join fetch prodotto.prodottoOrdines prodottoOrdines ");
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("desProdotto", " and prodotto.desProdotto like :desProdotto ");
        map.put("prezzo", " and prodotto.prezzo in (:prezzo) ");
        map.put("idConfiguraMenu", " and configuraMenus1.idConfiguraMenu in (:configuraMenus1) ");
        map.put("createUser", " and prodotto.createUser like :createUser ");
        map.put("flagValido", " and prodotto.flagValido= :flagValido ");
        map.put("updateUser", " and prodotto.updateUser like :updateUser ");
        map.put("idGenere", " and genere.idGenere in (:idGenere) ");
        map.put("idProdotto", " and prodotto.idProdotto in (:idProdotto) ");
        map.put("id", " and prodotto.idProdotto in (:idProdotto) ");
        map.put("createTimestampBefore", " and prodotto.createTimestamp>= :createTimestampBefore ");
        map.put("createTimestampAfter", " and prodotto.createTimestamp<= :createTimestampAfter ");
        map.put("updateTimestampBefore", " and prodotto.updateTimestamp>= :updateTimestampBefore ");
        map.put("updateTimestampAfter", " and prodotto.updateTimestamp<= :updateTimestampAfter ");
        map.put("idProdottoOrdine", " and prodottoOrdines.idProdottoOrdine in (:prodottoOrdines) ");
        return map;
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }

}