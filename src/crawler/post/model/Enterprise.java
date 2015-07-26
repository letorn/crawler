package crawler.post.model;

import java.util.Date;

public class Enterprise {

	private Long id;
	private String name;
	private String category;
	private String categoryCode;
	private String nature;
	private String natureCode;
	private String scale;
	private String scaleCode;
	private String introduction;
	private String website;
	private String areaCode;
	private String address;
	private String dataSrc;
	private String dataUrl;
	private Date createDate;

	private Long lbsId;
	private Double lbsLon;
	private Double lbsLat;

	private Integer status = 0;// 0 未处理, 1忽略, 2 新增, 3 更新, 大于1表示已经处理

	private Long enterpriseAccountId;
	private Integer enterpriseAccountCreateMode;

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

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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

	public Long getEnterpriseAccountId() {
		return enterpriseAccountId;
	}

	public void setEnterpriseAccountId(Long enterpriseAccountId) {
		this.enterpriseAccountId = enterpriseAccountId;
	}

	public Integer getEnterpriseAccountCreateMode() {
		return enterpriseAccountCreateMode;
	}

	public void setEnterpriseAccountCreateMode(Integer enterpriseAccountCreateMode) {
		this.enterpriseAccountCreateMode = enterpriseAccountCreateMode;
	}

}
