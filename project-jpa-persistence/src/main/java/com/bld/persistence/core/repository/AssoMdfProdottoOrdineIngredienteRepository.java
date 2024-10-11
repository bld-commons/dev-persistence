package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.AssoMdfProdottoOrdineIngredientePK;
import com.bld.persistence.core.domain.AssoMdfProdottoOrdineIngrediente;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface AssoMdfProdottoOrdineIngredienteRepository extends BaseJpaRepository<AssoMdfProdottoOrdineIngrediente,AssoMdfProdottoOrdineIngredientePK>{

}