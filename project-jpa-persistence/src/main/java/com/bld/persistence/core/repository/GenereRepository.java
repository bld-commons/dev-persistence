package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.Genere;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface GenereRepository extends BaseJpaRepository<Genere,Long>{

}