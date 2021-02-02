package bld.commons.persistence.base.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
public class DateBaseEntity extends BaseEntity {

	@Column(name = "update_by", length = 255)
	@NotNull
	@LastModifiedBy
	private String updateBy;
	@Column(name = "update_on")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "MM")
	@LastModifiedDate
	private Calendar updateOn;

}
