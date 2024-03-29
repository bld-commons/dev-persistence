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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;


/**
 * The persistent class for the speedy database table.
 * 
 */
@Entity
@Table(name="speedy")
@NamedQuery(name="Speedy.findAll", query="SELECT s FROM Speedy s")
public class Speedy implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SPEEDY_IDSPEEDY_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SPEEDY_IDSPEEDY_GENERATOR")
	@Column(name="id_speedy", unique=true, nullable=false)
	private Long idSpeedy;

	@Column(name="create_timestamp", nullable=false)
	private Timestamp createTimestamp;

	@Column(name="create_user", nullable=false, length=255)
	private String createUser;

	@Column(name="flag_valido", nullable=false)
	private Boolean flagValido;

	@Column(length=50)
	private String numero;

	@Column(name="update_timestamp", nullable=false)
	private Timestamp updateTimestamp;

	@Column(name="update_user", nullable=false, length=255)
	private String updateUser;

	private Long version;

	//bi-directional many-to-one association to CalendarioSpeedy
	@OneToMany(mappedBy="speedy")
	private Set<CalendarioSpeedy> calendarioSpeedies;

	//bi-directional many-to-one association to Ristorante
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_ristorante", nullable=false)
	private Ristorante ristorante;

	//bi-directional many-to-one association to Utente
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_utente", nullable=false)
	private Utente utente;

	public Speedy() {
	}

	public Long getIdSpeedy() {
		return this.idSpeedy;
	}

	public void setIdSpeedy(Long idSpeedy) {
		this.idSpeedy = idSpeedy;
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

	public String getNumero() {
		return this.numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
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

	public Set<CalendarioSpeedy> getCalendarioSpeedies() {
		return this.calendarioSpeedies;
	}

	public void setCalendarioSpeedies(Set<CalendarioSpeedy> calendarioSpeedies) {
		this.calendarioSpeedies = calendarioSpeedies;
	}

	public CalendarioSpeedy addCalendarioSpeedy(CalendarioSpeedy calendarioSpeedy) {
		getCalendarioSpeedies().add(calendarioSpeedy);
		calendarioSpeedy.setSpeedy(this);

		return calendarioSpeedy;
	}

	public CalendarioSpeedy removeCalendarioSpeedy(CalendarioSpeedy calendarioSpeedy) {
		getCalendarioSpeedies().remove(calendarioSpeedy);
		calendarioSpeedy.setSpeedy(null);

		return calendarioSpeedy;
	}

	public Ristorante getRistorante() {
		return this.ristorante;
	}

	public void setRistorante(Ristorante ristorante) {
		this.ristorante = ristorante;
	}

	public Utente getUtente() {
		return this.utente;
	}

	public void setUtente(Utente utente) {
		this.utente = utente;
	}

}