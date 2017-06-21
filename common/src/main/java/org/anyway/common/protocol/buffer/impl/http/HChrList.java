/*
 * 名称: HChrList
 * 描述: 用户HTTP的自定义List
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

package org.anyway.server.data.http;

import java.util.ArrayList;
import java.util.List;

import org.anyway.common.uConfigVar;
import org.anyway.common.utils.uNetUtil;
import org.anyway.server.data.contracts.IChrList;

public class HChrList implements IChrList{
	
	private List<HMessageBuffer.CBody> list = null;
	
	public HChrList()
	{
		list = new ArrayList<HMessageBuffer.CBody>();
	}
	
	/**
	 * 返回当前list
	 * @return
	 */
	public List<HMessageBuffer.CBody> GetList()
	{
		return this.list;
	}
	
	/**
	 * 追加String到list
	 * @param str
	 */
	public void Append(byte[] nr)
	{
		String str = "";
		str = uNetUtil.getString(nr, uConfigVar.CharsetName);
		HMessageBuffer.CBody body = new HMessageBuffer.CBody(str);
		list.add(body);
	}
	
	/**
	 * 追加String到list
	 * @param str
	 */
	public void Append(String str)
	{
		HMessageBuffer.CBody body = new HMessageBuffer.CBody(str);
		list.add(body);
	}
	
	/**
	 * 追加CBody到list
	 * @param body
	 */
	public void Append(HMessageBuffer.CBody body)
	{
		list.add(body);
	}
	
	/**
	 * 追加CBodys到list
	 * @param body
	 */
	public void Append(ArrayList<HMessageBuffer.CBody> bodys)
	{
		list = bodys;
	}
	
	/**
	 * Clear
	 */
	public void Clear()
	{
		if (list!=null)
		{
			list.clear();
		}
	}
	
	/**
	 * 清空并释放
	 */
	public void Release()
	{
		if (list!=null)
		{
			list.clear();
			list = null;
		}
	}
}
