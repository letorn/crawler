package model;

import java.io.Serializable;
import java.util.Date;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_validate")
public class Validate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	@Column
	private String account;

	@Column("create_time")
	private Date createTime;

	@Column("flag_cateGory")
	private Integer flagCateGory;

	@Column("user_CateGoryFlag")
	private String userCateGoryFlag;

	@Column("validate_code")
	private String validateCode;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getFlagCateGory() {
		return this.flagCateGory;
	}

	public void setFlagCateGory(Integer flagCateGory) {
		this.flagCateGory = flagCateGory;
	}

	public String getUserCateGoryFlag() {
		return this.userCateGoryFlag;
	}

	public void setUserCateGoryFlag(String userCateGoryFlag) {
		this.userCateGoryFlag = userCateGoryFlag;
	}

	public String getValidateCode() {
		return this.validateCode;
	}

	public void setValidateCode(String validateCode) {
		this.validateCode = validateCode;
	}

}