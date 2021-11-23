package com.bld.persistence.core.repository;

import bld.commons.repository.BaseJpaRepository;
import com.bld.persistence.core.domain.TipoModifica;
import org.springframework.stereotype.Repository;

@Repository
public  interface TipoModificaRepository extends BaseJpaRepository<TipoModifica,Long>{

}