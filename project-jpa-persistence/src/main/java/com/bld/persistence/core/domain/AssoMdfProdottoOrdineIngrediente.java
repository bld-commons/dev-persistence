package com.bld.persistence.core.domain;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;



/**
 * The persistent class for the asso_mdf_prodotto_ordine_ingrediente database table.
 * 
 */
@Entity
@Table(name="asso_mdf_prodotto_ordine_ingrediente")
@NamedQuery(name="AssoMdfProdottoOrdineIngrediente.findAll", query="SELECT a FROM AssoMdfProdottoOrdineIngrediente a")
public class AssoMdfProdottoOrdineIngrediente implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AssoMdfProdottoOrdineIngredientePK id;

	//bi-directional many-to-one association to Ingrediente
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_ingrediente", nullable=false, insertable=false, updatable=false)
	private Ingrediente ingrediente;

	//bi-directional many-to-one association to ModificaProdottoOrdine
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_modifica_prodotto_ordine", nullable=false, insertable=false, updatable=false)
	private ModificaProdottoOrdine modificaProdottoOrdine;

	//bi-directional many-to-one association to TipoModifica
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tipo_modifica", nullable=false)
	private TipoModifica tipoModifica;

	public AssoMdfProdottoOrdineIngrediente() {
	}

	public AssoMdfProdottoOrdineIngredientePK getId() {
		return this.id;
	}

	public void setId(AssoMdfProdottoOrdineIngredientePK id) {
		this.id = id;
	}

	public Ingrediente getIngrediente() {
		return this.ingrediente;
	}

	public void setIngrediente(Ingrediente ingrediente) {
		this.ingrediente = ingrediente;
	}

	public ModificaProdottoOrdine getModificaProdottoOrdine() {
		return this.modificaProdottoOrdine;
	}

	public void setModificaProdottoOrdine(ModificaProdottoOrdine modificaProdottoOrdine) {
		this.modificaProdottoOrdine = modificaProdottoOrdine;
	}

	public TipoModifica getTipoModifica() {
		return this.tipoModifica;
	}

	public void setTipoModifica(TipoModifica tipoModifica) {
		this.tipoModifica = tipoModifica;
	}

}