package com.bld.persistence.core.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;


/**
 * The persistent class for the menu_ordine_prodotto database table.
 * 
 */
@Entity
@Table(name="menu_ordine_prodotto")
@NamedQuery(name="MenuOrdineProdotto.findAll", query="SELECT m FROM MenuOrdineProdotto m")
public class MenuOrdineProdotto implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MenuOrdineProdottoPK id;

	@Column(nullable=false)
	private Integer quantita;

	//bi-directional many-to-one association to Ordine
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_ordine", nullable=false, insertable=false, updatable=false)
	private Ordine ordine;

	//bi-directional many-to-one association to Prodotto
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_menu", nullable=false, insertable=false, updatable=false)
	private Prodotto prodotto1;

	//bi-directional many-to-one association to Prodotto
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_prodotto", nullable=false, insertable=false, updatable=false)
	private Prodotto prodotto2;

	public MenuOrdineProdotto() {
	}

	public MenuOrdineProdottoPK getId() {
		return this.id;
	}

	public void setId(MenuOrdineProdottoPK id) {
		this.id = id;
	}

	public Integer getQuantita() {
		return this.quantita;
	}

	public void setQuantita(Integer quantita) {
		this.quantita = quantita;
	}

	public Ordine getOrdine() {
		return this.ordine;
	}

	public void setOrdine(Ordine ordine) {
		this.ordine = ordine;
	}

	public Prodotto getProdotto1() {
		return this.prodotto1;
	}

	public void setProdotto1(Prodotto prodotto1) {
		this.prodotto1 = prodotto1;
	}

	public Prodotto getProdotto2() {
		return this.prodotto2;
	}

	public void setProdotto2(Prodotto prodotto2) {
		this.prodotto2 = prodotto2;
	}

}