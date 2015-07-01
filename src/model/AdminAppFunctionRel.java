package model;

import java.io.Serializable;
import java.util.Date;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_admin_app_function_rel")
public class AdminAppFunctionRel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column("create_date")
	private Date createDate;

	@Column("function_code")
	private String functionCode;

	@Column("information_cover_id")
	private Long informationCoverId;

	@Column("information_id")
	private Long informationId;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getFunctionCode() {
		return this.functionCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public Long getInformationCoverId() {
		return this.informationCoverId;
	}

	public void setInformationCoverId(Long informationCoverId) {
		this.informationCoverId = informationCoverId;
	}

	public Long getInformationId() {
		return this.informationId;
	}

	public void setInformationId(Long informationId) {
		this.informationId = informationId;
	}

}