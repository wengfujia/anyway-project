/*
 * 名称: TCPREQUEST.java
 * 描述: tcp封装类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月29日
 * 修改日期:
 */

package org.anyway.server.data.packages;

import java.util.Date;
import io.netty.channel.ChannelHandlerContext;

import org.anyway.common.utils.uStringUtils;
import org.anyway.server.api.CSHTMsgStream;

@SuppressWarnings("serial")
public class TCPREQUEST implements java.io.Serializable {
	
	private String id;
	private ChannelHandlerContext context;
	private CSHTMsgStream cstream;
	
	private int status;
	private long time;
	
	public TCPREQUEST() {
		this.status = 0; //等待状态
		this.time = System.currentTimeMillis();
		this.id = String.valueOf(this.time + Long.valueOf(uStringUtils.getRandom()));
	}
	
	/**
	 * UUID号表示接连唯一号，做为key
	 * @return
	 */
	public String getID() {
		return this.id;
	}
	
	/**
	 * 获取context
	 * @return
	 */
	public ChannelHandlerContext getContext() {
		return this.context;
	}
	/**
	 * 设置ChannelHandlerContext
	 * @param context
	 */
	public void setContext(ChannelHandlerContext context) {
		this.context = context;
	}
	
	/**
	 * 设置socket的接收数据
	 * @return
	 */
	public CSHTMsgStream getCStream() {
		return this.cstream;
	}
	public void setCStream(CSHTMsgStream stream) {
		this.cstream = stream;
	}
	
	/**
	 * 设置等待处理
	 */
	public void setWait() {
		this.status = 0;
		this.time = new Date().getTime();
	}
	
	/**
	 * 设置已经处理，等待消息应答
	 */
	public void setDoning() {
		this.status = 1;
		this.time = new Date().getTime();
	}
	
	/**
	 * 设置消息已经收到应答，等待接收消息处理结果
	 */
	public void setDone() {
		this.status = 2;
		this.time = new Date().getTime();
	}
	
	/**
	 * 是否处理等待应答状态
	 * @return
	 */
	public Boolean isDoning() {
		return this.status == 1 ? true:false;
	}
	
	/**
	 * 是否处理等待处理结果状态
	 * @return
	 */
	public Boolean isDone() {
		return this.status == 2 ? true:false;
	}
	
	/**
	 * 获取状态变化后距现在的时间秒数
	 * @return
	 */
	public long getTimes() {
		long times = (System.currentTimeMillis() - this.time) / 1000;
		return times;
	}
	
	/**
	 * 关闭连接
	 */
	public void Close() {
		this.context.close();
		this.context = null;
	}
}
