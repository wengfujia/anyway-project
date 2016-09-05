package org.anyway.wechat.entity.customer;

/**
 * 视频消息
 * @author beinfo
 *
 */
public class VideoMessage extends MediaMessage {
	/**
	 * 视频消息对象
	 */
	private Video video;

	public VideoMessage() {
		super();
	}

	public VideoMessage(Video video) {
		super();
		this.video = video;
	}

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}
}
