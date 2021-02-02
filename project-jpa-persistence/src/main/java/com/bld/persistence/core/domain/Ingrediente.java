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
 * The persistent class for the ingrediente database table.
 * 
 */
@Entity
@Table(name="ingrediente")
@NamedQuery(name="Ingrediente.findAll", query="SELECT i FROM Ingrediente i")
public class Ingrediente implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="INGREDIENTE_IDINGREDIENTE_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="INGREDIENTE_IDINGREDIENTE_GENERATOR")
	@Column(name="id_ingrediente", unique=true, nullable=false)
	private Long idIngrediente;

	@Column(name="create_timestamp", nullable=false)
	private Timestamp createTimestamp;

	@Column(name="create_user", nullable=false, length=255)
	private String createUser;

	@Column(name="des_ingrediente", nullable=false, length=255)
	private String desIngrediente;

	@Column(name="flag_valido", nullable=false)
	private Boolean flagValido;

	@Column(name="update_timestamp", nullable=false)
	private Timestamp updateTimestamp;

	@Column(name="update_user", nullable=false, length=255)
	private String updateUser;

	private Long version;

	//bi-directional many-to-one association to AssoMdfProdottoOrdineIngrediente
	@OneToMany(mappedBy="ingrediente")
	private Set<AssoMdfProdottoOrdineIngrediente> assoMdfProdottoOrdineIngredientes;

	//bi-directional many-to-one association to Ristorante
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_ristorante", nullable=false)
	private Ristorante ristorante;

	public Ingrediente() {
	}

	public Long getIdIngrediente() {
		return this.idIngrediente;
	}

	public void setIdIngrediente(Long idIngrediente) {
		this.idIngrediente = idIngrediente;
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

	public String getDesIngrediente() {
		return this.desIngrediente;
	}

	public void setDesIngrediente(String desIngrediente) {
		this.desIngrediente = desIngrediente;
	}

	public Boolean getFlagValido() {
		return this.flagValido;
	}

	public void setFlagValido(Boolean flagValido) {
		this.flagValido = flagValido;
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
		assoMdfProdottoOrdineIngrediente.setIngrediente(this);

		return assoMdfProdottoOrdineIngrediente;
	}

	public AssoMdfProdottoOrdineIngrediente removeAssoMdfProdottoOrdineIngrediente(AssoMdfProdottoOrdineIngrediente assoMdfProdottoOrdineIngrediente) {
		getAssoMdfProdottoOrdineIngredientes().remove(assoMdfProdottoOrdineIngrediente);
		assoMdfProdottoOrdineIngrediente.setIngrediente(null);

		return assoMdfProdottoOrdineIngrediente;
	}

	public Ristorante getRistorante() {
		return this.ristorante;
	}

	public void setRistorante(Ristorante ristorante) {
		this.ristorante = ristorante;
	}

}