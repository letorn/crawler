package model;

import java.io.Serializable;
import java.util.Date;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_access_times_information_detail")
public class AccessTimesInformationDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Column("information_id")
	private Long informationId;

	@Column("user_id")
	private Long userId;

	@Column("access_time")
	private Date accessTime;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInformationId() {
		return this.informationId;
	}

	public void setInformationId(Long informationId) {
		this.informationId = informationId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getAccessTime() {
		return this.accessTime;
	}

	public void setAccessTime(Date accessTime) {
		this.accessTime = accessTime;
	}

}