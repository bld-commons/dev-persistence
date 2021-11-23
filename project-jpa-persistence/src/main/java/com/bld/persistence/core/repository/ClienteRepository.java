package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.Cliente;
import bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface ClienteRepository extends BaseJpaRepository<Cliente,Long>{

}