package com.bld.persistence.core.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the configura_menu database table.
 * 
 */
@Entity
@Table(name="configura_menu")
@NamedQuery(name="ConfiguraMenu.findAll", query="SELECT c FROM ConfiguraMenu c")
public class ConfiguraMenu implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CONFIGURA_MENU_IDCONFIGURAMENU_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CONFIGURA_MENU_IDCONFIGURAMENU_GENERATOR")
	@Column(name="id_configura_menu", unique=true, nullable=false)
	private Long idConfiguraMenu;

	@Column(nullable=false)
	private Integer quantita;

	//bi-directional many-to-one association to Genere
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_genere", nullable=false)
	private Genere genere;

	//bi-directional many-to-one association to Prodotto
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_menu", nullable=false)
	private Prodotto prodotto;

	//bi-directional many-to-many association to Prodotto
	@ManyToMany(mappedBy="configuraMenus2")
	private Set<Prodotto> prodottos;

	public ConfiguraMenu() {
	}

	public Long getIdConfiguraMenu() {
		return this.idConfiguraMenu;
	}

	public void setIdConfiguraMenu(Long idConfiguraMenu) {
		this.idConfiguraMenu = idConfiguraMenu;
	}

	public Integer getQuantita() {
		return this.quantita;
	}

	public void setQuantita(Integer quantita) {
		this.quantita = quantita;
	}

	public Genere getGenere() {
		return this.genere;
	}

	public void setGenere(Genere genere) {
		this.genere = genere;
	}

	public Prodotto getProdotto() {
		return this.prodotto;
	}

	public void setProdotto(Prodotto prodotto) {
		this.prodotto = prodotto;
	}

	public Set<Prodotto> getProdottos() {
		return this.prodottos;
	}

	public void setProdottos(Set<Prodotto> prodottos) {
		this.prodottos = prodottos;
	}

}