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

	@Column("post_id")
	private Long postId;

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
		return entAbilityId;
	}

	public void setEntAbilityId(Long entAbilityId) {
		this.entAbilityId = entAbilityId;
	}

	public Long getEntId() {
		return entId;
	}

	public void setEntId(Long entId) {
		this.entId = entId;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Integer getMatchType() {
		return matchType;
	}

	public void setMatchType(Integer matchType) {
		this.matchType = matchType;
	}

	public String getParamCode() {
		return paramCode;
	}

	public void setParamCode(String paramCode) {
		this.paramCode = paramCode;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public String getTechnologyCateCode() {
		return technologyCateCode;
	}

	public void setTechnologyCateCode(String technologyCateCode) {
		this.technologyCateCode = technologyCateCode;
	}

	public String getTechnologyCode() {
		return technologyCode;
	}

	public void setTechnologyCode(String technologyCode) {
		this.technologyCode = technologyCode;
	}

	public Double getTotalPoint() {
		return totalPoint;
	}

	public void setTotalPoint(Double totalPoint) {
		this.totalPoint = totalPoint;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Double getWeightPoint() {
		return weightPoint;
	}

	public void setWeightPoint(Double weightPoint) {
		this.weightPoint = weightPoint;
	}

}