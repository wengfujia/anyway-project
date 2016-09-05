package org.anyway.wechat.entity.message.resp;

/**
 * 文本消息
 * @author beinfo
 *
 */
public class TextMessage extends BaseMessage {
	/**
	 * 回复的消息内容
	 */
	private String Content;

	public TextMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TextMessage(String content) {
		super();
		Content = content;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}
}
