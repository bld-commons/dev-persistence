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
 * The persistent class for the tipo_stato_ordine database table.
 * 
 */
@Entity
@Table(name="tipo_stato_ordine")
@NamedQuery(name="TipoStatoOrdine.findAll", query="SELECT t FROM TipoStatoOrdine t")
public class TipoStatoOrdine implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TIPO_STATO_ORDINE_IDTIPOSTATOORDINE_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TIPO_STATO_ORDINE_IDTIPOSTATOORDINE_GENERATOR")
	@Column(name="id_tipo_stato_ordine", unique=true, nullable=false)
	private Long idTipoStatoOrdine;

	@Column(name="des_tipo_stato_ordine", length=255)
	private String desTipoStatoOrdine;

	//bi-directional many-to-one association to Ordine
	@OneToMany(mappedBy="tipoStatoOrdine")
	private Set<Ordine> ordines;

	public TipoStatoOrdine() {
	}

	public Long getIdTipoStatoOrdine() {
		return this.idTipoStatoOrdine;
	}

	public void setIdTipoStatoOrdine(Long idTipoStatoOrdine) {
		this.idTipoStatoOrdine = idTipoStatoOrdine;
	}

	public String getDesTipoStatoOrdine() {
		return this.desTipoStatoOrdine;
	}

	public void setDesTipoStatoOrdine(String desTipoStatoOrdine) {
		this.desTipoStatoOrdine = desTipoStatoOrdine;
	}

	public Set<Ordine> getOrdines() {
		return this.ordines;
	}

	public void setOrdines(Set<Ordine> ordines) {
		this.ordines = ordines;
	}

	public Ordine addOrdine(Ordine ordine) {
		getOrdines().add(ordine);
		ordine.setTipoStatoOrdine(this);

		return ordine;
	}

	public Ordine removeOrdine(Ordine ordine) {
		getOrdines().remove(ordine);
		ordine.setTipoStatoOrdine(null);

		return ordine;
	}

}