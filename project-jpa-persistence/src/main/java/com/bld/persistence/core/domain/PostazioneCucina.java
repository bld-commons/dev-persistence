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
 * The persistent class for the postazione_cucina database table.
 * 
 */
@Entity
@Table(name="postazione_cucina")
@NamedQuery(name="PostazioneCucina.findAll", query="SELECT p FROM PostazioneCucina p")
public class PostazioneCucina implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="POSTAZIONE_CUCINA_IDPOSTAZIONECUCINA_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="POSTAZIONE_CUCINA_IDPOSTAZIONECUCINA_GENERATOR")
	@Column(name="id_postazione_cucina", unique=true, nullable=false)
	private Long idPostazioneCucina;

	@Column(name="create_timestamp", nullable=false)
	private Timestamp createTimestamp;

	@Column(name="create_user", nullable=false, length=255)
	private String createUser;

	@Column(name="des_postazione_cucina", nullable=false, length=255)
	private String desPostazioneCucina;

	@Column(name="flag_valido", nullable=false)
	private Boolean flagValido;

	@Column(name="update_timestamp", nullable=false)
	private Timestamp updateTimestamp;

	@Column(name="update_user", nullable=false, length=255)
	private String updateUser;

	private Long version;

	//bi-directional many-to-one association to Genere
	@OneToMany(mappedBy="postazioneCucina")
	private Set<Genere> generes;

	//bi-directional many-to-one association to Ristorante
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_ristorante", nullable=false)
	private Ristorante ristorante;

	public PostazioneCucina() {
	}

	public Long getIdPostazioneCucina() {
		return this.idPostazioneCucina;
	}

	public void setIdPostazioneCucina(Long idPostazioneCucina) {
		this.idPostazioneCucina = idPostazioneCucina;
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

	public String getDesPostazioneCucina() {
		return this.desPostazioneCucina;
	}

	public void setDesPostazioneCucina(String desPostazioneCucina) {
		this.desPostazioneCucina = desPostazioneCucina;
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

	public Set<Genere> getGeneres() {
		return this.generes;
	}

	public void setGeneres(Set<Genere> generes) {
		this.generes = generes;
	}

	public Genere addGenere(Genere genere) {
		getGeneres().add(genere);
		genere.setPostazioneCucina(this);

		return genere;
	}

	public Genere removeGenere(Genere genere) {
		getGeneres().remove(genere);
		genere.setPostazioneCucina(null);

		return genere;
	}

	public Ristorante getRistorante() {
		return this.ristorante;
	}

	public void setRistorante(Ristorante ristorante) {
		this.ristorante = ristorante;
	}

}