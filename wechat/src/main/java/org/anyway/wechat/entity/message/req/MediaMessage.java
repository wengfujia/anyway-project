package org.anyway.wechat.entity.message.req;

/**
 *  多媒体消息<br>
 * 图片消息、语音消息直接用此类
 * @author beinfo
 *
 */
public class MediaMessage extends BaseMessage {
	/**
	 * 媒体id，可以调用多媒体文件下载接口拉取数据
	 */
	private String mediaId;

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
}
