/*
 * 名称: HTTPREQUEST.java
 * 描述: Http封装类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月29日
 * 修改日期:
 */

package org.anyway.server.data.packages;

import org.anyway.common.utils.uStringUtil;
import org.anyway.server.data.models.IpTableBean;
import org.anyway.server.data.packages.json.JBuffer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

@SuppressWarnings("serial")
public class HTTPREQUEST<T> implements java.io.Serializable {
	
	private IpTableBean iptable;
	private String id;
	private FullHttpRequest fullRequest;
	private ChannelHandlerContext context;
	private DecoderResult decoderResult;
	private String uri;
	private HttpMethod httpMethod;
	private JBuffer<T> body; 
	private int retry;	//重试次数
	private int status;
	private long time;
	
	/**
	 * 构造函数
	 */
	public HTTPREQUEST() {
		this.iptable = null;
		this.id = String.valueOf(this.time + Long.valueOf(uStringUtil.getRandom()));
		this.status = 0; //等待状态
		this.time = System.currentTimeMillis();
		this.retry = 1;
		//this.body = new JBuffer<String>();
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
	 * UUID号表示接连唯一号，做为key
	 * @return
	 */
	public String getID() {
		return this.id;
	}
	
	/**
	 * 获取request
	 * @return
	 */
	public FullHttpRequest getRequest() {
		return this.fullRequest;
	}
	/**
	 * 设置request
	 * @param request
	 */
	public void setRequest(FullHttpRequest request) {
		this.fullRequest = request;
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
	 * 获取或设置解码结果
	 * @return
	 */
	public DecoderResult getDecoderResult() {
		return this.decoderResult;
	}
	public void setDecoderResult(DecoderResult decoderResult) {
		this.decoderResult = decoderResult;
	}

	/**
	 * 设置消息标识号
	 * @param commandid
	 */
	public void setCommandID(int commandid) {
		this.body.setCommandId(commandid);
	}
	
	/**
	 * 获取或设置 request内容
	 * @return
	 */
	public JBuffer<T> getJBody() {
		return this.body;
	}
	public void setJBody(JBuffer<T> jbody) {
		if (null != this.body) {
			this.body.Clear();
			this.body = null;	
		}		
		this.body = jbody;
	}
	
	/**
	 * 获取或设置 uri
	 * @return
	 */
	public String getUri() {
		return this.uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	/**
	 * 获取或设置 request方式
	 * @return
	 */
	public HttpMethod getHttpMethod() {
		return this.httpMethod;
	}
	public void setHttpMethod(HttpMethod method) {
		this.httpMethod = method;
	}
	
	/**
	 * 设置等待处理
	 */
	public void setWait() {
		this.status = 0;
		this.time = System.currentTimeMillis();
		this.retry++;
	}
	
	/**
	 * 设置已经处理，等待消息应答
	 */
	public void setDoning() {
		this.status = 1;
		this.time = System.currentTimeMillis();
	}
	
	/**
	 * 设置消息已经收到应答，等待接收消息处理结果
	 */
	public void setDone() {
		this.status = 2;
		this.time = System.currentTimeMillis();
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
	 * 重试次数
	 * @return
	 */
	public int getRetry() {
		return this.retry;
	}
	
	/**
	 * 获取状态
	 * @return
	 */
	public int getStatus() {
		return this.status;
	}
	
	/**
	 * 获取状态变化后距现在的时间秒数
	 * @return
	 */
	public int getTimes() {
		int times = (int) ((System.currentTimeMillis() - this.time) / 1000);
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
