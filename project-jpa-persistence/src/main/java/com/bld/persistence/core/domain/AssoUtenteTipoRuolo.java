package com.bld.persistence.core.domain;

import java.io.Serializable;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;


/**
 * The persistent class for the asso_utente_tipo_ruolo database table.
 * 
 */
@Entity
@Table(name="asso_utente_tipo_ruolo")
@NamedQuery(name="AssoUtenteTipoRuolo.findAll", query="SELECT a FROM AssoUtenteTipoRuolo a")
public class AssoUtenteTipoRuolo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AssoUtenteTipoRuoloPK id;

	//bi-directional many-to-one association to Ristorante
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_ristorante")
	private Ristorante ristorante;

	//bi-directional many-to-one association to TipoRuolo
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tipo_ruolo", nullable=false, insertable=false, updatable=false)
	private TipoRuolo tipoRuolo;

	//bi-directional many-to-one association to Utente
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_utente", nullable=false, insertable=false, updatable=false)
	private Utente utente;

	public AssoUtenteTipoRuolo() {
	}

	public AssoUtenteTipoRuoloPK getId() {
		return this.id;
	}

	public void setId(AssoUtenteTipoRuoloPK id) {
		this.id = id;
	}

	public Ristorante getRistorante() {
		return this.ristorante;
	}

	public void setRistorante(Ristorante ristorante) {
		this.ristorante = ristorante;
	}

	public TipoRuolo getTipoRuolo() {
		return this.tipoRuolo;
	}

	public void setTipoRuolo(TipoRuolo tipoRuolo) {
		this.tipoRuolo = tipoRuolo;
	}

	public Utente getUtente() {
		return this.utente;
	}

	public void setUtente(Utente utente) {
		this.utente = utente;
	}

}