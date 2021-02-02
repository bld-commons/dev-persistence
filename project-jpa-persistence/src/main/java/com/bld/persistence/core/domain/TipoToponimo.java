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
 * The persistent class for the tipo_toponimo database table.
 * 
 */
@Entity
@Table(name="tipo_toponimo")
@NamedQuery(name="TipoToponimo.findAll", query="SELECT t FROM TipoToponimo t")
public class TipoToponimo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TIPO_TOPONIMO_IDTIPOTOPONIMO_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TIPO_TOPONIMO_IDTIPOTOPONIMO_GENERATOR")
	@Column(name="id_tipo_toponimo", unique=true, nullable=false)
	private Long idTipoToponimo;

	@Column(name="cod_tipo_toponimo", nullable=false, length=10)
	private String codTipoToponimo;

	@Column(name="des_tipo_toponimo", nullable=false, length=255)
	private String desTipoToponimo;

	//bi-directional many-to-one association to Cliente
	@OneToMany(mappedBy="tipoToponimo")
	private Set<Cliente> clientes;

	//bi-directional many-to-one association to Ristorante
	@OneToMany(mappedBy="tipoToponimo")
	private Set<Ristorante> ristorantes;

	public TipoToponimo() {
	}

	public Long getIdTipoToponimo() {
		return this.idTipoToponimo;
	}

	public void setIdTipoToponimo(Long idTipoToponimo) {
		this.idTipoToponimo = idTipoToponimo;
	}

	public String getCodTipoToponimo() {
		return this.codTipoToponimo;
	}

	public void setCodTipoToponimo(String codTipoToponimo) {
		this.codTipoToponimo = codTipoToponimo;
	}

	public String getDesTipoToponimo() {
		return this.desTipoToponimo;
	}

	public void setDesTipoToponimo(String desTipoToponimo) {
		this.desTipoToponimo = desTipoToponimo;
	}

	public Set<Cliente> getClientes() {
		return this.clientes;
	}

	public void setClientes(Set<Cliente> clientes) {
		this.clientes = clientes;
	}

	public Cliente addCliente(Cliente cliente) {
		getClientes().add(cliente);
		cliente.setTipoToponimo(this);

		return cliente;
	}

	public Cliente removeCliente(Cliente cliente) {
		getClientes().remove(cliente);
		cliente.setTipoToponimo(null);

		return cliente;
	}

	public Set<Ristorante> getRistorantes() {
		return this.ristorantes;
	}

	public void setRistorantes(Set<Ristorante> ristorantes) {
		this.ristorantes = ristorantes;
	}

	public Ristorante addRistorante(Ristorante ristorante) {
		getRistorantes().add(ristorante);
		ristorante.setTipoToponimo(this);

		return ristorante;
	}

	public Ristorante removeRistorante(Ristorante ristorante) {
		getRistorantes().remove(ristorante);
		ristorante.setTipoToponimo(null);

		return ristorante;
	}

}