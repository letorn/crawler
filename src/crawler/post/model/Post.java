package crawler.post.model;

import java.util.Date;
import java.util.Map;

public class Post {

	private Long id;
	private String url;
	private Date date;
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
	private String address;
	private String introduction;
	private String enterpriseURL;
	private Integer status = 0;// -1 数据不完整, 0 数据完整，但未处理, 1忽略, 2 新增, 3 更新, 大于1表示已经处理

	private Map<String, String> experienceAbility;
	private Map<String, String> educationAbility;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getURL() {
		return url;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getEnterpriseURL() {
		return enterpriseURL;
	}

	public void setEnterpriseURL(String enterpriseURL) {
		this.enterpriseURL = enterpriseURL;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Map<String, String> getExperienceAbility() {
		return experienceAbility;
	}

	public void setExperienceAbility(Map<String, String> experienceAbility) {
		this.experienceAbility = experienceAbility;
	}

	public Map<String, String> getEducationAbility() {
		return educationAbility;
	}

	public void setEducationAbility(Map<String, String> educationAbility) {
		this.educationAbility = educationAbility;
	}

}
