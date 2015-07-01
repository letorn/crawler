package model;

import java.io.Serializable;
import java.util.Date;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_view_ent_post_temp")
public class ViewEntPostTemp implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Column("area_code")
	private String areaCode;

	@Column("employ_num")
	private String employNum;

	@Column("ent_id")
	private Long entId;

	@Column("ent_name")
	private String entName;

	@Column
	private String industry;

	@Column
	private Double lat;

	@Column
	private Double lon;

	@Column("max_salary")
	private Integer maxSalary;

	@Column("min_salary")
	private Integer minSalary;

	@Column("post_aliases")
	private String postAliases;

	@Column("post_code")
	private String postCode;

	@Column("post_id")
	private Long postId;

	@Column
	private String property;

	@Column("publish_date")
	private Date publishDate;

	@Column("salary_code")
	private String salaryCode;

	@Column("tag_selected")
	private String tagSelected;

	public String getAreaCode() {
		return this.areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getEmployNum() {
		return this.employNum;
	}

	public void setEmployNum(String employNum) {
		this.employNum = employNum;
	}

	public Long getEntId() {
		return this.entId;
	}

	public void setEntId(Long entId) {
		this.entId = entId;
	}

	public String getEntName() {
		return this.entName;
	}

	public void setEntName(String entName) {
		this.entName = entName;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIndustry() {
		return this.industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public Double getLat() {
		return this.lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return this.lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Integer getMaxSalary() {
		return this.maxSalary;
	}

	public void setMaxSalary(Integer maxSalary) {
		this.maxSalary = maxSalary;
	}

	public Integer getMinSalary() {
		return this.minSalary;
	}

	public void setMinSalary(Integer minSalary) {
		this.minSalary = minSalary;
	}

	public String getPostAliases() {
		return this.postAliases;
	}

	public void setPostAliases(String postAliases) {
		this.postAliases = postAliases;
	}

	public String getPostCode() {
		return this.postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public Long getPostId() {
		return this.postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public String getProperty() {
		return this.property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Date getPublishDate() {
		return this.publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public String getSalaryCode() {
		return this.salaryCode;
	}

	public void setSalaryCode(String salaryCode) {
		this.salaryCode = salaryCode;
	}

	public String getTagSelected() {
		return this.tagSelected;
	}

	public void setTagSelected(String tagSelected) {
		this.tagSelected = tagSelected;
	}

}