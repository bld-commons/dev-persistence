package com.bld.persistence.core.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the tipo_contatto database table.
 * 
 */
@Entity
@Table(name="tipo_contatto")
@NamedQuery(name="TipoContatto.findAll", query="SELECT t FROM TipoContatto t")
public class TipoContatto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TIPO_CONTATTO_IDTIPOCONTATTO_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TIPO_CONTATTO_IDTIPOCONTATTO_GENERATOR")
	@Column(name="id_tipo_contatto", unique=true, nullable=false)
	private Long idTipoContatto;

	@Column(name="des_tipo_contatto", nullable=false, length=255)
	private String desTipoContatto;

	//bi-directional many-to-one association to ContattoCliente
	@OneToMany(mappedBy="tipoContatto")
	private Set<ContattoCliente> contattoClientes;

	public TipoContatto() {
	}

	public Long getIdTipoContatto() {
		return this.idTipoContatto;
	}

	public void setIdTipoContatto(Long idTipoContatto) {
		this.idTipoContatto = idTipoContatto;
	}

	public String getDesTipoContatto() {
		return this.desTipoContatto;
	}

	public void setDesTipoContatto(String desTipoContatto) {
		this.desTipoContatto = desTipoContatto;
	}

	public Set<ContattoCliente> getContattoClientes() {
		return this.contattoClientes;
	}

	public void setContattoClientes(Set<ContattoCliente> contattoClientes) {
		this.contattoClientes = contattoClientes;
	}

	public ContattoCliente addContattoCliente(ContattoCliente contattoCliente) {
		getContattoClientes().add(contattoCliente);
		contattoCliente.setTipoContatto(this);

		return contattoCliente;
	}

	public ContattoCliente removeContattoCliente(ContattoCliente contattoCliente) {
		getContattoClientes().remove(contattoCliente);
		contattoCliente.setTipoContatto(null);

		return contattoCliente;
	}

}