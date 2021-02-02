/**************************************************************************
 * 
 * Copyright 2019 (C) DXC Technology
 * 
 * Author      : DXC Technology
 * Project Name: pmg-persistence-common
 * Package     : com.bld.pmg.persistence.domain
 * File Name   : BaseEntity.java
 *
 ***************************************************************************/
package bld.commons.persistence.base.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 * The Class BaseEntity.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public class BaseEntity {

	@Column(name = "created_by", length = 255)
	@NotNull
	@CreatedBy
	private String createdBy;
	@Column(name = "created_on")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "MM")
	@CreatedDate
	private Calendar createdOn;

}
