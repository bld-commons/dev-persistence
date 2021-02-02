package com.bld.persistence.core.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the utente database table.
 * 
 */
@Entity
@Table(name="utente")
@NamedQuery(name="Utente.findAll", query="SELECT u FROM Utente u")
public class Utente implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="UTENTE_IDUTENTE_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="UTENTE_IDUTENTE_GENERATOR")
	@Column(name="id_utente", unique=true, nullable=false)
	private Long idUtente;

	@Column(name="cod_fiscale", nullable=false, length=16)
	private String codFiscale;

	@Column(nullable=false, length=255)
	private String cognome;

	@Column(name="conferma_registrazione", nullable=false)
	private Boolean confermaRegistrazione;

	@Column(name="create_timestamp", nullable=false)
	private Timestamp createTimestamp;

	@Column(name="create_user", nullable=false, length=255)
	private String createUser;

	@Temporal(TemporalType.DATE)
	@Column(name="data_nascita", nullable=false)
	private Date dataNascita;

	@Column(nullable=false, length=255)
	private String email;

	@Column(nullable=false, length=255)
	private String nome;

	@Column(nullable=false, length=255)
	private String password;

	@Column(name="update_timestamp", nullable=false)
	private Timestamp updateTimestamp;

	@Column(name="update_user", nullable=false, length=255)
	private String updateUser;

	//bi-directional many-to-one association to AssoUtenteTipoRuolo
	@OneToMany(mappedBy="utente")
	private Set<AssoUtenteTipoRuolo> assoUtenteTipoRuolos;

	//bi-directional many-to-one association to Speedy
	@OneToMany(mappedBy="utente")
	private Set<Speedy> speedies;

	//bi-directional many-to-one association to Cliente
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_cliente")
	private Cliente cliente;

	//bi-directional many-to-many association to Ristorante
	@ManyToMany
	@JoinTable(
		name="asso_ristorante_utente"
		, joinColumns={
			@JoinColumn(name="id_utente", nullable=false)
			}
		, inverseJoinColumns={
			@JoinColumn(name="id_ristorante", nullable=false)
			}
		)
	private Set<Ristorante> ristorantes;

	//bi-directional many-to-one association to TipoUtente
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tipo_utente", nullable=false)
	private TipoUtente tipoUtente;

	public Utente() {
	}

	public Long getIdUtente() {
		return this.idUtente;
	}

	public void setIdUtente(Long idUtente) {
		this.idUtente = idUtente;
	}

	public String getCodFiscale() {
		return this.codFiscale;
	}

	public void setCodFiscale(String codFiscale) {
		this.codFiscale = codFiscale;
	}

	public String getCognome() {
		return this.cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public Boolean getConfermaRegistrazione() {
		return this.confermaRegistrazione;
	}

	public void setConfermaRegistrazione(Boolean confermaRegistrazione) {
		this.confermaRegistrazione = confermaRegistrazione;
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

	public Date getDataNascita() {
		return this.dataNascita;
	}

	public void setDataNascita(Date dataNascita) {
		this.dataNascita = dataNascita;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Set<AssoUtenteTipoRuolo> getAssoUtenteTipoRuolos() {
		return this.assoUtenteTipoRuolos;
	}

	public void setAssoUtenteTipoRuolos(Set<AssoUtenteTipoRuolo> assoUtenteTipoRuolos) {
		this.assoUtenteTipoRuolos = assoUtenteTipoRuolos;
	}

	public AssoUtenteTipoRuolo addAssoUtenteTipoRuolo(AssoUtenteTipoRuolo assoUtenteTipoRuolo) {
		getAssoUtenteTipoRuolos().add(assoUtenteTipoRuolo);
		assoUtenteTipoRuolo.setUtente(this);

		return assoUtenteTipoRuolo;
	}

	public AssoUtenteTipoRuolo removeAssoUtenteTipoRuolo(AssoUtenteTipoRuolo assoUtenteTipoRuolo) {
		getAssoUtenteTipoRuolos().remove(assoUtenteTipoRuolo);
		assoUtenteTipoRuolo.setUtente(null);

		return assoUtenteTipoRuolo;
	}

	public Set<Speedy> getSpeedies() {
		return this.speedies;
	}

	public void setSpeedies(Set<Speedy> speedies) {
		this.speedies = speedies;
	}

	public Speedy addSpeedy(Speedy speedy) {
		getSpeedies().add(speedy);
		speedy.setUtente(this);

		return speedy;
	}

	public Speedy removeSpeedy(Speedy speedy) {
		getSpeedies().remove(speedy);
		speedy.setUtente(null);

		return speedy;
	}

	public Cliente getCliente() {
		return this.cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Set<Ristorante> getRistorantes() {
		return this.ristorantes;
	}

	public void setRistorantes(Set<Ristorante> ristorantes) {
		this.ristorantes = ristorantes;
	}

	public TipoUtente getTipoUtente() {
		return this.tipoUtente;
	}

	public void setTipoUtente(TipoUtente tipoUtente) {
		this.tipoUtente = tipoUtente;
	}

}