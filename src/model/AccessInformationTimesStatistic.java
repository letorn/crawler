package model;

import java.io.Serializable;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_access_information_times_statistics")
public class AccessInformationTimesStatistic implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Column("information_id")
	private Long informationId;

	@Column("access_times")
	private Integer accessTimes;

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

	public Integer getAccessTimes() {
		return this.accessTimes;
	}

	public void setAccessTimes(Integer accessTimes) {
		this.accessTimes = accessTimes;
	}

}