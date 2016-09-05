package org.anyway.server.web.models;

/*
 * 名称: KeywordBean
 * 描述: 缓存数据类基类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 * 修改日期:
 * 暂废用
 */

@SuppressWarnings("serial")
public class StopWordBean implements java.io.Serializable {
	private String keyword;

    /**
	 * 获取关键字
	 * @return String
	 */
	public String getKeyword() {
        return keyword;
    }
	/**
	 * 设置关键字
	 * @param keyword
	 */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
}