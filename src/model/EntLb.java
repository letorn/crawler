package model;

import java.io.Serializable;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_ent_lbs")
public class EntLb implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id("lbs_id")
	private String lbsId;

	@Column("latitude")
	private Double latitude;

	@Column("longitude")
	private Double longitude;

	@Column("remark")
	private String remark;

	public String getLbsId() {
		return this.lbsId;
	}

	public void setLbsId(String lbsId) {
		this.lbsId = lbsId;
	}

	public Double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}