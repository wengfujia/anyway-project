package org.anyway.wechat.entity.customer;

/**
 * 发送图文消息
 * @author beinfo
 *
 */
public class NewsMessage extends CustomerBaseMessage {
	/**
	 * 图文消息对象
	 */
	private News news;

	public NewsMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NewsMessage(News news) {
		super();
		this.news = news;
	}

	public News getNews() {
		return news;
	}

	public void setNews(News news) {
		this.news = news;
	}
}
