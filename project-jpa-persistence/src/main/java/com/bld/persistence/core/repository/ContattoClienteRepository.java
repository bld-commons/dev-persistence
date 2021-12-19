package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.ContattoCliente;
import bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface ContattoClienteRepository extends BaseJpaRepository<ContattoCliente,String>{

}