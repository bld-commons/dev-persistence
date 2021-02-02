package com.bld.persistence.core.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the modifica_prodotto_ordine database table.
 * 
 */
@Entity
@Table(name="modifica_prodotto_ordine")
@NamedQuery(name="ModificaProdottoOrdine.findAll", query="SELECT m FROM ModificaProdottoOrdine m")
public class ModificaProdottoOrdine implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="MODIFICA_PRODOTTO_ORDINE_IDMODIFICAPRODOTTOORDINE_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MODIFICA_PRODOTTO_ORDINE_IDMODIFICAPRODOTTOORDINE_GENERATOR")
	@Column(name="id_modifica_prodotto_ordine", unique=true, nullable=false)
	private Long idModificaProdottoOrdine;

	@Column(name="create_timestamp", nullable=false)
	private Timestamp createTimestamp;

	@Column(name="create_user", nullable=false, length=255)
	private String createUser;

	@Column(name="flag_valido", nullable=false)
	private Boolean flagValido;

	@Column(nullable=false)
	private Integer quantita;

	@Column(name="update_timestamp", nullable=false)
	private Timestamp updateTimestamp;

	@Column(name="update_user", nullable=false, length=255)
	private String updateUser;

	private Long version;

	//bi-directional many-to-one association to AssoMdfProdottoOrdineIngrediente
	@OneToMany(mappedBy="modificaProdottoOrdine")
	private Set<AssoMdfProdottoOrdineIngrediente> assoMdfProdottoOrdineIngredientes;

	//bi-directional many-to-one association to ProdottoOrdine
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_prodotto_ordine", nullable=false)
	private ProdottoOrdine prodottoOrdine;

	public ModificaProdottoOrdine() {
	}

	public Long getIdModificaProdottoOrdine() {
		return this.idModificaProdottoOrdine;
	}

	public void setIdModificaProdottoOrdine(Long idModificaProdottoOrdine) {
		this.idModificaProdottoOrdine = idModificaProdottoOrdine;
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

	public Set<AssoMdfProdottoOrdineIngrediente> getAssoMdfProdottoOrdineIngredientes() {
		return this.assoMdfProdottoOrdineIngredientes;
	}

	public void setAssoMdfProdottoOrdineIngredientes(Set<AssoMdfProdottoOrdineIngrediente> assoMdfProdottoOrdineIngredientes) {
		this.assoMdfProdottoOrdineIngredientes = assoMdfProdottoOrdineIngredientes;
	}

	public AssoMdfProdottoOrdineIngrediente addAssoMdfProdottoOrdineIngrediente(AssoMdfProdottoOrdineIngrediente assoMdfProdottoOrdineIngrediente) {
		getAssoMdfProdottoOrdineIngredientes().add(assoMdfProdottoOrdineIngrediente);
		assoMdfProdottoOrdineIngrediente.setModificaProdottoOrdine(this);

		return assoMdfProdottoOrdineIngrediente;
	}

	public AssoMdfProdottoOrdineIngrediente removeAssoMdfProdottoOrdineIngrediente(AssoMdfProdottoOrdineIngrediente assoMdfProdottoOrdineIngrediente) {
		getAssoMdfProdottoOrdineIngredientes().remove(assoMdfProdottoOrdineIngrediente);
		assoMdfProdottoOrdineIngrediente.setModificaProdottoOrdine(null);

		return assoMdfProdottoOrdineIngrediente;
	}

	public ProdottoOrdine getProdottoOrdine() {
		return this.prodottoOrdine;
	}

	public void setProdottoOrdine(ProdottoOrdine prodottoOrdine) {
		this.prodottoOrdine = prodottoOrdine;
	}

}