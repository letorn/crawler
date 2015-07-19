package model;

import java.io.Serializable;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_category_post")
public class CategoryPost implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	@Column("post_category_code")
	private String postCategoryCode;

	@Column("post_category_name")
	private String postCategoryName;

	@Column("parent_code")
	private String parentCode;

	@Column
	private String remark;

	@Column
	private String orders;

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

	public String getPostCategoryName() {
		return postCategoryName;
	}

	public void setPostCategoryName(String postCategoryName) {
		this.postCategoryName = postCategoryName;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOrders() {
		return orders;
	}

	public void setOrders(String orders) {
		this.orders = orders;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

}