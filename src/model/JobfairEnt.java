package model;

import java.io.Serializable;
import java.util.Date;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_jobfair_ent")
public class JobfairEnt implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column("ent_id")
	private Long entId;

	@Column("fair_id")
	private Long fairId;

	@Column("audit_status")
	private Integer auditStatus;

	@Column("booth_no")
	private String boothNo;

	@Column("is_sign_up")
	private Integer isSignUp;

	@Column("create_time")
	private Date createTime;

	@Column
	private String remark;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getEntId() {
		return entId;
	}

	public void setEntId(Long entId) {
		this.entId = entId;
	}

	public Long getFairId() {
		return fairId;
	}

	public void setFairId(Long fairId) {
		this.fairId = fairId;
	}

	public Integer getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getBoothNo() {
		return boothNo;
	}

	public void setBoothNo(String boothNo) {
		this.boothNo = boothNo;
	}

	public Integer getIsSignUp() {
		return isSignUp;
	}

	public void setIsSignUp(Integer isSignUp) {
		this.isSignUp = isSignUp;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}