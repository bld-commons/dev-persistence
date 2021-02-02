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
 * The persistent class for the ordine database table.
 * 
 */
@Entity
@Table(name="ordine")
@NamedQuery(name="Ordine.findAll", query="SELECT o FROM Ordine o")
public class Ordine implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="ORDINE_IDORDINE_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ORDINE_IDORDINE_GENERATOR")
	@Column(name="id_ordine", unique=true, nullable=false)
	private Long idOrdine;

	@Column(name="codice_ordine", nullable=false, length=50)
	private String codiceOrdine;

	@Column(name="create_timestamp", nullable=false)
	private Timestamp createTimestamp;

	@Column(name="create_user", nullable=false, length=255)
	private String createUser;

	@Column(name="flag_valido", nullable=false)
	private Boolean flagValido;

	@Column(nullable=false)
	private double sconto;

	@Column(name="update_timestamp", nullable=false)
	private Timestamp updateTimestamp;

	@Column(name="update_user", nullable=false, length=255)
	private String updateUser;

	private Long version;

	//bi-directional many-to-one association to MenuOrdineProdotto
	@OneToMany(mappedBy="ordine")
	private Set<MenuOrdineProdotto> menuOrdineProdottos;

	//bi-directional many-to-one association to CalendarioSpeedy
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_calendario_speedy")
	private CalendarioSpeedy calendarioSpeedy;

	//bi-directional many-to-one association to Cliente
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_cliente", nullable=false)
	private Cliente cliente;

	//bi-directional many-to-one association to Ristorante
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_ristorante", nullable=false)
	private Ristorante ristorante;

	//bi-directional many-to-one association to TipoStatoOrdine
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_tipo_stato_ordine", nullable=false)
	private TipoStatoOrdine tipoStatoOrdine;

	//bi-directional many-to-one association to ProdottoOrdine
	@OneToMany(mappedBy="ordine")
	private Set<ProdottoOrdine> prodottoOrdines;

	public Ordine() {
	}

	public Long getIdOrdine() {
		return this.idOrdine;
	}

	public void setIdOrdine(Long idOrdine) {
		this.idOrdine = idOrdine;
	}

	public String getCodiceOrdine() {
		return this.codiceOrdine;
	}

	public void setCodiceOrdine(String codiceOrdine) {
		this.codiceOrdine = codiceOrdine;
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

	public Boolean getFlagValido() {
		return this.flagValido;
	}

	public void setFlagValido(Boolean flagValido) {
		this.flagValido = flagValido;
	}

	public double getSconto() {
		return this.sconto;
	}

	public void setSconto(double sconto) {
		this.sconto = sconto;
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

	public Set<MenuOrdineProdotto> getMenuOrdineProdottos() {
		return this.menuOrdineProdottos;
	}

	public void setMenuOrdineProdottos(Set<MenuOrdineProdotto> menuOrdineProdottos) {
		this.menuOrdineProdottos = menuOrdineProdottos;
	}

	public MenuOrdineProdotto addMenuOrdineProdotto(MenuOrdineProdotto menuOrdineProdotto) {
		getMenuOrdineProdottos().add(menuOrdineProdotto);
		menuOrdineProdotto.setOrdine(this);

		return menuOrdineProdotto;
	}

	public MenuOrdineProdotto removeMenuOrdineProdotto(MenuOrdineProdotto menuOrdineProdotto) {
		getMenuOrdineProdottos().remove(menuOrdineProdotto);
		menuOrdineProdotto.setOrdine(null);

		return menuOrdineProdotto;
	}

	public CalendarioSpeedy getCalendarioSpeedy() {
		return this.calendarioSpeedy;
	}

	public void setCalendarioSpeedy(CalendarioSpeedy calendarioSpeedy) {
		this.calendarioSpeedy = calendarioSpeedy;
	}

	public Cliente getCliente() {
		return this.cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Ristorante getRistorante() {
		return this.ristorante;
	}

	public void setRistorante(Ristorante ristorante) {
		this.ristorante = ristorante;
	}

	public TipoStatoOrdine getTipoStatoOrdine() {
		return this.tipoStatoOrdine;
	}

	public void setTipoStatoOrdine(TipoStatoOrdine tipoStatoOrdine) {
		this.tipoStatoOrdine = tipoStatoOrdine;
	}

	public Set<ProdottoOrdine> getProdottoOrdines() {
		return this.prodottoOrdines;
	}

	public void setProdottoOrdines(Set<ProdottoOrdine> prodottoOrdines) {
		this.prodottoOrdines = prodottoOrdines;
	}

	public ProdottoOrdine addProdottoOrdine(ProdottoOrdine prodottoOrdine) {
		getProdottoOrdines().add(prodottoOrdine);
		prodottoOrdine.setOrdine(this);

		return prodottoOrdine;
	}

	public ProdottoOrdine removeProdottoOrdine(ProdottoOrdine prodottoOrdine) {
		getProdottoOrdines().remove(prodottoOrdine);
		prodottoOrdine.setOrdine(null);

		return prodottoOrdine;
	}

}