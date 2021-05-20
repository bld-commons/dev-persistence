package com.bld.persistence.filter;

import bld.commons.reflection.annotations.LikeString;
import bld.commons.reflection.model.FilterParameter;

public class ConfiguraMenuFilter extends FilterParameter {

	
	private Integer quantita;
	
	@LikeString
	private String desGenere;
	
	@LikeString
	private String desGenereProdotto;
	
	

	public ConfiguraMenuFilter(Integer quantita, String desGenere, String desGenereProdotto) {
		super();
		this.quantita = quantita;
		this.desGenere = desGenere;
		this.desGenereProdotto = desGenereProdotto;
	}

	public ConfiguraMenuFilter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getQuantita() {
		return quantita;
	}

	public void setQuantita(Integer quantita) {
		this.quantita = quantita;
	}

	public String getDesGenere() {
		return desGenere;
	}

	public void setDesGenere(String desGenere) {
		this.desGenere = desGenere;
	}

	public String getDesGenereProdotto() {
		return desGenereProdotto;
	}

	public void setDesGenereProdotto(String desGenereProdotto) {
		this.desGenereProdotto = desGenereProdotto;
	}
	
	
	
	
	
	
}
