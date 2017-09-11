package org.anyway.common.protocol.buffer;

/*
 * 名称: ChrList
 * 描述: 自定义CChrList与HChrList的接口类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2014年1月27日
 * 修改日期:
 */
public interface IChrList {
	
	/***
	 * Append
	 * @param str
	 */
	public void Append(String str);
	
	/***
	 * Append
	 * @param nr
	 */
	public void Append(byte[] nr);
}
