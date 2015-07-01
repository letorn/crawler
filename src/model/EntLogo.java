package model;

import java.io.Serializable;

import dao.data.Column;
import dao.data.Id;
import dao.data.Table;

@Table("zcdh_ent_logo")
public class EntLogo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column("ent_id")
	private Long entId;

	@Column("file_code")
	private String fileCode;

	@Column("img_category")
	private String imgCategory;

	@Column("img_name")
	private String imgName;

	@Column("img_path")
	private String imgPath;

	@Column("img_size")
	private Long imgSize;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getEntId() {
		return this.entId;
	}

	public void setEntId(Long entId) {
		this.entId = entId;
	}

	public String getFileCode() {
		return this.fileCode;
	}

	public void setFileCode(String fileCode) {
		this.fileCode = fileCode;
	}

	public String getImgCategory() {
		return this.imgCategory;
	}

	public void setImgCategory(String imgCategory) {
		this.imgCategory = imgCategory;
	}

	public String getImgName() {
		return this.imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public String getImgPath() {
		return this.imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public Long getImgSize() {
		return this.imgSize;
	}

	public void setImgSize(Long imgSize) {
		this.imgSize = imgSize;
	}

}