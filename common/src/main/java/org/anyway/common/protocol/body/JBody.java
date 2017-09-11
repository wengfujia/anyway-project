package org.anyway.common.protocol.body;

import java.util.List;

/*
 * 名称: JBody
 * 描述: Json包体中解析包
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2014年2月2日
 * 修改日期:
 */
public class JBody {
	private String head;
	private List<String> content;
	
	/**
	 * 获取包体中的头
	 * @return
	 */
	public String GetHead() {
		return head;
	}
	/**
	 * 设置包体中的头
	 * @param sHead
	 */
	public void SetHead(String sHead) {
		this.head = sHead;
	}
	
	/**
	 * 获取包体中的内容
	 * @return List<String>
	 */
	public List<String> GetContent() {
		return content;
	}
	/**
	 * 设置包体中的内容
	 * @param mContent
	 */
	public void SetContent (List<String> mContent) {
		this.content = mContent;
	}
}
