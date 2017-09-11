/*
 * 名称: TCPREQUEST.java
 * 描述: tcp封装类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月29日
 * 修改日期:
 */

package org.anyway.common.protocol.request;

import java.util.concurrent.atomic.AtomicInteger;

import org.anyway.common.protocol.TcpMessageCoder;
import org.anyway.common.utils.StringUtil;

public class TcpRequest extends BaseRequest {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4352766292056577874L;
	
	private TcpMessageCoder cstream;
	
	public TcpRequest() {
		this.id = StringUtil.getUUID();
		this.status = 0; //等待状态
		this.time = System.currentTimeMillis();
		this.retry = new AtomicInteger(0);
	}
	
	/**
	 * UUID号表示接连唯一号，做为key
	 * @return
	 */
	public String getID() {
		return StringUtil.getUUID();
	}
	
	/**
	 * 设置socket的接收数据
	 * @return
	 */
	public TcpMessageCoder getCStream() {
		return this.cstream;
	}
	public void setCStream(TcpMessageCoder stream) {
		this.cstream = stream;
	}
	
}
