package com.bld.persistence.core.domain;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;


/**
 * The persistent class for the calendario_speedy database table.
 * 
 */
@Entity
@Table(name="calendario_speedy")
@NamedQuery(name="CalendarioSpeedy.findAll", query="SELECT c FROM CalendarioSpeedy c")
public class CalendarioSpeedy implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CALENDARIO_SPEEDY_IDCALENDARIOSPEEDY_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CALENDARIO_SPEEDY_IDCALENDARIOSPEEDY_GENERATOR")
	@Column(name="id_calendario_speedy", unique=true, nullable=false)
	private Long idCalendarioSpeedy;

	@Temporal(TemporalType.DATE)
	@Column(name="data_lavoro")
	private Date dataLavoro;

	//bi-directional many-to-one association to Speedy
	@NotNull
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_speedy")
	private Speedy speedy;

	//bi-directional many-to-one association to Ordine
	@OneToMany(mappedBy="calendarioSpeedy")
	private Set<Ordine> ordines;

	public CalendarioSpeedy() {
	}

	public Long getIdCalendarioSpeedy() {
		return this.idCalendarioSpeedy;
	}

	public void setIdCalendarioSpeedy(Long idCalendarioSpeedy) {
		this.idCalendarioSpeedy = idCalendarioSpeedy;
	}

	public Date getDataLavoro() {
		return this.dataLavoro;
	}

	public void setDataLavoro(Date dataLavoro) {
		this.dataLavoro = dataLavoro;
	}

	public Speedy getSpeedy() {
		return this.speedy;
	}

	public void setSpeedy(Speedy speedy) {
		this.speedy = speedy;
	}

	public Set<Ordine> getOrdines() {
		return this.ordines;
	}

	public void setOrdines(Set<Ordine> ordines) {
		this.ordines = ordines;
	}

	public Ordine addOrdine(Ordine ordine) {
		getOrdines().add(ordine);
		ordine.setCalendarioSpeedy(this);

		return ordine;
	}

	public Ordine removeOrdine(Ordine ordine) {
		getOrdines().remove(ordine);
		ordine.setCalendarioSpeedy(null);

		return ordine;
	}

}