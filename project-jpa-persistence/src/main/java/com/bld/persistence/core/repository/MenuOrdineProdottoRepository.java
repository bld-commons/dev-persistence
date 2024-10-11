package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.MenuOrdineProdottoPK;
import com.bld.persistence.core.domain.MenuOrdineProdotto;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface MenuOrdineProdottoRepository extends BaseJpaRepository<MenuOrdineProdotto,MenuOrdineProdottoPK>{

}