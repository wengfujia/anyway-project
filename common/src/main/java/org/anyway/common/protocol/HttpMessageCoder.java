/*
 * 名称: HSHTMsgStream
 * 描述: Http的消息处理类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

package org.anyway.common.protocol;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

import org.anyway.common.SystemConfig;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.utils.NetUtil;
import org.anyway.common.utils.SecretUtil;
import org.anyway.common.protocol.buffer.impl.http.HChrList;
import org.anyway.common.protocol.buffer.impl.http.HMessageBuffer;
import org.anyway.common.protocol.header.Header;

public class HttpMessageCoder {

	private Header header = new Header();
	private HChrList msgs = new HChrList();
	
	/**
	 * 清空
	 */
	public void ClearStream() {
		if (msgs != null)
			msgs.Clear();
		header.Clear();
	}	
	
	/**
	 * 构造函数
	 */
	public HttpMessageCoder() {
		ClearStream();
	}
    
    /**
     * EncodeHeader
     * @param h
     */
    public void EncodeHeader(Header h) {
    	header.Clear();
    	header = h;
    }
    
    /**
     * 获取包头
     * @return
     */
    public Header getHeader(){
    	return header;
    }
    

    /**
     * SetNr
     * @param nr
     */
    public void SetNr(ArrayList<HMessageBuffer.CBody> nr) {
    	this.msgs.Append(nr);
    }

    /**
     * SetNr
     * @param nr
     */
    public void SetNr(HChrList nr) {
    	this.msgs = nr;
    }
    /**
     * GetNr
     * @return
     */
    public HChrList GetNr() {
    	return this.msgs;
    }

    /**
     * GetCommand
     * @return int 
     */
    public int GetCommand() {
    	return header.getCommandID();
    }

    /**
     * GetLength
     * @return int
     */
    public int GetLength() {
      return header.getLen();
    }

    /**
     * GetStatus
     * @return int
     */
    public int GetStatus() {
      return header.getStatus();
    }
	
	//把Stream里的东西读出到buffer里   //不去长度的
	public byte[] LoadFromStream(CryptEnum encrypt) {
		
		byte[] result = null;
		HMessageBuffer hbuffer = new HMessageBuffer();
		try
		{
			hbuffer.setResult(this.header.getStatus());
			hbuffer.setCommandId(this.header.getCommandID());
			hbuffer.setData(msgs.GetList());
			//转换成json
			String sjson = toJson(hbuffer);

			result = NetUtil.getBytes(sjson, SystemConfig.CharsetName);
			if (result != null) 
			{
				result = SecretUtil.Encrypt(result, encrypt);			
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (hbuffer!=null) {hbuffer.Clear(); hbuffer = null;}
		}
		return result;
	}
	
	//把Stream里的东西读出到buffer里   //不去长度的
	public String LoadFromStreamString(CryptEnum encrypt) {
		
		String result = null;
		HMessageBuffer hbuffer = new HMessageBuffer();
		try
		{
			hbuffer.setResult(this.header.getStatus());
			hbuffer.setUserName(this.header.getUser());
			hbuffer.setPassword(this.header.getPwd());
			hbuffer.setCommandId(this.header.getCommandID());
			hbuffer.setData(msgs.GetList());
			//转换成json
			String sjson = toJson(hbuffer);
			if (encrypt == CryptEnum.NONE) {
				result = sjson;
			}
			else {
				byte[] tmp = NetUtil.getBytes(sjson, SystemConfig.CharsetName);
				if (tmp != null) 
				{
					tmp = SecretUtil.Encrypt(tmp, encrypt);
					result = NetUtil.getString(tmp, SystemConfig.CharsetName);			
				}
				else {
					result = sjson;
				}
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		finally
		{
			if (hbuffer!=null) {hbuffer.Clear(); hbuffer = null;}
		}
		return result;
	}
	
	/**
	 * SetStringToBuffer
	 * 把String存入到msgs
	 * @param body
	 */
	public void SetStringToBuffer(String body)
	{
		msgs.Append(body);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * HMessageBuffer转换成json
	 * @param snr
	 * @return
	 */
	private static String toJson(HMessageBuffer snr) 
	{
		Gson gson = new Gson();
		return gson.toJson(snr);
	}
	
	/*
	 * 组合成HMessageBuffer
	 * 参数:int result, String body
	 * 返回:HMessageBuffer
	 */
	private static HMessageBuffer SetVarToHMessageBuffer(int result, String body)
	{
		HMessageBuffer hbuffer = new HMessageBuffer();
		List<HMessageBuffer.CBody> list = new ArrayList<HMessageBuffer.CBody>();
    	list.add(new HMessageBuffer.CBody(body));
    	hbuffer.setResult(result);
    	hbuffer.setData(list);
    	return hbuffer;
	}
	
	/*
	 * 组合成HMessageBuffer
	 * 参数:HEADER header, String body
	 * 返回:HMessageBuffer
	 */
	private static HMessageBuffer SetVarToHMessageBuffer(Header header, String body)
	{
		HMessageBuffer hbuffer = new HMessageBuffer();
		List<HMessageBuffer.CBody> list = new ArrayList<HMessageBuffer.CBody>();
    	list.add(new HMessageBuffer.CBody(body));
    	hbuffer.setResult(header.getStatus());
    	hbuffer.setCommandId(header.getCommandID());
    	hbuffer.setUserName(header.getUser());
    	hbuffer.setPassword(header.getPwd());
    	hbuffer.setData(list);
    	return hbuffer;
	}
	
	/*
	 * 组合成HMessageBuffer
	 * 参数:int result, byte[] body
	 * 返回:HMessageBuffer
	 */
	@SuppressWarnings("unused")
	private static HMessageBuffer SetVarToHMessageBuffer(int result, byte[] body)
	{
		HMessageBuffer hbuffer = new HMessageBuffer();
		ArrayList<HMessageBuffer.CBody> list = new ArrayList<HMessageBuffer.CBody>();
		String content = NetUtil.getString(body, SystemConfig.CharsetName);
    	list.add(new HMessageBuffer.CBody(content));
    	hbuffer.setResult(result);
    	hbuffer.setData(list);
    	return hbuffer;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 转换成json
	 * @param header
	 * @param body
	 * @return
	 * @throws Exception 
	 */
	public static byte[] toJson(Header header, String body)
	{
		byte[] jsons = null;
		HMessageBuffer hbuffer = null;
		try
		{
			hbuffer = SetVarToHMessageBuffer(header, body);
			jsons = NetUtil.getBytes(toJson(hbuffer), SystemConfig.CharsetName);	
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		finally
		{
			if (hbuffer!=null) {hbuffer.Clear(); hbuffer = null;}
		}
		
		return jsons;
	}
	
	/**
	 * 转换成json
	 * @param result
	 * @param body
	 * @return
	 * @throws Exception 
	 */
	public static byte[] toJson(int result, String body)
	{
		byte[] jsons = null;
		HMessageBuffer hbuffer = null;
		try
		{
			hbuffer = SetVarToHMessageBuffer(result, body);
			jsons = NetUtil.getBytes(toJson(hbuffer), SystemConfig.CharsetName);	
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (hbuffer!=null) {hbuffer.Clear(); hbuffer = null;}
		}
		
		return jsons;
	}
	
	/**
	 * 转换成json
	 * @param result
	 * @param body
	 * @return
	 * @throws Exception 
	 */
	public static String toJsonString(int result, String body) 
	{
		String jsons = "";
		HMessageBuffer hbuffer = null;
		try
		{
			hbuffer = SetVarToHMessageBuffer(result, body);
			jsons = toJson(hbuffer);
		}
		finally
		{
			if (hbuffer!=null) {hbuffer.Clear(); hbuffer = null;}
		}
		
		return jsons;
	}

}
