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

	@Column("technology_gategory_code")
	private String technologyGategoryCode;

	@Column("technology_gategory_name")
	private String technologyGategoryName;

	@Column("parent_code")
	private Integer parentCode;

	@Column
	private Integer percent;

	@Column
	private String remark;

	@Column("is_delete")
	private Integer isDelete = 1;

	@Column
	private String code;

	@Column
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTechnologyGategoryCode() {
		return technologyGategoryCode;
	}

	public void setTechnologyGategoryCode(String technologyGategoryCode) {
		this.technologyGategoryCode = technologyGategoryCode;
	}

	public String getTechnologyGategoryName() {
		return technologyGategoryName;
	}

	public void setTechnologyGategoryName(String technologyGategoryName) {
		this.technologyGategoryName = technologyGategoryName;
	}

	public Integer getParentCode() {
		return parentCode;
	}

	public void setParentCode(Integer parentCode) {
		this.parentCode = parentCode;
	}

	public Integer getPercent() {
		return percent;
	}

	public void setPercent(Integer percent) {
		this.percent = percent;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}