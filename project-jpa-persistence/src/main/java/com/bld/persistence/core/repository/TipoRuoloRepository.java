package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.TipoRuolo;
import bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface TipoRuoloRepository extends BaseJpaRepository<TipoRuolo,Long>{

}