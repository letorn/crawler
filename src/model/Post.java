package model;

import java.io.Serializable;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_post")
public class Post implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	@Column("technology_param_id")
	private Long technologyParamId;

	@Column("post_code")
	private String postCode;

	@Column("post_name")
	private String postName;

	@Column("post_description")
	private String postDescription;

	@Column("post_category_code")
	private String postCategoryCode;

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

	public Integer getIsDelete() {
		return this.isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public String getPostCategoryCode() {
		return this.postCategoryCode;
	}

	public void setPostCategoryCode(String postCategoryCode) {
		this.postCategoryCode = postCategoryCode;
	}

	public String getPostCode() {
		return this.postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getPostDescription() {
		return this.postDescription;
	}

	public void setPostDescription(String postDescription) {
		this.postDescription = postDescription;
	}

	public String getPostName() {
		return this.postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getTechnologyParamId() {
		return this.technologyParamId;
	}

	public void setTechnologyParamId(Long technologyParamId) {
		this.technologyParamId = technologyParamId;
	}

}