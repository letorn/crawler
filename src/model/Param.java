package model;

import java.io.Serializable;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_param")
public class Param implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	@Column("param_category_code")
	private String paramCategoryCode;

	@Column("param_code")
	private String paramCode;

	@Column("param_name")
	private String paramName;

	@Column("param_value")
	private Integer paramValue;

	@Column
	private String remark;

	@Column("is_delete")
	private Integer isDelete = 1;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getParamCategoryCode() {
		return paramCategoryCode;
	}

	public void setParamCategoryCode(String paramCategoryCode) {
		this.paramCategoryCode = paramCategoryCode;
	}

	public String getParamCode() {
		return paramCode;
	}

	public void setParamCode(String paramCode) {
		this.paramCode = paramCode;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public Integer getParamValue() {
		return paramValue;
	}

	public void setParamValue(Integer paramValue) {
		this.paramValue = paramValue;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

}