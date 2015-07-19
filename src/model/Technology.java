package model;

import java.io.Serializable;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_technology")
public class Technology implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id("technology_id")
	private Integer technologyId;

	@Column("technical_code")
	private String technicalCode;

	@Column("match_type")
	private Integer matchType;

	@Column("technical_name")
	private String technicalName;

	@Column("param_category_code")
	private String paramCategoryCode;

	@Column("techonlogy_gategory_code")
	private String technologyGategoryCode;

	@Column("is_delete")
	private Integer isDelete = 1;

	public Integer getTechnologyId() {
		return technologyId;
	}

	public void setTechnologyId(Integer technologyId) {
		this.technologyId = technologyId;
	}

	public String getTechnicalCode() {
		return technicalCode;
	}

	public void setTechnicalCode(String technicalCode) {
		this.technicalCode = technicalCode;
	}

	public Integer getMatchType() {
		return matchType;
	}

	public void setMatchType(Integer matchType) {
		this.matchType = matchType;
	}

	public String getTechnicalName() {
		return technicalName;
	}

	public void setTechnicalName(String technicalName) {
		this.technicalName = technicalName;
	}

	public String getParamCategoryCode() {
		return paramCategoryCode;
	}

	public void setParamCategoryCode(String paramCategoryCode) {
		this.paramCategoryCode = paramCategoryCode;
	}

	public String getTechnologyGategoryCode() {
		return technologyGategoryCode;
	}

	public void setTechnologyGategoryCode(String technologyGategoryCode) {
		this.technologyGategoryCode = technologyGategoryCode;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

}