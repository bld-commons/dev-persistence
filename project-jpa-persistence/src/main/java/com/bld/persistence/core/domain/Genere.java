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
 * The persistent class for the genere database table.
 * 
 */
@Entity
@Table(name="genere")
@NamedQuery(name="Genere.findAll", query="SELECT g FROM Genere g")
public class Genere implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="GENERE_IDGENERE_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="GENERE_IDGENERE_GENERATOR")
	@Column(name="id_genere", unique=true, nullable=false)
	private Long idGenere;

	@Column(name="create_timestamp", nullable=false)
	private Timestamp createTimestamp;

	@Column(name="create_user", nullable=false, length=255)
	private String createUser;

	@Column(name="des_genere", nullable=false, length=255)
	private String desGenere;

	@Column(name="flag_valido", nullable=false)
	private Boolean flagValido;

	@Column(name="update_timestamp", nullable=false)
	private Timestamp updateTimestamp;

	@Column(name="update_user", nullable=false, length=255)
	private String updateUser;

	private Long version;

	//bi-directional many-to-one association to ConfiguraMenu
	@OneToMany(mappedBy="genere")
	private Set<ConfiguraMenu> configuraMenus;

	//bi-directional many-to-one association to PostazioneCucina
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_postazione_cucina", nullable=false)
	private PostazioneCucina postazioneCucina;

	//bi-directional many-to-one association to Prodotto
	@OneToMany(mappedBy="genere")
	private Set<Prodotto> prodottos;

	public Genere() {
	}

	public Long getIdGenere() {
		return this.idGenere;
	}

	public void setIdGenere(Long idGenere) {
		this.idGenere = idGenere;
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

	public String getDesGenere() {
		return this.desGenere;
	}

	public void setDesGenere(String desGenere) {
		this.desGenere = desGenere;
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

	public Set<ConfiguraMenu> getConfiguraMenus() {
		return this.configuraMenus;
	}

	public void setConfiguraMenus(Set<ConfiguraMenu> configuraMenus) {
		this.configuraMenus = configuraMenus;
	}

	public ConfiguraMenu addConfiguraMenus(ConfiguraMenu configuraMenus) {
		getConfiguraMenus().add(configuraMenus);
		configuraMenus.setGenere(this);

		return configuraMenus;
	}

	public ConfiguraMenu removeConfiguraMenus(ConfiguraMenu configuraMenus) {
		getConfiguraMenus().remove(configuraMenus);
		configuraMenus.setGenere(null);

		return configuraMenus;
	}

	public PostazioneCucina getPostazioneCucina() {
		return this.postazioneCucina;
	}

	public void setPostazioneCucina(PostazioneCucina postazioneCucina) {
		this.postazioneCucina = postazioneCucina;
	}

	public Set<Prodotto> getProdottos() {
		return this.prodottos;
	}

	public void setProdottos(Set<Prodotto> prodottos) {
		this.prodottos = prodottos;
	}

	public Prodotto addProdotto(Prodotto prodotto) {
		getProdottos().add(prodotto);
		prodotto.setGenere(this);

		return prodotto;
	}

	public Prodotto removeProdotto(Prodotto prodotto) {
		getProdottos().remove(prodotto);
		prodotto.setGenere(null);

		return prodotto;
	}

}