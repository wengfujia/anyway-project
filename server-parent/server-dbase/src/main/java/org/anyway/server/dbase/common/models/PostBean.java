package org.anyway.server.dbase.common.models;
/*
 * 名称: PostBean
 * 描述: 职位类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */
@SuppressWarnings("serial")
public class PostBean extends DataBase{
	
	protected String postId;
	protected String postName;
	protected String description;
	protected String creater;
	protected PostBean postBean;
	
	/**
	 * 获取职位名
	 * @return String
	 */
	public String getPostName() {
		return postName;
	}	
	/**
	 * 设置职位名
	 * @param postName
	 */
	public void setPostName(String postName) {
		this.postName = postName;
	}
	
	/**
	 * 获取职位描停
	 * @return String
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * 设置职位描述
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * 获取创建者
	 * @return String
	 */
	public String getCreater() {
		return creater;
	}
	/**
	 * 设置创建者
	 * @param creater
	 */
	public void setCreater(String creater) {
		this.creater = creater;
	}
	
	/**
	 * 获取职位信息
	 * @return PostBean
	 */
	public PostBean getPostBean() {
		return postBean;
	}
	/**
	 * 设置职位信息
	 * @param postBean
	 */
	public void setPostBean(PostBean postBean) {
		this.postBean = postBean;
	}
}
