package model;

import java.io.Serializable;
import java.util.Date;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_ent_post")
public class EntPost implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Column("depart_id")
	private Long departId;

	@Column("ent_id")
	private Long entId;

	@Column("admin_account")
	private String adminAccount;

	@Column("end_time")
	private Date endTime;

	@Column("expire_date")
	private Date expireDate;

	@Column
	private Integer headcounts;

	@Column("is_several")
	private Integer isSeveral;

	@Column("lbs_id")
	private Long lbsId;

	@Column
	private String parea;

	@Column("pjob_category")
	private String pjobCategory;

	@Column
	private String pmajor;

	@Column("post_address")
	private String postAddress;

	@Column("post_aliases")
	private String postAliases;

	@Column("post_category_code")
	private String postCategoryCode;

	@Column("post_code")
	private String postCode;

	@Column("post_name")
	private String postName;

	@Column("post_remark")
	private String postRemark;

	@Column
	private String psalary;

	@Column("publish_date")
	private Date publishDate;

	@Column
	private String remark;

	@Column("salary_type")
	private Integer salaryType;

	@Column("start_time")
	private Date startTime;

	@Column("tag_selected")
	private String tagSelected;

	@Column("update_date")
	private Date updateDate;

	@Column("week_day")
	private String weekDay;

	@Column
	private String welfare;

	@Column("welfare_selected")
	private String welfareSelected;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAdminAccount() {
		return this.adminAccount;
	}

	public void setAdminAccount(String adminAccount) {
		this.adminAccount = adminAccount;
	}

	public Long getDepartId() {
		return this.departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Long getEntId() {
		return this.entId;
	}

	public void setEntId(Long entId) {
		this.entId = entId;
	}

	public Date getExpireDate() {
		return this.expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public Integer getHeadcounts() {
		return this.headcounts;
	}

	public void setHeadcounts(Integer headcounts) {
		this.headcounts = headcounts;
	}

	public Integer getIsSeveral() {
		return this.isSeveral;
	}

	public void setIsSeveral(Integer isSeveral) {
		this.isSeveral = isSeveral;
	}

	public Long getLbsId() {
		return this.lbsId;
	}

	public void setLbsId(Long lbsId) {
		this.lbsId = lbsId;
	}

	public String getParea() {
		return this.parea;
	}

	public void setParea(String parea) {
		this.parea = parea;
	}

	public String getPjobCategory() {
		return this.pjobCategory;
	}

	public void setPjobCategory(String pjobCategory) {
		this.pjobCategory = pjobCategory;
	}

	public String getPmajor() {
		return this.pmajor;
	}

	public void setPmajor(String pmajor) {
		this.pmajor = pmajor;
	}

	public String getPostAddress() {
		return this.postAddress;
	}

	public void setPostAddress(String postAddress) {
		this.postAddress = postAddress;
	}

	public String getPostAliases() {
		return this.postAliases;
	}

	public void setPostAliases(String postAliases) {
		this.postAliases = postAliases;
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

	public String getPostName() {
		return this.postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public String getPostRemark() {
		return this.postRemark;
	}

	public void setPostRemark(String postRemark) {
		this.postRemark = postRemark;
	}

	public String getPsalary() {
		return this.psalary;
	}

	public void setPsalary(String psalary) {
		this.psalary = psalary;
	}

	public Date getPublishDate() {
		return this.publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getSalaryType() {
		return this.salaryType;
	}

	public void setSalaryType(Integer salaryType) {
		this.salaryType = salaryType;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getTagSelected() {
		return this.tagSelected;
	}

	public void setTagSelected(String tagSelected) {
		this.tagSelected = tagSelected;
	}

	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getWeekDay() {
		return this.weekDay;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public String getWelfare() {
		return this.welfare;
	}

	public void setWelfare(String welfare) {
		this.welfare = welfare;
	}

	public String getWelfareSelected() {
		return this.welfareSelected;
	}

	public void setWelfareSelected(String welfareSelected) {
		this.welfareSelected = welfareSelected;
	}

}