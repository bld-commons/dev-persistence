package com.bld.persistence.core.domain;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;


/**
 * The persistent class for the tipo_modifica database table.
 * 
 */
@Entity
@Table(name="tipo_modifica")
@NamedQuery(name="TipoModifica.findAll", query="SELECT t FROM TipoModifica t")
public class TipoModifica implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TIPO_MODIFICA_IDTIPOMODIFICA_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TIPO_MODIFICA_IDTIPOMODIFICA_GENERATOR")
	@Column(name="id_tipo_modifica", unique=true, nullable=false)
	private Long idTipoModifica;

	@Column(name="des_tipo_modifica", length=255)
	private String desTipoModifica;

	//bi-directional many-to-one association to AssoMdfProdottoOrdineIngrediente
	@OneToMany(mappedBy="tipoModifica")
	private Set<AssoMdfProdottoOrdineIngrediente> assoMdfProdottoOrdineIngredientes;

	public TipoModifica() {
	}

	public Long getIdTipoModifica() {
		return this.idTipoModifica;
	}

	public void setIdTipoModifica(Long idTipoModifica) {
		this.idTipoModifica = idTipoModifica;
	}

	public String getDesTipoModifica() {
		return this.desTipoModifica;
	}

	public void setDesTipoModifica(String desTipoModifica) {
		this.desTipoModifica = desTipoModifica;
	}

	public Set<AssoMdfProdottoOrdineIngrediente> getAssoMdfProdottoOrdineIngredientes() {
		return this.assoMdfProdottoOrdineIngredientes;
	}

	public void setAssoMdfProdottoOrdineIngredientes(Set<AssoMdfProdottoOrdineIngrediente> assoMdfProdottoOrdineIngredientes) {
		this.assoMdfProdottoOrdineIngredientes = assoMdfProdottoOrdineIngredientes;
	}

	public AssoMdfProdottoOrdineIngrediente addAssoMdfProdottoOrdineIngrediente(AssoMdfProdottoOrdineIngrediente assoMdfProdottoOrdineIngrediente) {
		getAssoMdfProdottoOrdineIngredientes().add(assoMdfProdottoOrdineIngrediente);
		assoMdfProdottoOrdineIngrediente.setTipoModifica(this);

		return assoMdfProdottoOrdineIngrediente;
	}

	public AssoMdfProdottoOrdineIngrediente removeAssoMdfProdottoOrdineIngrediente(AssoMdfProdottoOrdineIngrediente assoMdfProdottoOrdineIngrediente) {
		getAssoMdfProdottoOrdineIngredientes().remove(assoMdfProdottoOrdineIngrediente);
		assoMdfProdottoOrdineIngrediente.setTipoModifica(null);

		return assoMdfProdottoOrdineIngrediente;
	}

}