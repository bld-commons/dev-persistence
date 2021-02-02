package com.bld.persistence.core.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


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