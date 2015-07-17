package crawler.post.model;

import java.util.Date;

public class Account {

	private Long id;
	private Long enterpriseId;
	private String account;
	private Integer createMode = 2;// 0 企业录入, 1 客服录入, 2 自动采集
	private Date createDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getCreateMode() {
		return createMode;
	}

	public void setCreateMode(Integer createMode) {
		this.createMode = createMode;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
