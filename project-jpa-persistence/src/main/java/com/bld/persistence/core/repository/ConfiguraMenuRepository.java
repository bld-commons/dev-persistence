package com.bld.persistence.core.repository;

import bld.commons.repository.BaseJpaRepository;
import com.bld.persistence.core.domain.ConfiguraMenu;
import org.springframework.stereotype.Repository;

@Repository
public  interface ConfiguraMenuRepository extends BaseJpaRepository<ConfiguraMenu,Long>{

}