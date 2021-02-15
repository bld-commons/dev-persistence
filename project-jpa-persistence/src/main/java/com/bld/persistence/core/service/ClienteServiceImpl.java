package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.Cliente;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.bld.persistence.core.domain.ClienteRepository;
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
public  class ClienteServiceImpl extends JpaServiceImpl<Cliente,Long> implements ClienteService{


	

	@Autowired
    private ClienteRepository clienteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From Cliente cliente join fetch cliente.tipoToponimo tipoToponimo "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct cliente"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(cliente)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
	@Override
    protected  JpaRepository<Cliente,Long> getJpaRepository() {
        return clienteRepository;
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("cap", " and cliente.cap like :cap ");
        map.put("idTipoToponimo", " and tipoToponimo.idTipoToponimo in (:idTipoToponimo) ");
        map.put("idCliente", " and cliente.idCliente in (:idCliente) ");
        map.put("id", " and cliente.idCliente in (:idCliente) ");
        map.put("idUtente", " and utentes.idUtente in (:utentes) ");
        map.put("createUser", " and cliente.createUser like :createUser ");
        map.put("idOrdine", " and ordines.idOrdine in (:ordines) ");
        map.put("contatto", " and contattoClientes.contatto in (:contattoClientes) ");
        map.put("noteAggiuntive", " and cliente.noteAggiuntive like :noteAggiuntive ");
        map.put("numCivico", " and cliente.numCivico like :numCivico ");
        map.put("scala", " and cliente.scala like :scala ");
        map.put("citofono", " and cliente.citofono like :citofono ");
        map.put("updateUser", " and cliente.updateUser like :updateUser ");
        map.put("indirizzo", " and cliente.indirizzo like :indirizzo ");
        map.put("interno", " and cliente.interno like :interno ");
        map.put("createTimestampBefore", " and cliente.createTimestamp>= :createTimestampBefore ");
        map.put("createTimestampAfter", " and cliente.createTimestamp<= :createTimestampAfter ");
        map.put("updateTimestampBefore", " and cliente.updateTimestamp>= :updateTimestampBefore ");
        map.put("updateTimestampAfter", " and cliente.updateTimestamp<= :updateTimestampAfter ");
        return map;
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }
	@Override
    protected  void mapOneToMany() {
        addJoinOneToMany("idUtente", " left join fetch cliente.utentes utentes ");
        addJoinOneToMany("idOrdine", "  join fetch cliente.ordines ordines ");
        addJoinOneToMany("contatto", "  join fetch cliente.contattoClientes contattoClientes ");
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
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }

}