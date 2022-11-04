package com.bld.persistence.core.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bld.persistence.core.domain.Cliente;
import com.bld.persistence.core.repository.ClienteRepository;

import bld.commons.processor.annotations.JpqlOrderBuilder;
import bld.commons.processor.annotations.NativeOrderBuilder;
import bld.commons.processor.annotations.OrderAlias;
import bld.commons.processor.annotations.QueryBuilder;
import bld.commons.service.JpaServiceImpl;

@Service
@Transactional
@QueryBuilder(jpaOrder = {
		@JpqlOrderBuilder(key = "toponimo", order = "cliente.tipoToponimo.desTipoToponimo"),
		@JpqlOrderBuilder(key="ind",order="concat(${toponimo},' ',${indirizzo},' ',${numCivico})", alias = {
				@OrderAlias(alias = "toponimo", field = "cliente.tipoToponimo.desTipoToponimo"),
				@OrderAlias(alias = "indirizzo", field = "cliente.indirizzo"),
				@OrderAlias(alias = "numCivico", field = "cliente.numCivico"),
		})
},nativeOrder = {@NativeOrderBuilder(key = "desTopnimo", order = "tt.des_toponimo")})
public  class ClienteServiceImpl extends JpaServiceImpl<Cliente,Long> implements ClienteService{
	@Autowired
    private ClienteRepository clienteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<Cliente,Long> getJpaRepository() {
        return this.clienteRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}