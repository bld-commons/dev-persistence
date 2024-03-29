package com.bld.persistence.core.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;


/**
 * The persistent class for the storico_ordine database table.
 * 
 */
@Entity
@Table(name="storico_ordine")
@NamedQuery(name="StoricoOrdine.findAll", query="SELECT s FROM StoricoOrdine s")
public class StoricoOrdine implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private StoricoOrdinePK id;

	@Column(name="create_timestamp", nullable=false)
	private Timestamp createTimestamp;

	@Column(name="create_user", nullable=false, length=255)
	private String createUser;

	@Column(name="data_ricevuta", nullable=false)
	private Timestamp dataRicevuta;

	@Column(nullable=false)
	private byte[] ricevuta;

	@Column(name="update_timestamp", nullable=false)
	private Timestamp updateTimestamp;

	@Column(name="update_user", nullable=false, length=255)
	private String updateUser;

	private Long version;

	//bi-directional many-to-one association to Cliente
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_cliente", nullable=false, insertable=false, updatable=false)
	private Cliente cliente;

	//bi-directional many-to-one association to Ristorante
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_ristorante", nullable=false, insertable=false, updatable=false)
	private Ristorante ristorante;

	public StoricoOrdine() {
	}

	public StoricoOrdinePK getId() {
		return this.id;
	}

	public void setId(StoricoOrdinePK id) {
		this.id = id;
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

	public Timestamp getDataRicevuta() {
		return this.dataRicevuta;
	}

	public void setDataRicevuta(Timestamp dataRicevuta) {
		this.dataRicevuta = dataRicevuta;
	}

	public byte[] getRicevuta() {
		return this.ricevuta;
	}

	public void setRicevuta(byte[] ricevuta) {
		this.ricevuta = ricevuta;
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

	public Cliente getCliente() {
		return this.cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Ristorante getRistorante() {
		return this.ristorante;
	}

	public void setRistorante(Ristorante ristorante) {
		this.ristorante = ristorante;
	}

}