package com.bld.persistence.core.domain;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.bld.persistence.core.domain.base.BaseCliente;


/**
 * The persistent class for the cliente database table.
 * 
 */
@Entity
@Table(name="cliente")
@NamedQuery(name="Cliente.findAll", query="SELECT c FROM Cliente c")
public class Cliente extends BaseCliente {

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



	private Long version;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tipo_toponimo", nullable=false)
	private TipoToponimo tipoToponimo;

	@OneToMany(mappedBy="cliente")
	private Set<ContattoCliente> contattoClientes;

	@OneToMany(mappedBy="cliente")
	private Set<Ordine> ordines;

	@OneToMany(mappedBy="cliente")
	private Set<StoricoOrdine> storicoOrdines;

	@OneToMany(mappedBy="cliente")
	private Set<Utente> utentes;

	public Cliente() {
		super();
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