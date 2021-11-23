package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.Ordine;
import bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface OrdineRepository extends BaseJpaRepository<Ordine,Long>{

}