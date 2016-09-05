package org.anyway.wechat.entity.message.resp;

import java.util.List;

/**
 * 多图文消息
 * @author beinfo
 *
 */
public class NewsMessage extends BaseMessage {
	/**
	 * 图文消息个数，限制为10条以内
	 */
	private int ArticleCount;
	
	/**
	 * 多条图文消息信息，默认第一个item为大图
	 */
	private List<Article> Articles;

	public NewsMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NewsMessage(int articleCount, List<Article> articles) {
		super();
		ArticleCount = articleCount;
		Articles = articles;
	}

	public int getArticleCount() {
		return ArticleCount;
	}

	public void setArticleCount(int articleCount) {
		ArticleCount = articleCount;
	}

	public List<Article> getArticles() {
		return Articles;
	}

	public void setArticles(List<Article> articles) {
		Articles = articles;
	}
}
