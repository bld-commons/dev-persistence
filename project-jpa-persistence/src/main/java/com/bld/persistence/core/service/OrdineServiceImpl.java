package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.Ordine;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bld.persistence.core.domain.OrdineRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class OrdineServiceImpl extends JpaServiceImpl<Ordine,Long> implements OrdineService{


	

	@Autowired
    private OrdineRepository ordineRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From Ordine ordine join fetch ordine.cliente cliente join fetch ordine.tipoStatoOrdine tipoStatoOrdine left join fetch ordine.calendarioSpeedy calendarioSpeedy join fetch ordine.ristorante ristorante "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct ordine"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(ordine)"+FROM_BY_FILTER;
    
	
    
    
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("idOrdine", " and ordine.idOrdine in (:idOrdine) ");
        map.put("id", " and ordine.idOrdine in (:idOrdine) ");
        map.put("idCliente", " and cliente.idCliente in (:idCliente) ");
        map.put("codiceOrdine", " and ordine.codiceOrdine like :codiceOrdine ");
        map.put("flagValido", " and ordine.flagValido= :flagValido ");
        map.put("idTipoStatoOrdine", " and tipoStatoOrdine.idTipoStatoOrdine in (:idTipoStatoOrdine) ");
        map.put("createUser", " and ordine.createUser like :createUser ");
        map.put("idCalendarioSpeedy", " and calendarioSpeedy.idCalendarioSpeedy in (:idCalendarioSpeedy) ");
        map.put("idRistorante", " and ristorante.idRistorante in (:idRistorante) ");
        map.put("sconto", " and ordine.sconto in (:sconto) ");
        map.put("updateTimestampBeforeEqual", " and ordine.updateTimestamp>= :updateTimestampBeforeEqual ");
        map.put("updateTimestampAfterEqual", " and ordine.updateTimestamp<= :updateTimestampAfterEqual ");
        map.put("updateTimestampBefore", " and ordine.updateTimestamp> :updateTimestampBefore ");
        map.put("updateTimestampAfter", " and ordine.updateTimestamp< :updateTimestampAfter ");
        map.put("updateTimestamp", " and ordine.updateTimestamp= :updateTimestamp ");
        map.put("idProdottoOrdine", " and prodottoOrdines.idProdottoOrdine in (:prodottoOrdines) ");
        map.put("createTimestampBeforeEqual", " and ordine.createTimestamp>= :createTimestampBeforeEqual ");
        map.put("createTimestampAfterEqual", " and ordine.createTimestamp<= :createTimestampAfterEqual ");
        map.put("createTimestampBefore", " and ordine.createTimestamp> :createTimestampBefore ");
        map.put("createTimestampAfter", " and ordine.createTimestamp< :createTimestampAfter ");
        map.put("createTimestamp", " and ordine.createTimestamp= :createTimestamp ");
        map.put("updateUser", " and ordine.updateUser like :updateUser ");
        return map;
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
	@Override
    protected  void mapOneToMany() {
        addJoinOneToMany("idProdottoOrdine", "  join fetch ordine.prodottoOrdines prodottoOrdines ");
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
	@Override
    protected  JpaRepository<Ordine,Long> getJpaRepository() {
        return ordineRepository;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
    }

}