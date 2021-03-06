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

	@Column("post_category_code")
	private String postCategoryCode;

	@Column("post_code")
	private String postCode;

	@Column("post_description")
	private String postDescription;

	@Column("post_name")
	private String postName;

	@Column
	private String remark;

	@Column("technology_param_id")
	private Long technologyParamId;

	@Column("is_delete")
	private Integer isDelete = 1;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPostCategoryCode() {
		return postCategoryCode;
	}

	public void setPostCategoryCode(String postCategoryCode) {
		this.postCategoryCode = postCategoryCode;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getPostDescription() {
		return postDescription;
	}

	public void setPostDescription(String postDescription) {
		this.postDescription = postDescription;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getTechnologyParamId() {
		return technologyParamId;
	}

	public void setTechnologyParamId(Long technologyParamId) {
		this.technologyParamId = technologyParamId;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

}