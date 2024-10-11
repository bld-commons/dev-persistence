package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.TipoModifica;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface TipoModificaRepository extends BaseJpaRepository<TipoModifica,Long>{

}