package com.bld.persistence.core.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the ristorante database table.
 * 
 */
@Entity
@Table(name="ristorante")
@NamedQuery(name="Ristorante.findAll", query="SELECT r FROM Ristorante r")
public class Ristorante implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="RISTORANTE_IDRISTORANTE_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="RISTORANTE_IDRISTORANTE_GENERATOR")
	@Column(name="id_ristorante", unique=true, nullable=false)
	private Long idRistorante;

	@Column(nullable=false, length=5)
	private String cap;

	@Column(nullable=false, length=255)
	private String indirizzo;

	@Column(nullable=false, length=255)
	private String nome;

	@Column(name="num_civico", nullable=false, length=10)
	private String numCivico;

	//bi-directional many-to-one association to AssoUtenteTipoRuolo
	@OneToMany(mappedBy="ristorante")
	private Set<AssoUtenteTipoRuolo> assoUtenteTipoRuolos;

	//bi-directional many-to-one association to ConsegnaCap
	@OneToMany(mappedBy="ristorante")
	private Set<ConsegnaCap> consegnaCaps;

	//bi-directional many-to-one association to Ingrediente
	@OneToMany(mappedBy="ristorante")
	private Set<Ingrediente> ingredientes;

	//bi-directional many-to-one association to Ordine
	@OneToMany(mappedBy="ristorante")
	private Set<Ordine> ordines;

	//bi-directional many-to-one association to PostazioneCucina
	@OneToMany(mappedBy="ristorante")
	private Set<PostazioneCucina> postazioneCucinas;

	//bi-directional many-to-one association to TipoToponimo
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tipo_toponimo", nullable=false)
	private TipoToponimo tipoToponimo;

	//bi-directional many-to-one association to Speedy
	@OneToMany(mappedBy="ristorante")
	private Set<Speedy> speedies;

	//bi-directional many-to-one association to StoricoOrdine
	@OneToMany(mappedBy="ristorante")
	private Set<StoricoOrdine> storicoOrdines;

	//bi-directional many-to-many association to Utente
	@ManyToMany(mappedBy="ristorantes")
	private Set<Utente> utentes;

	public Ristorante() {
	}

	public Long getIdRistorante() {
		return this.idRistorante;
	}

	public void setIdRistorante(Long idRistorante) {
		this.idRistorante = idRistorante;
	}

	public String getCap() {
		return this.cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getIndirizzo() {
		return this.indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNumCivico() {
		return this.numCivico;
	}

	public void setNumCivico(String numCivico) {
		this.numCivico = numCivico;
	}

	public Set<AssoUtenteTipoRuolo> getAssoUtenteTipoRuolos() {
		return this.assoUtenteTipoRuolos;
	}

	public void setAssoUtenteTipoRuolos(Set<AssoUtenteTipoRuolo> assoUtenteTipoRuolos) {
		this.assoUtenteTipoRuolos = assoUtenteTipoRuolos;
	}

	public AssoUtenteTipoRuolo addAssoUtenteTipoRuolo(AssoUtenteTipoRuolo assoUtenteTipoRuolo) {
		getAssoUtenteTipoRuolos().add(assoUtenteTipoRuolo);
		assoUtenteTipoRuolo.setRistorante(this);

		return assoUtenteTipoRuolo;
	}

	public AssoUtenteTipoRuolo removeAssoUtenteTipoRuolo(AssoUtenteTipoRuolo assoUtenteTipoRuolo) {
		getAssoUtenteTipoRuolos().remove(assoUtenteTipoRuolo);
		assoUtenteTipoRuolo.setRistorante(null);

		return assoUtenteTipoRuolo;
	}

	public Set<ConsegnaCap> getConsegnaCaps() {
		return this.consegnaCaps;
	}

	public void setConsegnaCaps(Set<ConsegnaCap> consegnaCaps) {
		this.consegnaCaps = consegnaCaps;
	}

	public ConsegnaCap addConsegnaCap(ConsegnaCap consegnaCap) {
		getConsegnaCaps().add(consegnaCap);
		consegnaCap.setRistorante(this);

		return consegnaCap;
	}

	public ConsegnaCap removeConsegnaCap(ConsegnaCap consegnaCap) {
		getConsegnaCaps().remove(consegnaCap);
		consegnaCap.setRistorante(null);

		return consegnaCap;
	}

	public Set<Ingrediente> getIngredientes() {
		return this.ingredientes;
	}

	public void setIngredientes(Set<Ingrediente> ingredientes) {
		this.ingredientes = ingredientes;
	}

	public Ingrediente addIngrediente(Ingrediente ingrediente) {
		getIngredientes().add(ingrediente);
		ingrediente.setRistorante(this);

		return ingrediente;
	}

	public Ingrediente removeIngrediente(Ingrediente ingrediente) {
		getIngredientes().remove(ingrediente);
		ingrediente.setRistorante(null);

		return ingrediente;
	}

	public Set<Ordine> getOrdines() {
		return this.ordines;
	}

	public void setOrdines(Set<Ordine> ordines) {
		this.ordines = ordines;
	}

	public Ordine addOrdine(Ordine ordine) {
		getOrdines().add(ordine);
		ordine.setRistorante(this);

		return ordine;
	}

	public Ordine removeOrdine(Ordine ordine) {
		getOrdines().remove(ordine);
		ordine.setRistorante(null);

		return ordine;
	}

	public Set<PostazioneCucina> getPostazioneCucinas() {
		return this.postazioneCucinas;
	}

	public void setPostazioneCucinas(Set<PostazioneCucina> postazioneCucinas) {
		this.postazioneCucinas = postazioneCucinas;
	}

	public PostazioneCucina addPostazioneCucina(PostazioneCucina postazioneCucina) {
		getPostazioneCucinas().add(postazioneCucina);
		postazioneCucina.setRistorante(this);

		return postazioneCucina;
	}

	public PostazioneCucina removePostazioneCucina(PostazioneCucina postazioneCucina) {
		getPostazioneCucinas().remove(postazioneCucina);
		postazioneCucina.setRistorante(null);

		return postazioneCucina;
	}

	public TipoToponimo getTipoToponimo() {
		return this.tipoToponimo;
	}

	public void setTipoToponimo(TipoToponimo tipoToponimo) {
		this.tipoToponimo = tipoToponimo;
	}

	public Set<Speedy> getSpeedies() {
		return this.speedies;
	}

	public void setSpeedies(Set<Speedy> speedies) {
		this.speedies = speedies;
	}

	public Speedy addSpeedy(Speedy speedy) {
		getSpeedies().add(speedy);
		speedy.setRistorante(this);

		return speedy;
	}

	public Speedy removeSpeedy(Speedy speedy) {
		getSpeedies().remove(speedy);
		speedy.setRistorante(null);

		return speedy;
	}

	public Set<StoricoOrdine> getStoricoOrdines() {
		return this.storicoOrdines;
	}

	public void setStoricoOrdines(Set<StoricoOrdine> storicoOrdines) {
		this.storicoOrdines = storicoOrdines;
	}

	public StoricoOrdine addStoricoOrdine(StoricoOrdine storicoOrdine) {
		getStoricoOrdines().add(storicoOrdine);
		storicoOrdine.setRistorante(this);

		return storicoOrdine;
	}

	public StoricoOrdine removeStoricoOrdine(StoricoOrdine storicoOrdine) {
		getStoricoOrdines().remove(storicoOrdine);
		storicoOrdine.setRistorante(null);

		return storicoOrdine;
	}

	public Set<Utente> getUtentes() {
		return this.utentes;
	}

	public void setUtentes(Set<Utente> utentes) {
		this.utentes = utentes;
	}

}