package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.Genere;
import bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface GenereRepository extends BaseJpaRepository<Genere,Long>{

}