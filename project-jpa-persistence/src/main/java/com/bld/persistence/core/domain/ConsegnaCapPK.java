package com.bld.persistence.core.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * The primary key class for the consegna_cap database table.
 * 
 */
@Embeddable
public class ConsegnaCapPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(unique=true, nullable=false, length=5)
	private String cap;

	@Column(name="id_ristorante", insertable=false, updatable=false, unique=true, nullable=false)
	private Long idRistorante;

	public ConsegnaCapPK() {
	}
	public String getCap() {
		return this.cap;
	}
	public void setCap(String cap) {
		this.cap = cap;
	}
	public Long getIdRistorante() {
		return this.idRistorante;
	}
	public void setIdRistorante(Long idRistorante) {
		this.idRistorante = idRistorante;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ConsegnaCapPK)) {
			return false;
		}
		ConsegnaCapPK castOther = (ConsegnaCapPK)other;
		return 
			this.cap.equals(castOther.cap)
			&& this.idRistorante.equals(castOther.idRistorante);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.cap.hashCode();
		hash = hash * prime + this.idRistorante.hashCode();
		
		return hash;
	}
}