package com.bld.persistence.response;

import java.sql.Timestamp;
import java.util.Date;

import lombok.Data;

@Data
public class UtenteResponse {

	
	private Long idUtente;

	private String codFiscale;

	private String cognome;

	private Boolean confermaRegistrazione;

	private Timestamp createTimestamp;

	private String createUser;

	private Date dataNascita;

	private String email;

	private String nome;

	private String password;

	private Timestamp updateTimestamp;

	private String updateUser;

}
