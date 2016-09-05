package org.anyway.wechat.entity.customer;

/**
 * 文本消息对象
 * @author beinfo
 *
 */
public class Text {
	/**
	 * 文本消息内容
	 */
	private String content;
	
	public Text() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Text(String content) {
		super();
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
