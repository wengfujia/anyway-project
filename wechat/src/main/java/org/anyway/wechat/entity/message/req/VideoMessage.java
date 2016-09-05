package org.anyway.wechat.entity.message.req;

/**
 * 视频消息<br>
 * 视频MsgType为video,小视频MsgType为shortvideo
 * @author beinfo
 *
 */
public class VideoMessage extends MediaMessage {
	/**
	 * 视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据
	 */
	private String thumbMediaId;

	public VideoMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VideoMessage(String thumbMediaId) {
		super();
		this.thumbMediaId = thumbMediaId;
	}

	public String getThumbMediaId() {
		return thumbMediaId;
	}

	public void setThumbMediaId(String thumbMediaId) {
		this.thumbMediaId = thumbMediaId;
	}
}
