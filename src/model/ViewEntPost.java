package model;

import java.io.Serializable;
import java.util.Date;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_view_ent_post")
public class ViewEntPost implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Column("post_id")
	private Long postId;

	@Column
	private Double lat;

	@Column
	private Double lon;

	@Column("tag_selected")
	private String tagSelected;

	@Column("post_aliases")
	private String postAliases;

	@Column("ent_id")
	private Long entId;

	@Column("ent_name")
	private String entName;

	@Column
	private String industry;

	@Column
	private String property;

	@Column("employ_num")
	private String employNum;

	@Column("area_code")
	private String areaCode;

	@Column("salary_code")
	private String salaryCode;

	@Column("post_code")
	private String postCode;

	@Column("publish_date")
	private Date publishDate;

	@Column("max_salary")
	private Integer maxSalary;

	@Column("min_salary")
	private Integer minSalary;

	@Column("post_property_code")
	private String postPropertyCode;

	@Column("salary_type")
	private Integer salaryType;

	@Column("week_day")
	private String weekDay;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public String getTagSelected() {
		return tagSelected;
	}

	public void setTagSelected(String tagSelected) {
		this.tagSelected = tagSelected;
	}

	public String getPostAliases() {
		return postAliases;
	}

	public void setPostAliases(String postAliases) {
		this.postAliases = postAliases;
	}

	public Long getEntId() {
		return entId;
	}

	public void setEntId(Long entId) {
		this.entId = entId;
	}

	public String getEntName() {
		return entName;
	}

	public void setEntName(String entName) {
		this.entName = entName;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getEmployNum() {
		return employNum;
	}

	public void setEmployNum(String employNum) {
		this.employNum = employNum;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getSalaryCode() {
		return salaryCode;
	}

	public void setSalaryCode(String salaryCode) {
		this.salaryCode = salaryCode;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public Integer getMaxSalary() {
		return maxSalary;
	}

	public void setMaxSalary(Integer maxSalary) {
		this.maxSalary = maxSalary;
	}

	public Integer getMinSalary() {
		return minSalary;
	}

	public void setMinSalary(Integer minSalary) {
		this.minSalary = minSalary;
	}

	public String getPostPropertyCode() {
		return postPropertyCode;
	}

	public void setPostPropertyCode(String postPropertyCode) {
		this.postPropertyCode = postPropertyCode;
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

}