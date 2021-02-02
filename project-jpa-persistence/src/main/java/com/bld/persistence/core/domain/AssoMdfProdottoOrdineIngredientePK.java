package com.bld.persistence.core.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the asso_mdf_prodotto_ordine_ingrediente database table.
 * 
 */
@Embeddable
public class AssoMdfProdottoOrdineIngredientePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="id_modifica_prodotto_ordine", insertable=false, updatable=false, unique=true, nullable=false)
	private Long idModificaProdottoOrdine;

	@Column(name="id_ingrediente", insertable=false, updatable=false, unique=true, nullable=false)
	private Long idIngrediente;

	public AssoMdfProdottoOrdineIngredientePK() {
	}
	public Long getIdModificaProdottoOrdine() {
		return this.idModificaProdottoOrdine;
	}
	public void setIdModificaProdottoOrdine(Long idModificaProdottoOrdine) {
		this.idModificaProdottoOrdine = idModificaProdottoOrdine;
	}
	public Long getIdIngrediente() {
		return this.idIngrediente;
	}
	public void setIdIngrediente(Long idIngrediente) {
		this.idIngrediente = idIngrediente;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AssoMdfProdottoOrdineIngredientePK)) {
			return false;
		}
		AssoMdfProdottoOrdineIngredientePK castOther = (AssoMdfProdottoOrdineIngredientePK)other;
		return 
			this.idModificaProdottoOrdine.equals(castOther.idModificaProdottoOrdine)
			&& this.idIngrediente.equals(castOther.idIngrediente);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.idModificaProdottoOrdine.hashCode();
		hash = hash * prime + this.idIngrediente.hashCode();
		
		return hash;
	}
}