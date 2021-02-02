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
 * The persistent class for the cliente database table.
 * 
 */
@Entity
@Table(name="cliente")
@NamedQuery(name="Cliente.findAll", query="SELECT c FROM Cliente c")
public class Cliente implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CLIENTE_IDCLIENTE_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CLIENTE_IDCLIENTE_GENERATOR")
	@Column(name="id_cliente", unique=true, nullable=false)
	private Long idCliente;

	@Column(length=5)
	private String cap;

	@Column(nullable=false, length=255)
	private String citofono;

	@Column(name="create_timestamp", nullable=false)
	private Timestamp createTimestamp;

	@Column(name="create_user", nullable=false, length=255)
	private String createUser;

	@Column(nullable=false, length=255)
	private String indirizzo;

	@Column(length=5)
	private String interno;

	@Column(name="note_aggiuntive", length=255)
	private String noteAggiuntive;

	@Column(name="num_civico", nullable=false, length=10)
	private String numCivico;

	@Column(length=10)
	private String scala;

	@Column(name="update_timestamp", nullable=false)
	private Timestamp updateTimestamp;

	@Column(name="update_user", nullable=false, length=255)
	private String updateUser;

	private Long version;

	//bi-directional many-to-one association to TipoToponimo
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tipo_toponimo", nullable=false)
	private TipoToponimo tipoToponimo;

	//bi-directional many-to-one association to ContattoCliente
	@OneToMany(mappedBy="cliente")
	private Set<ContattoCliente> contattoClientes;

	//bi-directional many-to-one association to Ordine
	@OneToMany(mappedBy="cliente")
	private Set<Ordine> ordines;

	//bi-directional many-to-one association to StoricoOrdine
	@OneToMany(mappedBy="cliente")
	private Set<StoricoOrdine> storicoOrdines;

	//bi-directional many-to-one association to Utente
	@OneToMany(mappedBy="cliente")
	private Set<Utente> utentes;

	public Cliente() {
	}

	public Long getIdCliente() {
		return this.idCliente;
	}

	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}

	public String getCap() {
		return this.cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getCitofono() {
		return this.citofono;
	}

	public void setCitofono(String citofono) {
		this.citofono = citofono;
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

	public String getIndirizzo() {
		return this.indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getInterno() {
		return this.interno;
	}

	public void setInterno(String interno) {
		this.interno = interno;
	}

	public String getNoteAggiuntive() {
		return this.noteAggiuntive;
	}

	public void setNoteAggiuntive(String noteAggiuntive) {
		this.noteAggiuntive = noteAggiuntive;
	}

	public String getNumCivico() {
		return this.numCivico;
	}

	public void setNumCivico(String numCivico) {
		this.numCivico = numCivico;
	}

	public String getScala() {
		return this.scala;
	}

	public void setScala(String scala) {
		this.scala = scala;
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

	public TipoToponimo getTipoToponimo() {
		return this.tipoToponimo;
	}

	public void setTipoToponimo(TipoToponimo tipoToponimo) {
		this.tipoToponimo = tipoToponimo;
	}

	public Set<ContattoCliente> getContattoClientes() {
		return this.contattoClientes;
	}

	public void setContattoClientes(Set<ContattoCliente> contattoClientes) {
		this.contattoClientes = contattoClientes;
	}

	public ContattoCliente addContattoCliente(ContattoCliente contattoCliente) {
		getContattoClientes().add(contattoCliente);
		contattoCliente.setCliente(this);

		return contattoCliente;
	}

	public ContattoCliente removeContattoCliente(ContattoCliente contattoCliente) {
		getContattoClientes().remove(contattoCliente);
		contattoCliente.setCliente(null);

		return contattoCliente;
	}

	public Set<Ordine> getOrdines() {
		return this.ordines;
	}

	public void setOrdines(Set<Ordine> ordines) {
		this.ordines = ordines;
	}

	public Ordine addOrdine(Ordine ordine) {
		getOrdines().add(ordine);
		ordine.setCliente(this);

		return ordine;
	}

	public Ordine removeOrdine(Ordine ordine) {
		getOrdines().remove(ordine);
		ordine.setCliente(null);

		return ordine;
	}

	public Set<StoricoOrdine> getStoricoOrdines() {
		return this.storicoOrdines;
	}

	public void setStoricoOrdines(Set<StoricoOrdine> storicoOrdines) {
		this.storicoOrdines = storicoOrdines;
	}

	public StoricoOrdine addStoricoOrdine(StoricoOrdine storicoOrdine) {
		getStoricoOrdines().add(storicoOrdine);
		storicoOrdine.setCliente(this);

		return storicoOrdine;
	}

	public StoricoOrdine removeStoricoOrdine(StoricoOrdine storicoOrdine) {
		getStoricoOrdines().remove(storicoOrdine);
		storicoOrdine.setCliente(null);

		return storicoOrdine;
	}

	public Set<Utente> getUtentes() {
		return this.utentes;
	}

	public void setUtentes(Set<Utente> utentes) {
		this.utentes = utentes;
	}

	public Utente addUtente(Utente utente) {
		getUtentes().add(utente);
		utente.setCliente(this);

		return utente;
	}

	public Utente removeUtente(Utente utente) {
		getUtentes().remove(utente);
		utente.setCliente(null);

		return utente;
	}

}