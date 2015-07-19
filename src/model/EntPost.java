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

	@Column("expire_date")
	private Date expireDate;

	@Column
	private Integer headcounts;

	@Column("lbs_id")
	private Long lbsId;

	@Column
	private String parea;

	@Column("pjob_category")
	private String pjobCategory;

	@Column("post_address")
	private String postAddress;

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

	@Column
	private String welfare;

	@Column("post_aliases")
	private String postAliases;

	@Column("welfare_selected")
	private String welfareSelected;

	@Column
	private String pmajor;

	@Column("admin_account")
	private String adminAccount;

	@Column("is_several")
	private Integer isSeveral;

	@Column("end_time")
	private Date endTime;

	@Column("start_time")
	private Date startTime;

	@Column("tag_selected")
	private String tagSelected;

	@Column("salary_type")
	private Integer salaryType;

	@Column("week_day")
	private String weekDay;

	@Column("update_date")
	private Date updateDate;

	@Column("data_src")
	private String dataSrc;

	@Column("data_url")
	private String dataUrl;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public Long getEntId() {
		return entId;
	}

	public void setEntId(Long entId) {
		this.entId = entId;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public Integer getHeadcounts() {
		return headcounts;
	}

	public void setHeadcounts(Integer headcounts) {
		this.headcounts = headcounts;
	}

	public Long getLbsId() {
		return lbsId;
	}

	public void setLbsId(Long lbsId) {
		this.lbsId = lbsId;
	}

	public String getParea() {
		return parea;
	}

	public void setParea(String parea) {
		this.parea = parea;
	}

	public String getPjobCategory() {
		return pjobCategory;
	}

	public void setPjobCategory(String pjobCategory) {
		this.pjobCategory = pjobCategory;
	}

	public String getPostAddress() {
		return postAddress;
	}

	public void setPostAddress(String postAddress) {
		this.postAddress = postAddress;
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

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public String getPostRemark() {
		return postRemark;
	}

	public void setPostRemark(String postRemark) {
		this.postRemark = postRemark;
	}

	public String getPsalary() {
		return psalary;
	}

	public void setPsalary(String psalary) {
		this.psalary = psalary;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getWelfare() {
		return welfare;
	}

	public void setWelfare(String welfare) {
		this.welfare = welfare;
	}

	public String getPostAliases() {
		return postAliases;
	}

	public void setPostAliases(String postAliases) {
		this.postAliases = postAliases;
	}

	public String getWelfareSelected() {
		return welfareSelected;
	}

	public void setWelfareSelected(String welfareSelected) {
		this.welfareSelected = welfareSelected;
	}

	public String getPmajor() {
		return pmajor;
	}

	public void setPmajor(String pmajor) {
		this.pmajor = pmajor;
	}

	public String getAdminAccount() {
		return adminAccount;
	}

	public void setAdminAccount(String adminAccount) {
		this.adminAccount = adminAccount;
	}

	public Integer getIsSeveral() {
		return isSeveral;
	}

	public void setIsSeveral(Integer isSeveral) {
		this.isSeveral = isSeveral;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getTagSelected() {
		return tagSelected;
	}

	public void setTagSelected(String tagSelected) {
		this.tagSelected = tagSelected;
	}

	public Integer getSalaryType() {
		return salaryType;
	}

	public void setSalaryType(Integer salaryType) {
		this.salaryType = salaryType;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getDataSrc() {
		return dataSrc;
	}

	public void setDataSrc(String dataSrc) {
		this.dataSrc = dataSrc;
	}

	public String getDataUrl() {
		return dataUrl;
	}

	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}

}