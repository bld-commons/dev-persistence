package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.AssoMdfProdottoOrdineIngredientePK;
import bld.commons.repository.BaseJpaRepository;
import com.bld.persistence.core.domain.AssoMdfProdottoOrdineIngrediente;
import org.springframework.stereotype.Repository;

@Repository
public  interface AssoMdfProdottoOrdineIngredienteRepository extends BaseJpaRepository<AssoMdfProdottoOrdineIngrediente,AssoMdfProdottoOrdineIngredientePK>{

}