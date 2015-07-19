package model;

import java.io.Serializable;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_ent_post_status")
public class EntPostStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id("ps_id")
	private Long psId;

	@Column
	private Integer employ;

	@Column
	private Integer employed;

	@Column("post_id")
	private Long postId;

	@Column("post_status")
	private Integer postStatus;

	@Column
	private String remark;

	@Column("un_employ")
	private Integer unemploy;

	@Column("employ_total")
	private Integer employTotal;

	@Column("skim_count")
	private Integer skimCount;

	public Long getPsId() {
		return psId;
	}

	public void setPsId(Long psId) {
		this.psId = psId;
	}

	public Integer getEmploy() {
		return employ;
	}

	public void setEmploy(Integer employ) {
		this.employ = employ;
	}

	public Integer getEmployed() {
		return employed;
	}

	public void setEmployed(Integer employed) {
		this.employed = employed;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public Integer getPostStatus() {
		return postStatus;
	}

	public void setPostStatus(Integer postStatus) {
		this.postStatus = postStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getUnemploy() {
		return unemploy;
	}

	public void setUnemploy(Integer unemploy) {
		this.unemploy = unemploy;
	}

	public Integer getEmployTotal() {
		return employTotal;
	}

	public void setEmployTotal(Integer employTotal) {
		this.employTotal = employTotal;
	}

	public Integer getSkimCount() {
		return skimCount;
	}

	public void setSkimCount(Integer skimCount) {
		this.skimCount = skimCount;
	}

}