package org.anyway.wechat.entity.customer;

/**
 * 视频消息对象
 * @author beinfo
 *
 */
public class Video {
	/**
	 * 缩略图的媒体ID
	 */
	private String thumbMediaId;
	/**
	 * 视频消息的标题
	 */
	private String title;
	
	/**
	 * 视频消息的描述
	 */
	private String description;

	public Video() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Video(String thumbMediaId, String title, String description) {
		super();
		this.thumbMediaId = thumbMediaId;
		this.title = title;
		this.description = description;
	}

	public String getThumbMediaId() {
		return thumbMediaId;
	}

	public void setThumbMediaId(String thumbMediaId) {
		this.thumbMediaId = thumbMediaId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
