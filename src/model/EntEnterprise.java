package model;

import java.io.Serializable;
import java.util.Date;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_ent_enterprise")
public class EntEnterprise implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id("ent_id")
	private Long entId;

	@Column
	private String address;

	@Column
	private Long bkgId;

	@Column
	private String busRoutes;

	@Column
	private String city;

	@Column
	private String contact;

	@Column
	private String email;

	@Column("employ_num")
	private String employNum;

	@Column("ent_name")
	private String entName;

	@Column("ent_web")
	private String entWeb;

	@Column("establish_date")
	private Date establishDate;

	@Column
	private String fax;

	@Column
	private String industry;

	@Column
	private String introduction;

	@Column
	private Integer isLegalize;

	@Column("is_public_email")
	private Integer isPublicEmail;

	@Column("is_public_phone")
	private Integer isPublicPhone;

	@Column
	private String landmarks;

	@Column("lbs_id")
	private Long lbsId;

	@Column
	private String logo;

	@Column("micro_channel")
	private String microChannel;

	@Column("microblogging")
	private String microBlogging;

	@Column
	private String mobile;

	@Column
	private String parea;

	@Column
	private String phone;

	@Column
	private String property;

	@Column
	private String remark;

	@Column
	private String welfare;

	@Column
	private String zip;

	@Column("invited_ent_id")
	private Long invitedEntId;

	@Column("welfare_selected")
	private String welfareSelected;

	@Column("is_public_mobile")
	private Integer isPublicMobile;

	@Column("admin_account")
	private String adminAccount;

	@Column("create_date")
	private Date createDate;

	@Column("customer_service")
	private String customerService;

	@Column("tag_selected")
	private String tagSelected;

	@Column("bk_file_code")
	private String bkFileCode;

	@Column("file_code")
	private String fileCode;

	@Column("short_name")
	private String shortName;

	@Column("custom_html_name")
	private String customHtmlName;

	@Column("hightlight")
	private String hightLight;

	@Column
	private String domain;

	@Column("data_src")
	private String dataSrc;

	@Column("data_url")
	private String dataUrl;

	public Long getEntId() {
		return entId;
	}

	public void setEntId(Long entId) {
		this.entId = entId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Long getBkgId() {
		return bkgId;
	}

	public void setBkgId(Long bkgId) {
		this.bkgId = bkgId;
	}

	public String getBusRoutes() {
		return busRoutes;
	}

	public void setBusRoutes(String busRoutes) {
		this.busRoutes = busRoutes;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmployNum() {
		return employNum;
	}

	public void setEmployNum(String employNum) {
		this.employNum = employNum;
	}

	public String getEntName() {
		return entName;
	}

	public void setEntName(String entName) {
		this.entName = entName;
	}

	public String getEntWeb() {
		return entWeb;
	}

	public void setEntWeb(String entWeb) {
		this.entWeb = entWeb;
	}

	public Date getEstablishDate() {
		return establishDate;
	}

	public void setEstablishDate(Date establishDate) {
		this.establishDate = establishDate;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public Integer getIsLegalize() {
		return isLegalize;
	}

	public void setIsLegalize(Integer isLegalize) {
		this.isLegalize = isLegalize;
	}

	public Integer getIsPublicEmail() {
		return isPublicEmail;
	}

	public void setIsPublicEmail(Integer isPublicEmail) {
		this.isPublicEmail = isPublicEmail;
	}

	public Integer getIsPublicPhone() {
		return isPublicPhone;
	}

	public void setIsPublicPhone(Integer isPublicPhone) {
		this.isPublicPhone = isPublicPhone;
	}

	public String getLandmarks() {
		return landmarks;
	}

	public void setLandmarks(String landmarks) {
		this.landmarks = landmarks;
	}

	public Long getLbsId() {
		return lbsId;
	}

	public void setLbsId(Long lbsId) {
		this.lbsId = lbsId;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getMicroChannel() {
		return microChannel;
	}

	public void setMicroChannel(String microChannel) {
		this.microChannel = microChannel;
	}

	public String getMicroBlogging() {
		return microBlogging;
	}

	public void setMicroBlogging(String microBlogging) {
		this.microBlogging = microBlogging;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getParea() {
		return parea;
	}

	public void setParea(String parea) {
		this.parea = parea;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
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

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public Long getInvitedEntId() {
		return invitedEntId;
	}

	public void setInvitedEntId(Long invitedEntId) {
		this.invitedEntId = invitedEntId;
	}

	public String getWelfareSelected() {
		return welfareSelected;
	}

	public void setWelfareSelected(String welfareSelected) {
		this.welfareSelected = welfareSelected;
	}

	public Integer getIsPublicMobile() {
		return isPublicMobile;
	}

	public void setIsPublicMobile(Integer isPublicMobile) {
		this.isPublicMobile = isPublicMobile;
	}

	public String getAdminAccount() {
		return adminAccount;
	}

	public void setAdminAccount(String adminAccount) {
		this.adminAccount = adminAccount;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCustomerService() {
		return customerService;
	}

	public void setCustomerService(String customerService) {
		this.customerService = customerService;
	}

	public String getTagSelected() {
		return tagSelected;
	}

	public void setTagSelected(String tagSelected) {
		this.tagSelected = tagSelected;
	}

	public String getBkFileCode() {
		return bkFileCode;
	}

	public void setBkFileCode(String bkFileCode) {
		this.bkFileCode = bkFileCode;
	}

	public String getFileCode() {
		return fileCode;
	}

	public void setFileCode(String fileCode) {
		this.fileCode = fileCode;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getCustomHtmlName() {
		return customHtmlName;
	}

	public void setCustomHtmlName(String customHtmlName) {
		this.customHtmlName = customHtmlName;
	}

	public String getHightLight() {
		return hightLight;
	}

	public void setHightLight(String hightLight) {
		this.hightLight = hightLight;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
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