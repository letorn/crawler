package model;

import java.io.Serializable;
import java.util.Date;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_ent_account")
public class EntAccount implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id("account_id")
	private Long accountId;

	@Column
	private String account;

	@Column("create_date")
	private Date createDate;

	@Column("ent_id")
	private Long entId;

	@Column
	private String pwd;

	@Column
	private Integer status;

	@Column
	private String remark;

	@Column("create_mode")
	private Integer createMode;

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getEntId() {
		return entId;
	}

	public void setEntId(Long entId) {
		this.entId = entId;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getCreateMode() {
		return createMode;
	}

	public void setCreateMode(Integer createMode) {
		this.createMode = createMode;
	}

}