package crawler.post.model;

public class PostStatus {

	private Long id;
	private Long postId;
	private Integer number;
	private Integer employed;
	private Integer unemploy;

	private Boolean dirty = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getEmployed() {
		return employed;
	}

	public void setEmployed(Integer employed) {
		this.employed = employed;
	}

	public Integer getUnemploy() {
		return unemploy;
	}

	public void setUnemploy(Integer unemploy) {
		this.unemploy = unemploy;
	}

	public Boolean getDirty() {
		return dirty;
	}

	public void setDirty(Boolean dirty) {
		this.dirty = dirty;
	}

}
