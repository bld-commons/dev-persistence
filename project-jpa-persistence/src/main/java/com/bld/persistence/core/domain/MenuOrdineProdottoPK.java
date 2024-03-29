package com.bld.persistence.core.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * The primary key class for the menu_ordine_prodotto database table.
 * 
 */
@Embeddable
public class MenuOrdineProdottoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="id_menu", insertable=false, updatable=false, unique=true, nullable=false)
	private Long idMenu;

	@Column(name="id_prodotto", insertable=false, updatable=false, unique=true, nullable=false)
	private Long idProdotto;

	@Column(name="id_ordine", insertable=false, updatable=false, unique=true, nullable=false)
	private Long idOrdine;

	public MenuOrdineProdottoPK() {
	}
	public Long getIdMenu() {
		return this.idMenu;
	}
	public void setIdMenu(Long idMenu) {
		this.idMenu = idMenu;
	}
	public Long getIdProdotto() {
		return this.idProdotto;
	}
	public void setIdProdotto(Long idProdotto) {
		this.idProdotto = idProdotto;
	}
	public Long getIdOrdine() {
		return this.idOrdine;
	}
	public void setIdOrdine(Long idOrdine) {
		this.idOrdine = idOrdine;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MenuOrdineProdottoPK)) {
			return false;
		}
		MenuOrdineProdottoPK castOther = (MenuOrdineProdottoPK)other;
		return 
			this.idMenu.equals(castOther.idMenu)
			&& this.idProdotto.equals(castOther.idProdotto)
			&& this.idOrdine.equals(castOther.idOrdine);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.idMenu.hashCode();
		hash = hash * prime + this.idProdotto.hashCode();
		hash = hash * prime + this.idOrdine.hashCode();
		
		return hash;
	}
}