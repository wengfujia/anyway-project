/**
 * 
 */
package org.anyway.server.plugin.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.anyway.common.SystemConfig;
import org.anyway.common.enums.StatusEnum;
import org.anyway.common.future.InvokeCallback;
import org.anyway.common.future.ResponseFuture;
import org.anyway.common.models.IpTableBean;
import org.anyway.common.protocol.TcpMessageCoder;
import org.anyway.common.protocol.body.JBuffer;
import org.anyway.common.protocol.request.BaseRequest;
import org.anyway.common.protocol.request.HttpRequest;
import org.anyway.common.protocol.request.TcpRequest;
import org.anyway.common.utils.ClassUtil;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.exceptions.NoCacheException;
import org.anyway.server.adapter.cache.CacheManager;
import org.anyway.server.plugin.adapter.client.Client2Processor;

import io.netty.buffer.ByteBuf;

/**
 * @author wengfj
 *
 */
public abstract class BusinessBaseExecutor<T> implements Runnable {

	private T request;
	
	protected CacheManager getCacheManager() {
		try {
			return CacheManager.getInstance();
		} catch (NoCacheException e) {
			LoggerUtil.printInfo(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 获取request
	 * @return
	 */
	public T getRequest() {
		return request;
	}
	/**
	 * 设置request
	 * @param request
	 */
	public void setRequest(T request) {
		this.request = request;
	}
	
	public final InvokeCallback getInvokeCallback() {
		return ((BaseRequest)this.request).getInvokeCallback();
	}
	
	public void setInvokeCallback(InvokeCallback invokeCallback) {
		if (invokeCallback != getInvokeCallback()) {
			((BaseRequest)this.request).setInvokeCallback(invokeCallback);
		}
	}
	
	/**
	 * 回调
	 * @param status
	 * @param commandId
	 */
	protected final void invoke(int status, int commandId) {
		InvokeCallback invokeCallback = getInvokeCallback();
		if (null != invokeCallback) {
			invokeCallback.operationComplete(new ResponseFuture(status, commandId));
		}
	}
	
	/**
	 * 获取与数据库通讯的连接信息
	 * 如果当前request已经存在可用连接，则直接使用，否则需要从缓存获取新的连接
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IpTableBean getIpTable() {
		//获取当前request是否已经存在可用连接
		IpTableBean iptable = ((BaseRequest)request).getIpTable();
		if (null == iptable || iptable.getStatus() == StatusEnum.INVALID.getValue()) { //从缓存中获取新的可用连接
			if (request instanceof HttpRequest<?>) {
				JBuffer<String> jBody = ((HttpRequest<String>)request).getJBody();
				iptable = this.getCacheManager().getRoute(jBody.getSessionId(), ""+jBody.getCommandId());
			}
			else if (request instanceof TcpRequest) {
				TcpMessageCoder messageCoder = ((TcpRequest)request).getCStream();
				iptable = this.getCacheManager().getRoute(messageCoder.getHeader().getSessionid(), ""+messageCoder.getHeader().getCommandID());
			}
			((BaseRequest)request).setIpTable(iptable);
		}		
		//返回可用的连接信息
		return iptable;
	}
	
	/*
	 * 分解消息
	 * */
	@SuppressWarnings("unchecked")
	protected <P> byte[] decodeMessage(int commandid, P message) {	
		//获取消息处理类
		Class<?> classType = ClassUtil.getMsgClassByType(commandid);
  		if (classType==null) { //找不到，退出
  			return null;
  		}
  		
  		byte[] result = null;
  		try {
			Object invokerMessage = classType.newInstance();
			//根据类型判断
			if (message instanceof Map) {
				Method decodeMethod = classType.getMethod("decode", new Class[] { Map.class });
				result = (byte[]) decodeMethod.invoke(invokerMessage,
						new Object[] { new HashMap<String, String>((Map<String, String>) message) });
			} else if (message instanceof String) {
				Method decodeMethod = classType.getMethod("decode", new Class[] { String.class });
				result = (byte[]) decodeMethod.invoke(invokerMessage, new Object[] { new String((String) message) });
			} else if (message instanceof byte[]) {
				Method decodeMethod = classType.getMethod("decode", new Class[] { byte.class });
				result = (byte[]) decodeMethod.invoke(invokerMessage, new Object[] { (byte[]) message });
			}
		} catch (InstantiationException | InvocationTargetException e) {
			LoggerUtil.printInfo(e.getMessage());
		} catch (NoSuchMethodException | SecurityException e) {
			LoggerUtil.printInfo(e.getMessage());
		} catch (IllegalArgumentException | IllegalAccessException e) {
			LoggerUtil.printInfo(e.getMessage());
		}
  		return result;
	}
	
	/**
	 * 发送数据到数据层
	 * @param ibuffer
	 * @return
	 */
	protected int sendTo(ByteBuf ibuffer) {
		// 获取可用的hbase服务端连接
		IpTableBean iptable = this.getIpTable();
		if (null != iptable) {
			LoggerUtil.println("[route]Route to <Addr:%s,Port:%s>", iptable.getAddress(), iptable.getPort());
			// 提交到hbase服务端
			Client2Processor client = new Client2Processor(iptable.getAddress(), iptable.getPort());
			client.send(ibuffer, SystemConfig.RETRY);
			// 设置状态为等待应答
			((BaseRequest)request).setDoning();
		} else {
			// 设置状态为等待处理
			((BaseRequest)request).setWait();
		}
		return 0;
	}
	
}
