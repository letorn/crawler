package model;

import java.io.Serializable;
import java.util.Date;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_admin_call_jobhunte")
public class AdminCallJobhunte implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column("admin_id")
	private Long adminId;

	@Column("call_date")
	private Date callDate;

	@Column("jobhunte_id")
	private Long jobhunteId;

	@Column
	private String remark;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getAdminId() {
		return this.adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public Date getCallDate() {
		return this.callDate;
	}

	public void setCallDate(Date callDate) {
		this.callDate = callDate;
	}

	public Long getJobhunteId() {
		return this.jobhunteId;
	}

	public void setJobhunteId(Long jobhunteId) {
		this.jobhunteId = jobhunteId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}