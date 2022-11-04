package com.bld.persistence.core.domain.base;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

@MappedSuperclass
public class BaseCliente extends BaseEntity {

	@Id
	@SequenceGenerator(name = "CLIENTE_IDCLIENTE_GENERATOR")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLIENTE_IDCLIENTE_GENERATOR")
	@Column(name = "id_cliente", unique = true, nullable = false)
	private Long idCliente;

	public BaseCliente() {
		super();
	}

	public Long getIdCliente() {
		return this.idCliente;
	}

	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}

}