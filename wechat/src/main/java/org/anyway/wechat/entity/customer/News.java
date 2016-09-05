package org.anyway.wechat.entity.customer;

import java.util.List;

/**
 * 多图文消息对象
 * @author beinfo
 *
 */
public class News {
	/**
	 * 多条图文消息信息列表，默认第一个item为大图
	 * 图文消息条数限制在10条以内
	 */
	private List<Article> Articles;

	public News() {
		super();
		// TODO Auto-generated constructor stub
	}

	public News(List<Article> articles) {
		super();
		Articles = articles;
	}

	public List<Article> getArticles() {
		return Articles;
	}

	public void setArticles(List<Article> articles) {
		Articles = articles;
	}
}
