/**
 * 
 */
package org.anyway.common.protocol.request;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.ChannelHandlerContext;

import org.anyway.common.future.InvokeCallback;
import org.anyway.common.models.IpTableBean;

/**
 * @author wengfj
 *
 */
public abstract class BaseRequest implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3337875469740015684L;

	/**
	 * request序号
	 */
	protected String id;
	/**
	 * 消息类型 weixin、http、tcp
	 */
	private int msgType;
	
	private ChannelHandlerContext context;
	private IpTableBean iptable;
	private int timeOutMillis; //超时时间
	
	protected int status;
	protected AtomicInteger retry;	//重试次数
	protected long time;	//当前时间
	
	/**
	 * 回调
	 */
	private InvokeCallback invokeCallback;
	
	/**
	 * UUID号表示接连唯一号，做为key
	 * @return
	 */
	public String getID() {
		return this.id;
	}
	
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
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
	 * 获取跟数据层通讯的ip信息
	 * @return
	 */
	public IpTableBean getIpTable() {
		return this.iptable;
	}
	
	/**
	 * 设置跟数据层通讯的ip信息
	 * @param value
	 */
	public void setIpTable(IpTableBean value) {
		this.iptable = value;
	}
	

	/**
	 * 获取重试次数
	 * @return
	 */
	public int getRetry() {
		return this.retry.get();
	}
	
	/**
	 * 设置重试次数
	 */
	public int incRetry() {
		return this.retry.incrementAndGet();
	}
	
	/**
	 * 获取状态
	 * @return
	 */
	public int getStatus() {
		return this.status;
	}
	
	/**
	 * 获取超时时间
	 * @return
	 */
	public int getTimeOutMillis() {
		return this.timeOutMillis;
	}
	
	/**
	 * 设置超时时间
	 * @param timeOutMillis
	 */
	public void setTimeOutMillis(int timeOutMillis) {
		this.timeOutMillis = timeOutMillis;
	}
	
	/**
	 * 获取状态变化后距现在的时间毫秒
	 * @return
	 */
	public int getTimes() {
		int times = (int) (System.currentTimeMillis() - this.time);
		return times;
	}
	
	/**
	 * 获取回调函数
	 * @return
	 */
	public InvokeCallback getInvokeCallback() {
		return invokeCallback;
	}
	/**
	 * 设置回调函数
	 * @param invokeCallback
	 */
	public void setInvokeCallback(InvokeCallback invokeCallback) {
		this.invokeCallback = invokeCallback;
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
	 * 关闭连接
	 */
	public void close() {
		this.context.close();
		this.context = null;
	}

}
