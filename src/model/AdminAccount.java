package model;

import java.io.Serializable;
import java.util.Date;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_admin_account")
public class AdminAccount implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id("account_id")
	private Long accountId;

	@Column("admin_id")
	private Long adminId;

	@Column
	private String account;

	@Column
	private String pwd;

	@Column
	private Integer status;

	@Column("custom_server")
	private Integer customServer;

	@Column("create_date")
	private Date createDate;

	@Column
	private String remark;

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
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

	public Integer getCustomServer() {
		return customServer;
	}

	public void setCustomServer(Integer customServer) {
		this.customServer = customServer;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}