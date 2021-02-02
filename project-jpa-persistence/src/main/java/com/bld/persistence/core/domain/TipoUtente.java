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
 * The persistent class for the tipo_utente database table.
 * 
 */
@Entity
@Table(name="tipo_utente")
@NamedQuery(name="TipoUtente.findAll", query="SELECT t FROM TipoUtente t")
public class TipoUtente implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TIPO_UTENTE_IDTIPOUTENTE_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TIPO_UTENTE_IDTIPOUTENTE_GENERATOR")
	@Column(name="id_tipo_utente", unique=true, nullable=false)
	private Long idTipoUtente;

	@Column(name="des_tipo_utente", nullable=false, length=255)
	private String desTipoUtente;

	//bi-directional many-to-one association to Utente
	@OneToMany(mappedBy="tipoUtente")
	private Set<Utente> utentes;

	public TipoUtente() {
	}

	public Long getIdTipoUtente() {
		return this.idTipoUtente;
	}

	public void setIdTipoUtente(Long idTipoUtente) {
		this.idTipoUtente = idTipoUtente;
	}

	public String getDesTipoUtente() {
		return this.desTipoUtente;
	}

	public void setDesTipoUtente(String desTipoUtente) {
		this.desTipoUtente = desTipoUtente;
	}

	public Set<Utente> getUtentes() {
		return this.utentes;
	}

	public void setUtentes(Set<Utente> utentes) {
		this.utentes = utentes;
	}

	public Utente addUtente(Utente utente) {
		getUtentes().add(utente);
		utente.setTipoUtente(this);

		return utente;
	}

	public Utente removeUtente(Utente utente) {
		getUtentes().remove(utente);
		utente.setTipoUtente(null);

		return utente;
	}

}