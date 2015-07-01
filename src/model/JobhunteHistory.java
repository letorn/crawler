package model;

import java.io.Serializable;
import java.util.Date;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_jobhunte_history")
public class JobhunteHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id("hist_id")
	private String histId;

	@Column("user_id")
	private Long userId;

	@Column("post_name")
	private String postName;

	@Column
	private String parea;

	@Column("create_date")
	private Date createDate;

	public String getHistId() {
		return this.histId;
	}

	public void setHistId(String histId) {
		this.histId = histId;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getParea() {
		return this.parea;
	}

	public void setParea(String parea) {
		this.parea = parea;
	}

	public String getPostName() {
		return this.postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}