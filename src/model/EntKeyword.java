package model;

import java.io.Serializable;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_ent_keyword")
public class EntKeyword implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id("key_id")
	private Long keyId;

	@Column("post_id")
	private Long postId;

	@Column("adv_id")
	private Long advId;

	@Column("key_word")
	private String keyWord;

	@Column("advert_typecode")
	private String advertTypecode;

	@Column("price")
	private Double price;

	@Column("orders")
	private Integer orders;

	public Long getKeyId() {
		return keyId;
	}

	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public Long getAdvId() {
		return advId;
	}

	public void setAdvId(Long advId) {
		this.advId = advId;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getAdvertTypecode() {
		return advertTypecode;
	}

	public void setAdvertTypecode(String advertTypecode) {
		this.advertTypecode = advertTypecode;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getOrders() {
		return orders;
	}

	public void setOrders(Integer orders) {
		this.orders = orders;
	}

}