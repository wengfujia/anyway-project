package org.anyway.wechat.entity.customer;

/**
 * 文本消息
 * @author beinfo
 *
 */
public class TextMessage extends CustomerBaseMessage {
	/**
	 * 文本消息对象
	 */
	private Text text;
	
	public TextMessage() {
		super();
	}
	
	public TextMessage(Text text) {
		super();
		this.text = text;
	}

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}
}
