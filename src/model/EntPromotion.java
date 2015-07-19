package model;

import java.io.Serializable;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_ent_promotion")
public class EntPromotion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id("promotion_id")
	private Long promotionId;

	@Column("ent_id")
	private Long entId;

	@Column("ent_post_id")
	private Long entPostId;

	@Column("order_by")
	private Integer orderBy;

	@Column("promotion_value")
	private String promotionValue;

	public Long getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(Long promotionId) {
		this.promotionId = promotionId;
	}

	public Long getEntId() {
		return entId;
	}

	public void setEntId(Long entId) {
		this.entId = entId;
	}

	public Long getEntPostId() {
		return entPostId;
	}

	public void setEntPostId(Long entPostId) {
		this.entPostId = entPostId;
	}

	public Integer getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}

	public String getPromotionValue() {
		return promotionValue;
	}

	public void setPromotionValue(String promotionValue) {
		this.promotionValue = promotionValue;
	}

}