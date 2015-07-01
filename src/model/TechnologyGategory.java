package model;

import java.io.Serializable;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_technology_gategory")
public class TechnologyGategory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	@Column
	private String code;

	@Column
	private String name;

	@Column("parent_code")
	private Integer parentCode;

	@Column
	private Integer percent;

	@Column("technology_gategory_code")
	private String technologyGategoryCode;

	@Column("technology_gategory_name")
	private String technologyGategoryName;

	@Column
	private String remark;

	@Column("is_delete")
	private Integer isDelete = 1;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getIsDelete() {
		return this.isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getParentCode() {
		return this.parentCode;
	}

	public void setParentCode(Integer parentCode) {
		this.parentCode = parentCode;
	}

	public Integer getPercent() {
		return this.percent;
	}

	public void setPercent(Integer percent) {
		this.percent = percent;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTechnologyGategoryCode() {
		return this.technologyGategoryCode;
	}

	public void setTechnologyGategoryCode(String technologyGategoryCode) {
		this.technologyGategoryCode = technologyGategoryCode;
	}

	public String getTechnologyGategoryName() {
		return this.technologyGategoryName;
	}

	public void setTechnologyGategoryName(String technologyGategoryName) {
		this.technologyGategoryName = technologyGategoryName;
	}

}