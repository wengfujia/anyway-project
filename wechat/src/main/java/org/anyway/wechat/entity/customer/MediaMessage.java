package org.anyway.wechat.entity.customer;

/**
 * 媒体类， <br>
 * 图片/语音可直接用此类
 * @author beinfo
 *
 */
public class MediaMessage extends CustomerBaseMessage {
	/**
	 * 媒体对象
	 */
	private Media media;
	
	public MediaMessage() {
		super();
	}

	public MediaMessage(Media media) {
		super();
		this.media = media;
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}
}
