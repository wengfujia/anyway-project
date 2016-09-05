package org.anyway.wechat.entity.message.req;

/**
 * 链接消息
 * @author beinfo
 *
 */
public class LinkMessage extends BaseMessage {
	/**
	 * 标题
	 */
	private String Title;
	/**
	 * 描述
	 */
	private String Description;
	/**
	 * 链接
	 */
	private String Url;
	
	public LinkMessage() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public LinkMessage(String title, String description, String url) {
		super();
		Title = title;
		Description = description;
		Url = url;
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

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}
}
