package com.bld.persistence.core.repository;

import bld.commons.repository.BaseJpaRepository;
import com.bld.persistence.core.domain.Speedy;
import org.springframework.stereotype.Repository;

@Repository
public  interface SpeedyRepository extends BaseJpaRepository<Speedy,Long>{

}