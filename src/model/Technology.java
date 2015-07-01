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

	@Column("match_type")
	private Integer matchType;

	@Column("param_category_code")
	private String paramCategoryCode;

	@Column("technical_code")
	private String technicalCode;

	@Column("technical_name")
	private String technicalName;

	@Column("techonlogy_gategory_code")
	private String techonlogyGategoryCode;

	@Column("is_delete")
	private Integer isDelete = 1;

	public Integer getTechnologyId() {
		return this.technologyId;
	}

	public void setTechnologyId(Integer technologyId) {
		this.technologyId = technologyId;
	}

	public Integer getIsDelete() {
		return this.isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public Integer getMatchType() {
		return this.matchType;
	}

	public void setMatchType(Integer matchType) {
		this.matchType = matchType;
	}

	public String getParamCategoryCode() {
		return this.paramCategoryCode;
	}

	public void setParamCategoryCode(String paramCategoryCode) {
		this.paramCategoryCode = paramCategoryCode;
	}

	public String getTechnicalCode() {
		return this.technicalCode;
	}

	public void setTechnicalCode(String technicalCode) {
		this.technicalCode = technicalCode;
	}

	public String getTechnicalName() {
		return this.technicalName;
	}

	public void setTechnicalName(String technicalName) {
		this.technicalName = technicalName;
	}

	public String getTechonlogyGategoryCode() {
		return this.techonlogyGategoryCode;
	}

	public void setTechonlogyGategoryCode(String techonlogyGategoryCode) {
		this.techonlogyGategoryCode = techonlogyGategoryCode;
	}

}