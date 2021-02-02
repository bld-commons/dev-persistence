package com.bld.persistence.core.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the tipo_ruolo database table.
 * 
 */
@Entity
@Table(name="tipo_ruolo")
@NamedQuery(name="TipoRuolo.findAll", query="SELECT t FROM TipoRuolo t")
public class TipoRuolo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TIPO_RUOLO_IDTIPORUOLO_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TIPO_RUOLO_IDTIPORUOLO_GENERATOR")
	@Column(name="id_tipo_ruolo", unique=true, nullable=false)
	private Long idTipoRuolo;

	@Column(name="des_tipo_ruolo", nullable=false, length=255)
	private String desTipoRuolo;

	@Column(nullable=false)
	private Boolean selezionabile;

	//bi-directional many-to-one association to AssoUtenteTipoRuolo
	@OneToMany(mappedBy="tipoRuolo")
	private Set<AssoUtenteTipoRuolo> assoUtenteTipoRuolos;

	public TipoRuolo() {
	}

	public Long getIdTipoRuolo() {
		return this.idTipoRuolo;
	}

	public void setIdTipoRuolo(Long idTipoRuolo) {
		this.idTipoRuolo = idTipoRuolo;
	}

	public String getDesTipoRuolo() {
		return this.desTipoRuolo;
	}

	public void setDesTipoRuolo(String desTipoRuolo) {
		this.desTipoRuolo = desTipoRuolo;
	}

	public Boolean getSelezionabile() {
		return this.selezionabile;
	}

	public void setSelezionabile(Boolean selezionabile) {
		this.selezionabile = selezionabile;
	}

	public Set<AssoUtenteTipoRuolo> getAssoUtenteTipoRuolos() {
		return this.assoUtenteTipoRuolos;
	}

	public void setAssoUtenteTipoRuolos(Set<AssoUtenteTipoRuolo> assoUtenteTipoRuolos) {
		this.assoUtenteTipoRuolos = assoUtenteTipoRuolos;
	}

	public AssoUtenteTipoRuolo addAssoUtenteTipoRuolo(AssoUtenteTipoRuolo assoUtenteTipoRuolo) {
		getAssoUtenteTipoRuolos().add(assoUtenteTipoRuolo);
		assoUtenteTipoRuolo.setTipoRuolo(this);

		return assoUtenteTipoRuolo;
	}

	public AssoUtenteTipoRuolo removeAssoUtenteTipoRuolo(AssoUtenteTipoRuolo assoUtenteTipoRuolo) {
		getAssoUtenteTipoRuolos().remove(assoUtenteTipoRuolo);
		assoUtenteTipoRuolo.setTipoRuolo(null);

		return assoUtenteTipoRuolo;
	}

}