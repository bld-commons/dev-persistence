package com.bld.persistence.core.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;


/**
 * The persistent class for the prodotto database table.
 * 
 */
@Entity
@Table(name="prodotto")
@NamedQuery(name="Prodotto.findAll", query="SELECT p FROM Prodotto p")
public class Prodotto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="PRODOTTO_IDPRODOTTO_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PRODOTTO_IDPRODOTTO_GENERATOR")
	@Column(name="id_prodotto", unique=true, nullable=false)
	private Long idProdotto;

	@Column(name="create_timestamp", nullable=false)
	private Timestamp createTimestamp;

	@Column(name="create_user", nullable=false, length=255)
	private String createUser;

	@Column(name="des_prodotto", length=255)
	private String desProdotto;

	@Column(name="flag_valido", nullable=false)
	private Boolean flagValido;

	@Column(nullable=false)
	private double prezzo;

	@Column(name="update_timestamp", nullable=false)
	private Timestamp updateTimestamp;

	@Column(name="update_user", nullable=false, length=255)
	private String updateUser;

	private Long version;

	//bi-directional many-to-one association to ConfiguraMenu
	@OneToMany(mappedBy="prodotto")
	private Set<ConfiguraMenu> configuraMenus1;

	//bi-directional many-to-one association to MenuOrdineProdotto
	@OneToMany(mappedBy="prodotto1")
	private Set<MenuOrdineProdotto> menuOrdineProdottos1;

	//bi-directional many-to-one association to MenuOrdineProdotto
	@OneToMany(mappedBy="prodotto2")
	private Set<MenuOrdineProdotto> menuOrdineProdottos2;

	//bi-directional many-to-many association to ConfiguraMenu
	@ManyToMany
	@JoinTable(
		name="opzione_menu_prodotto"
		, joinColumns={
			@JoinColumn(name="id_prodotto", nullable=false)
			}
		, inverseJoinColumns={
			@JoinColumn(name="id_configura_menu", nullable=false)
			}
		)
	private Set<ConfiguraMenu> configuraMenus2;

	//bi-directional many-to-one association to Genere
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_genere", nullable=false)
	private Genere genere;

	//bi-directional many-to-one association to ProdottoOrdine
	@OneToMany(mappedBy="prodotto")
	private Set<ProdottoOrdine> prodottoOrdines;

	public Prodotto() {
	}

	public Long getIdProdotto() {
		return this.idProdotto;
	}

	public void setIdProdotto(Long idProdotto) {
		this.idProdotto = idProdotto;
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

	public String getDesProdotto() {
		return this.desProdotto;
	}

	public void setDesProdotto(String desProdotto) {
		this.desProdotto = desProdotto;
	}

	public Boolean getFlagValido() {
		return this.flagValido;
	}

	public void setFlagValido(Boolean flagValido) {
		this.flagValido = flagValido;
	}

	public double getPrezzo() {
		return this.prezzo;
	}

	public void setPrezzo(double prezzo) {
		this.prezzo = prezzo;
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

	public Set<ConfiguraMenu> getConfiguraMenus1() {
		return this.configuraMenus1;
	}

	public void setConfiguraMenus1(Set<ConfiguraMenu> configuraMenus1) {
		this.configuraMenus1 = configuraMenus1;
	}

	public ConfiguraMenu addConfiguraMenus1(ConfiguraMenu configuraMenus1) {
		getConfiguraMenus1().add(configuraMenus1);
		configuraMenus1.setProdotto(this);

		return configuraMenus1;
	}

	public ConfiguraMenu removeConfiguraMenus1(ConfiguraMenu configuraMenus1) {
		getConfiguraMenus1().remove(configuraMenus1);
		configuraMenus1.setProdotto(null);

		return configuraMenus1;
	}

	public Set<MenuOrdineProdotto> getMenuOrdineProdottos1() {
		return this.menuOrdineProdottos1;
	}

	public void setMenuOrdineProdottos1(Set<MenuOrdineProdotto> menuOrdineProdottos1) {
		this.menuOrdineProdottos1 = menuOrdineProdottos1;
	}

	public MenuOrdineProdotto addMenuOrdineProdottos1(MenuOrdineProdotto menuOrdineProdottos1) {
		getMenuOrdineProdottos1().add(menuOrdineProdottos1);
		menuOrdineProdottos1.setProdotto1(this);

		return menuOrdineProdottos1;
	}

	public MenuOrdineProdotto removeMenuOrdineProdottos1(MenuOrdineProdotto menuOrdineProdottos1) {
		getMenuOrdineProdottos1().remove(menuOrdineProdottos1);
		menuOrdineProdottos1.setProdotto1(null);

		return menuOrdineProdottos1;
	}

	public Set<MenuOrdineProdotto> getMenuOrdineProdottos2() {
		return this.menuOrdineProdottos2;
	}

	public void setMenuOrdineProdottos2(Set<MenuOrdineProdotto> menuOrdineProdottos2) {
		this.menuOrdineProdottos2 = menuOrdineProdottos2;
	}

	public MenuOrdineProdotto addMenuOrdineProdottos2(MenuOrdineProdotto menuOrdineProdottos2) {
		getMenuOrdineProdottos2().add(menuOrdineProdottos2);
		menuOrdineProdottos2.setProdotto2(this);

		return menuOrdineProdottos2;
	}

	public MenuOrdineProdotto removeMenuOrdineProdottos2(MenuOrdineProdotto menuOrdineProdottos2) {
		getMenuOrdineProdottos2().remove(menuOrdineProdottos2);
		menuOrdineProdottos2.setProdotto2(null);

		return menuOrdineProdottos2;
	}

	public Set<ConfiguraMenu> getConfiguraMenus2() {
		return this.configuraMenus2;
	}

	public void setConfiguraMenus2(Set<ConfiguraMenu> configuraMenus2) {
		this.configuraMenus2 = configuraMenus2;
	}

	public Genere getGenere() {
		return this.genere;
	}

	public void setGenere(Genere genere) {
		this.genere = genere;
	}

	public Set<ProdottoOrdine> getProdottoOrdines() {
		return this.prodottoOrdines;
	}

	public void setProdottoOrdines(Set<ProdottoOrdine> prodottoOrdines) {
		this.prodottoOrdines = prodottoOrdines;
	}

	public ProdottoOrdine addProdottoOrdine(ProdottoOrdine prodottoOrdine) {
		getProdottoOrdines().add(prodottoOrdine);
		prodottoOrdine.setProdotto(this);

		return prodottoOrdine;
	}

	public ProdottoOrdine removeProdottoOrdine(ProdottoOrdine prodottoOrdine) {
		getProdottoOrdines().remove(prodottoOrdine);
		prodottoOrdine.setProdotto(null);

		return prodottoOrdine;
	}

}