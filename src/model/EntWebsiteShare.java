package model;

import java.io.Serializable;
import java.util.Date;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_ent_website_share")
public class EntWebsiteShare implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column("user_id")
	private Long userId;

	@Column("ent_id")
	private Long entId;

	@Column("share_forward")
	private String shareForward;

	@Column("share_type")
	private String shareType;

	@Column("create_date")
	private Date createDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getEntId() {
		return entId;
	}

	public void setEntId(Long entId) {
		this.entId = entId;
	}

	public String getShareForward() {
		return shareForward;
	}

	public void setShareForward(String shareForward) {
		this.shareForward = shareForward;
	}

	public String getShareType() {
		return shareType;
	}

	public void setShareType(String shareType) {
		this.shareType = shareType;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}