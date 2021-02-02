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

// TODO: Auto-generated Javadoc
/**
 * The Class DateBaseEntity.
 */
@MappedSuperclass
public class DateBaseEntity extends BaseEntity {

	/** The update by. */
	@Column(name = "update_by", length = 255)
	@NotNull
	@LastModifiedBy
	private String updateBy;
	
	/** The update on. */
	@Column(name = "update_on")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "MM")
	@LastModifiedDate
	private Calendar updateOn;

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Calendar getUpdateOn() {
		return updateOn;
	}

	public void setUpdateOn(Calendar updateOn) {
		this.updateOn = updateOn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((updateBy == null) ? 0 : updateBy.hashCode());
		result = prime * result + ((updateOn == null) ? 0 : updateOn.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DateBaseEntity other = (DateBaseEntity) obj;
		if (updateBy == null) {
			if (other.updateBy != null)
				return false;
		} else if (!updateBy.equals(other.updateBy))
			return false;
		if (updateOn == null) {
			if (other.updateOn != null)
				return false;
		} else if (!updateOn.equals(other.updateOn))
			return false;
		return true;
	}

	
	
	
}
