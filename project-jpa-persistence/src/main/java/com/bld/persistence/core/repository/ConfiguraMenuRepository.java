package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.ConfiguraMenu;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface ConfiguraMenuRepository extends BaseJpaRepository<ConfiguraMenu,Long>{

}