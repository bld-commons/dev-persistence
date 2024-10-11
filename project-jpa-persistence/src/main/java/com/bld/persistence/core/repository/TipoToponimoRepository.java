package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.TipoToponimo;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface TipoToponimoRepository extends BaseJpaRepository<TipoToponimo,Long>{

}