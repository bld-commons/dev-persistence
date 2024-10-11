package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.TipoContatto;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface TipoContattoRepository extends BaseJpaRepository<TipoContatto,Long>{

}