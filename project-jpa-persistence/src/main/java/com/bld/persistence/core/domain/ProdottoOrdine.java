package com.bld.persistence.core.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;


/**
 * The persistent class for the prodotto_ordine database table.
 * 
 */
@Entity
@Table(name="prodotto_ordine")
@NamedQuery(name="ProdottoOrdine.findAll", query="SELECT p FROM ProdottoOrdine p")
public class ProdottoOrdine implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="PRODOTTO_ORDINE_IDPRODOTTOORDINE_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PRODOTTO_ORDINE_IDPRODOTTOORDINE_GENERATOR")
	@Column(name="id_prodotto_ordine", unique=true, nullable=false)
	private Long idProdottoOrdine;

	@Column(name="create_timestamp", nullable=false)
	private Timestamp createTimestamp;

	@Column(name="create_user", nullable=false, length=255)
	private String createUser;

	@Column(name="flag_valido", nullable=false)
	private Boolean flagValido;

	@Column(nullable=false)
	private Integer quantita;

	@Column(name="riduzione_prezzo", nullable=false)
	private double riduzionePrezzo;

	@Column(name="update_timestamp", nullable=false)
	private Timestamp updateTimestamp;

	@Column(name="update_user", nullable=false, length=255)
	private String updateUser;

	private Long version;

	//bi-directional many-to-one association to ModificaProdottoOrdine
	@OneToMany(mappedBy="prodottoOrdine")
	private Set<ModificaProdottoOrdine> modificaProdottoOrdines;

	//bi-directional many-to-one association to Ordine
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_ordine", nullable=false)
	private Ordine ordine;

	//bi-directional many-to-one association to Prodotto
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_prodotto", nullable=false)
	private Prodotto prodotto;

	public ProdottoOrdine() {
	}

	public Long getIdProdottoOrdine() {
		return this.idProdottoOrdine;
	}

	public void setIdProdottoOrdine(Long idProdottoOrdine) {
		this.idProdottoOrdine = idProdottoOrdine;
	}

	public Timestamp getCreateTimestamp() {
		return this.createTimestamp;
	}

	public void setCreateTimestamp(Timestamp createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Boolean getFlagValido() {
		return this.flagValido;
	}

	public void setFlagValido(Boolean flagValido) {
		this.flagValido = flagValido;
	}

	public Integer getQuantita() {
		return this.quantita;
	}

	public void setQuantita(Integer quantita) {
		this.quantita = quantita;
	}

	public double getRiduzionePrezzo() {
		return this.riduzionePrezzo;
	}

	public void setRiduzionePrezzo(double riduzionePrezzo) {
		this.riduzionePrezzo = riduzionePrezzo;
	}

	public Timestamp getUpdateTimestamp() {
		return this.updateTimestamp;
	}

	public void setUpdateTimestamp(Timestamp updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	public String getUpdateUser() {
		return this.updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Set<ModificaProdottoOrdine> getModificaProdottoOrdines() {
		return this.modificaProdottoOrdines;
	}

	public void setModificaProdottoOrdines(Set<ModificaProdottoOrdine> modificaProdottoOrdines) {
		this.modificaProdottoOrdines = modificaProdottoOrdines;
	}

	public ModificaProdottoOrdine addModificaProdottoOrdine(ModificaProdottoOrdine modificaProdottoOrdine) {
		getModificaProdottoOrdines().add(modificaProdottoOrdine);
		modificaProdottoOrdine.setProdottoOrdine(this);

		return modificaProdottoOrdine;
	}

	public ModificaProdottoOrdine removeModificaProdottoOrdine(ModificaProdottoOrdine modificaProdottoOrdine) {
		getModificaProdottoOrdines().remove(modificaProdottoOrdine);
		modificaProdottoOrdine.setProdottoOrdine(null);

		return modificaProdottoOrdine;
	}

	public Ordine getOrdine() {
		return this.ordine;
	}

	public void setOrdine(Ordine ordine) {
		this.ordine = ordine;
	}

	public Prodotto getProdotto() {
		return this.prodotto;
	}

	public void setProdotto(Prodotto prodotto) {
		this.prodotto = prodotto;
	}

}