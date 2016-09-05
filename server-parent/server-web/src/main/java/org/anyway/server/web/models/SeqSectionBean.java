package org.anyway.server.web.models;

/*
 * 名称: SeqSectionBean
 * 描述: 递增序号号段
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年07月09日
 * 修改日期:
 */

public class SeqSectionBean {
	
	private String sectionName;
	private int minId;
	private int maxId;
	private int length;
	
	/**
	 * 获取或设置序号号段的名称
	 * 表示此名称的设置有效
	 * @return
	 */
	public String getSectionName() {
		return this.sectionName;
	}
	public void setSectionName(String name) {
		this.sectionName = name;
	}
	
	/**
	 * 获取或设置最小id的值
	 * @return
	 */
	public int getMinID() {
		return this.minId;
	}
	public void setMinID(int value) {
		this.minId = value;
	}
	
	/**
	 * 获取或设置允许最大id的值
	 * @return
	 */
	public int getMaxID() {
		return this.maxId;
	}
	public void setMaxID(int value) {
		this.maxId = value;
	}
	
	/**
	 * 获取或设置序号长度
	 * 如序号为2位长，则length=10，依次类推
	 * @return
	 */
	public int getLength() {
		return this.length;
	}
	public void setLength(int len) {
		this.length = len;
	}
	
}
