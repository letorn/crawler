package model;

import java.io.Serializable;
import java.util.Date;

import dao.data.Column;
import dao.data.Id;
import dao.data.Ram;
import dao.data.Table;

@Table("zcdh_jobfair")
public class Jobfair implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Column("banner_id")
	private Long bannerId;

	@Column("org_id")
	private Long orgId;

	@Column
	private String organizers;

	@Column
	private String category;

	@Column
	private String title;

	@Column
	private String contacts;

	@Column
	private String phone;

	@Column("area_code")
	private String areaCode;

	@Ram("areaCode:areas.areaName")
	private String areaName;
	
	@Column
	private String domain;

	@Column
	private Double lat;

	@Column
	private Double lon;

	@Column
	private String address;

	@Column("bus_load")
	private String busLoad;

	@Column
	private String describes;

	@Column("create_time")
	private Date createTime;

	@Column("start_time")
	private Date startTime;

	@Column("end_time")
	private Date endTime;

	@Column("fair_type")
	private Integer fairType;

	@Column("html_name")
	private String htmlName;

	@Column
	private String intro;

	@Column("intro_type")
	private String introType;

	@Column
	private Integer status;

	@Column("sign_status")
	private Integer signStatus;

	@Column
	private Integer type;

	@Column
	private String url;

	@Column("url_type")
	private Integer urlType;

	@Column("app_file_code")
	private String appFileCode;

	@Column("place_file_code")
	private String placeFileCode;

	@Column("wap_file_code")
	private String wapFileCode;

	@Column("web_file_code")
	private String webFileCode;

	@Column
	private String step;

	@Column
	private String remark;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBannerId() {
		return bannerId;
	}

	public void setBannerId(Long bannerId) {
		this.bannerId = bannerId;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getOrganizers() {
		return organizers;
	}

	public void setOrganizers(String organizers) {
		this.organizers = organizers;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBusLoad() {
		return busLoad;
	}

	public void setBusLoad(String busLoad) {
		this.busLoad = busLoad;
	}

	public String getDescribes() {
		return describes;
	}

	public void setDescribes(String describes) {
		this.describes = describes;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getFairType() {
		return fairType;
	}

	public void setFairType(Integer fairType) {
		this.fairType = fairType;
	}

	public String getHtmlName() {
		return htmlName;
	}

	public void setHtmlName(String htmlName) {
		this.htmlName = htmlName;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getIntroType() {
		return introType;
	}

	public void setIntroType(String introType) {
		this.introType = introType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSignStatus() {
		return signStatus;
	}

	public void setSignStatus(Integer signStatus) {
		this.signStatus = signStatus;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getUrlType() {
		return urlType;
	}

	public void setUrlType(Integer urlType) {
		this.urlType = urlType;
	}

	public String getAppFileCode() {
		return appFileCode;
	}

	public void setAppFileCode(String appFileCode) {
		this.appFileCode = appFileCode;
	}

	public String getPlaceFileCode() {
		return placeFileCode;
	}

	public void setPlaceFileCode(String placeFileCode) {
		this.placeFileCode = placeFileCode;
	}

	public String getWapFileCode() {
		return wapFileCode;
	}

	public void setWapFileCode(String wapFileCode) {
		this.wapFileCode = wapFileCode;
	}

	public String getWebFileCode() {
		return webFileCode;
	}

	public void setWebFileCode(String webFileCode) {
		this.webFileCode = webFileCode;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}