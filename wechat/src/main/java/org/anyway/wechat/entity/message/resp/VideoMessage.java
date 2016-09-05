package org.anyway.wechat.entity.message.resp;

/**
 * 视频消息
 * @author beinfo
 *
 */
public class VideoMessage extends MediaMessage {
	/**
	 * 视频消息的标题
	 */
	private String Title;
	
	/**
	 * 视频消息的描述
	 */
	private String Description;

	public VideoMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public VideoMessage(String title, String description) {
		super();
		Title = title;
		Description = description;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}
}
