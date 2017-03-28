/**
 * 执行http业务逻辑的基类
 * 实现Runnable接口
 * 
 * @author wfj
 *
 */

package org.anyway.server.web.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.anyway.common.utils.uClassUtil;
import org.anyway.common.utils.uLogger;
import org.anyway.exceptions.NoCacheException;
import org.anyway.server.data.models.IpTableBean;
import org.anyway.server.data.packages.HTTPREQUEST;
import org.anyway.server.web.cache.CacheManager;
import org.anyway.server.web.common.enums.StatusEnum;
import org.anyway.server.web.http.handler.ResponseHandler;

public abstract class HttpBusinessExecutorBase implements Runnable {

	private HTTPREQUEST<String> request;
	
	public CacheManager getCacheManager() {
		try {
			return CacheManager.getInstance();
		} catch (NoCacheException e) {
			uLogger.printInfo(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 设置request
	 * @return
	 */
	public HTTPREQUEST<String> getRequest() {
		return request;
	}
	public void setRequest(HTTPREQUEST<String> request) {
		this.request = request;
	}
	
	/**
	 * 获取与数据库通讯的连接信息
	 * 如果当前request已经存在可用连接，则直接使用，否则需要从缓存获取新的连接
	 * @return
	 */
	public IpTableBean getIpTable() {
		//获取当前request是否已经存在可用连接
		IpTableBean iptable = this.getRequest().getIpTable();
		if (null == iptable || iptable.getStatus() == StatusEnum.INVALID.getValue()) { //从缓存中获取新的可用连接
			iptable = this.getCacheManager().getIpTable();
			this.request.setIpTable(iptable);
		}		
		//返回可用的连接信息
		return iptable;
	}
	
	@Override
	public void run() {
		
	}
	
	/*
	 * 分解消息
	 * */
	@SuppressWarnings("unchecked")
	protected<T> byte[] decodeMessage(int commandid, T message) {	
		//获取消息处理类
		Class<?> classType = uClassUtil.getMsgClassByType(commandid);
  		if (classType==null) { //找不到，退出
  			return null;
  		}
  		
  		byte[] result = null;
  		try {
			Object invokerMessage = classType.newInstance();
			//根据类型判断
			if (message instanceof Map ) {
				Method decodeMethod = classType.getMethod("decode", new Class[] { Map.class });
				result = (byte[]) decodeMethod.invoke(invokerMessage, new Object[] {  
				        new HashMap<String, String>((Map<String, String>)message) });
			}
			else if (message instanceof String ) {
				Method decodeMethod = classType.getMethod("decode", new Class[] { String.class });
				result = (byte[]) decodeMethod.invoke(invokerMessage, new Object[] {  
				        new String((String)message) });
			}
			
		} catch (InstantiationException | InvocationTargetException e) {
			uLogger.printInfo(e.getMessage());
		} catch (NoSuchMethodException | SecurityException e) {
			uLogger.printInfo(e.getMessage());
		} catch (IllegalArgumentException | IllegalAccessException e) {
			uLogger.printInfo(e.getMessage());
		}
  		return result;
	}
	
	/**
	 * 返回结果
	 * 
	 * @param content
	 */
	protected void sendResponse(String content) {
		ResponseHandler.writeResponse(content, this.request.getContext(), this.request.getRequest());
		
//		boolean isKeepAlive = new ResponseHandler().writeResponse(content, getRequest().getContext(),
//				getRequest().getIsKeepAlive(), getRequest().getDecoderResult());
//		// If keep-alive is off, close the connection once the content is fully
//		// written.
//		if (!isKeepAlive) {
//			getRequest().getContext().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//		}
	}
}