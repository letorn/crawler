package crawler.post.model;

import java.util.Date;

public class Enterprise {

	private Long id;
	private String url;
	private Date date;
	private String name;
	private String category;
	private String categoryCode;
	private String nature;
	private String natureCode;
	private String scale;
	private String scaleCode;
	private String website;
	private String address;
	private String introduction;
	private Integer status = 0;// 0 未处理, 1忽略, 2 新增, 3 更新, 大于1表示已经处理

	private String areaCode;
	private Double lbsLon;
	private Double lbsLat;

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

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getScaleCode() {
		return scaleCode;
	}

	public void setScaleCode(String scaleCode) {
		this.scaleCode = scaleCode;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public Double getLBSLon() {
		return lbsLon;
	}

	public void setLBSLon(Double lbsLon) {
		this.lbsLon = lbsLon;
	}

	public Double getLBSLat() {
		return lbsLat;
	}

	public void setLBSLat(Double lbsLat) {
		this.lbsLat = lbsLat;
	}

}
