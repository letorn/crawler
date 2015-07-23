package crawler.post.model;

import java.util.Date;

import map.Point;

public class Post implements Point {

	private Long id;
	private String name;
	private String category;
	private String categoryCode;
	private Integer number;
	private String numberText;
	private Integer isSeveral;
	private String nature;
	private String natureCode;
	private String salary;
	private String salaryText;
	private Integer salaryType = 1;
	private String experience;
	private String experienceCode;
	private String education;
	private String educationCode;
	private String welfare;
	private String welfareCode;
	private String introduction;
	private String areaCode;
	private String address;
	private String dataSrc;
	private String dataUrl;
	private Date updateDate;
	private Date publishDate;

	private Long lbsId;
	private Double lbsLon;
	private Double lbsLat;

	private Integer status = 0;// -1 数据不完整, 0 数据完整，但未处理, 1忽略, 2 新增, 3 更新

	private String enterpriseUrl;
	private String enterpriseName;

	private Long experienceId;
	private Long educationId;
	private Long postStatusId;
	private Long postPromotionId;
	private Long postViewId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getNumberText() {
		return numberText;
	}

	public void setNumberText(String numberText) {
		this.numberText = numberText;
	}

	public Integer getIsSeveral() {
		return isSeveral;
	}

	public void setIsSeveral(Integer isSeveral) {
		this.isSeveral = isSeveral;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getNatureCode() {
		return natureCode;
	}

	public void setNatureCode(String natureCode) {
		this.natureCode = natureCode;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getSalaryText() {
		return salaryText;
	}

	public void setSalaryText(String salaryText) {
		this.salaryText = salaryText;
	}

	public Integer getSalaryType() {
		return salaryType;
	}

	public void setSalaryType(Integer salaryType) {
		this.salaryType = salaryType;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getExperienceCode() {
		return experienceCode;
	}

	public void setExperienceCode(String experienceCode) {
		this.experienceCode = experienceCode;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getEducationCode() {
		return educationCode;
	}

	public void setEducationCode(String educationCode) {
		this.educationCode = educationCode;
	}

	public String getWelfare() {
		return welfare;
	}

	public void setWelfare(String welfare) {
		this.welfare = welfare;
	}

	public String getWelfareCode() {
		return welfareCode;
	}

	public void setWelfareCode(String welfareCode) {
		this.welfareCode = welfareCode;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public Long getLbsId() {
		return lbsId;
	}

	public void setLbsId(Long lbsId) {
		this.lbsId = lbsId;
	}

	public Double getLbsLon() {
		return lbsLon;
	}

	public void setLbsLon(Double lbsLon) {
		this.lbsLon = lbsLon;
	}

	public Double getLbsLat() {
		return lbsLat;
	}

	public void setLbsLat(Double lbsLat) {
		this.lbsLat = lbsLat;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getEnterpriseUrl() {
		return enterpriseUrl;
	}

	public void setEnterpriseUrl(String enterpriseUrl) {
		this.enterpriseUrl = enterpriseUrl;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public Long getExperienceId() {
		return experienceId;
	}

	public void setExperienceId(Long experienceId) {
		this.experienceId = experienceId;
	}

	public Long getEducationId() {
		return educationId;
	}

	public void setEducationId(Long educationId) {
		this.educationId = educationId;
	}

	public Long getPostStatusId() {
		return postStatusId;
	}

	public void setPostStatusId(Long postStatusId) {
		this.postStatusId = postStatusId;
	}

	public Long getPostPromotionId() {
		return postPromotionId;
	}

	public void setPostPromotionId(Long postPromotionId) {
		this.postPromotionId = postPromotionId;
	}

	public Long getPostViewId() {
		return postViewId;
	}

	public void setPostViewId(Long postViewId) {
		this.postViewId = postViewId;
	}

	public Long getPointId() {
		return lbsId;
	}

	public double[] getPoint() {
		return new double[] { lbsLon, lbsLat };
	}

}
