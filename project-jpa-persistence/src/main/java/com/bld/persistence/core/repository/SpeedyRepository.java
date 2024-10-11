package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.Speedy;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface SpeedyRepository extends BaseJpaRepository<Speedy,Long>{

}