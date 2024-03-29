package com.bld.persistence.core.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;


/**
 * The persistent class for the contatto_cliente database table.
 * 
 */
@Entity
@Table(name="contatto_cliente")
@NamedQuery(name="ContattoCliente.findAll", query="SELECT c FROM ContattoCliente c")
public class ContattoCliente implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CONTATTO_CLIENTE_CONTATTO_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CONTATTO_CLIENTE_CONTATTO_GENERATOR")
	@Column(unique=true, nullable=false, length=255)
	private String contatto;

	//bi-directional many-to-one association to Cliente
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_cliente", nullable=false)
	private Cliente cliente;

	//bi-directional many-to-one association to TipoContatto
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tipo_contatto", nullable=false)
	private TipoContatto tipoContatto;

	public ContattoCliente() {
	}

	public String getContatto() {
		return this.contatto;
	}

	public void setContatto(String contatto) {
		this.contatto = contatto;
	}

	public Cliente getCliente() {
		return this.cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public TipoContatto getTipoContatto() {
		return this.tipoContatto;
	}

	public void setTipoContatto(TipoContatto tipoContatto) {
		this.tipoContatto = tipoContatto;
	}

}