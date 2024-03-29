package com.bld.persistence.core.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * The primary key class for the storico_ordine database table.
 * 
 */
@Embeddable
public class StoricoOrdinePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="id_cliente", insertable=false, updatable=false, unique=true, nullable=false)
	private Long idCliente;

	@Column(name="id_ristorante", insertable=false, updatable=false, unique=true, nullable=false)
	private Long idRistorante;

	@Column(name="codice_ordine", unique=true, nullable=false, length=255)
	private String codiceOrdine;

	public StoricoOrdinePK() {
	}
	public Long getIdCliente() {
		return this.idCliente;
	}
	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}
	public Long getIdRistorante() {
		return this.idRistorante;
	}
	public void setIdRistorante(Long idRistorante) {
		this.idRistorante = idRistorante;
	}
	public String getCodiceOrdine() {
		return this.codiceOrdine;
	}
	public void setCodiceOrdine(String codiceOrdine) {
		this.codiceOrdine = codiceOrdine;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof StoricoOrdinePK)) {
			return false;
		}
		StoricoOrdinePK castOther = (StoricoOrdinePK)other;
		return 
			this.idCliente.equals(castOther.idCliente)
			&& this.idRistorante.equals(castOther.idRistorante)
			&& this.codiceOrdine.equals(castOther.codiceOrdine);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.idCliente.hashCode();
		hash = hash * prime + this.idRistorante.hashCode();
		hash = hash * prime + this.codiceOrdine.hashCode();
		
		return hash;
	}
}