package model;

import java.io.Serializable;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_ent_ability_require")
public class EntAbilityRequire implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id("ent_ability_id")
	private Long entAbilityId;

	@Column("post_id")
	private Long postId;

	@Column("ent_id")
	private Long entId;

	@Column
	private Integer grade;

	@Column("match_type")
	private Integer matchType;

	@Column("param_code")
	private String paramCode;

	@Column("post_code")
	private String postCode;

	@Column("technology_cate_code")
	private String technologyCateCode;

	@Column("technology_code")
	private String technologyCode;

	@Column("total_point")
	private Double totalPoint;

	@Column
	private Integer weight;

	@Column("weight_point")
	private Double weightPoint;

	public Long getEntAbilityId() {
		return this.entAbilityId;
	}

	public void setEntAbilityId(Long entAbilityId) {
		this.entAbilityId = entAbilityId;
	}

	public Long getEntId() {
		return this.entId;
	}

	public void setEntId(Long entId) {
		this.entId = entId;
	}

	public Integer getGrade() {
		return this.grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Integer getMatchType() {
		return this.matchType;
	}

	public void setMatchType(Integer matchType) {
		this.matchType = matchType;
	}

	public String getParamCode() {
		return this.paramCode;
	}

	public void setParamCode(String paramCode) {
		this.paramCode = paramCode;
	}

	public String getPostCode() {
		return this.postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public Long getPostId() {
		return this.postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public String getTechnologyCateCode() {
		return this.technologyCateCode;
	}

	public void setTechnologyCateCode(String technologyCateCode) {
		this.technologyCateCode = technologyCateCode;
	}

	public String getTechnologyCode() {
		return this.technologyCode;
	}

	public void setTechnologyCode(String technologyCode) {
		this.technologyCode = technologyCode;
	}

	public Double getTotalPoint() {
		return this.totalPoint;
	}

	public void setTotalPoint(Double totalPoint) {
		this.totalPoint = totalPoint;
	}

	public Integer getWeight() {
		return this.weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Double getWeightPoint() {
		return this.weightPoint;
	}

	public void setWeightPoint(Double weightPoint) {
		this.weightPoint = weightPoint;
	}

}