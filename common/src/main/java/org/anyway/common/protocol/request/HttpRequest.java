/*
 * 名称: HTTPREQUEST.java
 * 描述: Http封装类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月29日
 * 修改日期:
 */

package org.anyway.common.protocol.request;

import java.util.concurrent.atomic.AtomicInteger;

import org.anyway.common.protocol.body.JBuffer;
import org.anyway.common.utils.StringUtil;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

public class HttpRequest<T> extends BaseRequest {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4199609788436795565L;
	
	private FullHttpRequest fullRequest;
	private DecoderResult decoderResult;
	private String uri;
	private HttpMethod httpMethod;
	private JBuffer<T> body; 
	
	/**
	 * 构造函数
	 */
	public HttpRequest() {
		this.id = StringUtil.getUUID();
		setIpTable(null);
		this.status = 0; //等待状态
		this.time = System.currentTimeMillis();
		this.retry = new AtomicInteger(0);
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
	
}
