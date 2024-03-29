package com.bld.persistence.core.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * The primary key class for the asso_utente_tipo_ruolo database table.
 * 
 */
@Embeddable
public class AssoUtenteTipoRuoloPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="id_utente", insertable=false, updatable=false, unique=true, nullable=false)
	private Long idUtente;

	@Column(name="id_tipo_ruolo", insertable=false, updatable=false, unique=true, nullable=false)
	private Long idTipoRuolo;

	@Column(name="url_path", unique=true, nullable=false, length=255)
	private String urlPath;

	public AssoUtenteTipoRuoloPK() {
	}
	public Long getIdUtente() {
		return this.idUtente;
	}
	public void setIdUtente(Long idUtente) {
		this.idUtente = idUtente;
	}
	public Long getIdTipoRuolo() {
		return this.idTipoRuolo;
	}
	public void setIdTipoRuolo(Long idTipoRuolo) {
		this.idTipoRuolo = idTipoRuolo;
	}
	public String getUrlPath() {
		return this.urlPath;
	}
	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AssoUtenteTipoRuoloPK)) {
			return false;
		}
		AssoUtenteTipoRuoloPK castOther = (AssoUtenteTipoRuoloPK)other;
		return 
			this.idUtente.equals(castOther.idUtente)
			&& this.idTipoRuolo.equals(castOther.idTipoRuolo)
			&& this.urlPath.equals(castOther.urlPath);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.idUtente.hashCode();
		hash = hash * prime + this.idTipoRuolo.hashCode();
		hash = hash * prime + this.urlPath.hashCode();
		
		return hash;
	}
}