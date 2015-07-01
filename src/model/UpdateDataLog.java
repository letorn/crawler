package model;

import java.io.Serializable;
import java.util.Date;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_update_data_log")
public class UpdateDataLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column("admin_id")
	private Long adminId;

	@Column("user_id")
	private Long userId;

	@Column("ent_id")
	private Long entId;

	@Column("update_content")
	private String updateContent;

	@Column("update_table_name")
	private String updateTableName;

	@Column("update_time")
	private Date updateTime;

	@Column("update_type")
	private Integer updateType;

	@Column
	private String remark;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getAdminId() {
		return this.adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public Long getEntId() {
		return this.entId;
	}

	public void setEntId(Long entId) {
		this.entId = entId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUpdateContent() {
		return this.updateContent;
	}

	public void setUpdateContent(String updateContent) {
		this.updateContent = updateContent;
	}

	public String getUpdateTableName() {
		return this.updateTableName;
	}

	public void setUpdateTableName(String updateTableName) {
		this.updateTableName = updateTableName;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getUpdateType() {
		return this.updateType;
	}

	public void setUpdateType(Integer updateType) {
		this.updateType = updateType;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}