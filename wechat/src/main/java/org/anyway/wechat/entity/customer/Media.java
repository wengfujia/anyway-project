package org.anyway.wechat.entity.customer;

/**
 * 多媒体对象<br>
 * 图片，语音直接用此对象
 * @author beinfo
 *
 */
public class Media {
	/**
	 * 媒体ID(图片，语音的媒体ID)
	 */
	private String mediaId;
	
	public Media() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Media(String mediaId) {
		super();
		this.mediaId = mediaId;
	}
	
	public String getMediaId() {
		return mediaId;
	}
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
}
