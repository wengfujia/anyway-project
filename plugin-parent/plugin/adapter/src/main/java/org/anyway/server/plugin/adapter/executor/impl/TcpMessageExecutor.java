/*
 * 名称: TcpMessageExecutor
 * 描述: TCP消息处理器，
 * 说明: 
 * 		1、先进行消息合法过滤
 * 		2、转换成网络传输包
 * 		3、判断是否有可用的hbase连接
 * 		4、有可用连接，通过socket把数据传至hbase，并把缓存移入已处理
 * 		5、如果没有可用连接，把缓存移回到等待缓存区
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2017年07月29日
 * 
 */

package org.anyway.server.plugin.adapter.executor.impl;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;

import java.lang.reflect.InvocationTargetException;

import org.anyway.common.AdapterConfig;
import org.anyway.common.annotation.MessageAnnotation;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.protocol.TcpMessageCoder;
import org.anyway.common.protocol.header.CommandID;
import org.anyway.common.protocol.header.Header;
import org.anyway.common.protocol.request.TcpRequest;
import org.anyway.common.types.pint;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.server.adapter.cache.CacheManager;
import org.anyway.server.plugin.adapter.AbstractTcpBusinessExecutor;
import org.anyway.server.plugin.adapter.dispatcher.Dispatcher;
import org.apache.commons.beanutils.BeanUtils;

@MessageAnnotation(msgType = CommandID.TCP_REQUEST)
public class TcpMessageExecutor extends AbstractTcpBusinessExecutor {

	private CacheManager cachemanager = null;
	
	/**
	 * 消息过滤 是否有效消息：true有效，false无效
	 * 
	 * @return
	 */
	private int doFilter() {
		int status = 0;
		
		TcpMessageCoder messageCoder = getRequest().getCStream();
		if ( messageCoder == null) {
			status = -23;
		}
		
		if (StringUtil.isNullOrEmpty(messageCoder.getHeader().getUser())) {
			status = -18;
		}
		
		if (status != 0) {
			Response(messageCoder.getHeader());
		}
		
		return status;
	}

	/**
	 * 消息分解
	 */
	private int doDecode(String commandValue, ByteBuf bytebuf) {
		int status = 0;
		byte[] buffer = null;
		
		TcpMessageCoder messageCoder = getRequest().getCStream();
		//判断业务是否需要传入hbase服务端
		if ("HBASE".equalsIgnoreCase(commandValue)) { //需要转到数据库服务层处理
			pint len = new pint();
			buffer = messageCoder.GetNr(len);
		}
		else if ("DECODE".equalsIgnoreCase(commandValue)) { //需要进行解码处理
			buffer = super.decodeMessage(messageCoder.GetCommand(), messageCoder.GetString());
		}	
		
	  	if (status == 0) {
	  		// 组合tcp网络包,用于传输到处理层
			Header header = null;
			try {
				header = (Header) BeanUtils.cloneBean(messageCoder.getHeader());
			} catch (IllegalAccessException | InstantiationException | InvocationTargetException
					| NoSuchMethodException e) {
				LoggerUtil.getLogger().error("TcpMessageExecutor clone header error{}", e);
				return -23;
			}
			header.setStatus(0);
			header.setResptype(1);
			header.setSequence(getRequest().getID());
			TcpMessageCoder result = new TcpMessageCoder(AdapterConfig.getInstance().getUSMaxSendBufferSize());
			if (null != buffer) {
				result.SetNr(buffer, buffer.length);
			}
			result.EncodeHeader(header);
			int len = result.LoadFromStream(bytebuf, CryptEnum.DES);
			if (len <= 0) {
				status = -23;
			}
			//清空缓存
			result.ClearStream();
			result = null;
	  	}
	  	else {
	  		Response(messageCoder.getHeader());
	  	}
		return status;
	}
	
	/**
	 * 跳转到数据层
	 * @param commandValue
	 * @return
	 */
	private int route(String commandValue) {
		ByteBuf ibuffer = this.getRequest().getContext().alloc().buffer();
		int status = doDecode(commandValue, ibuffer); // 进行解码，转换成内部业务消息包
		if (status == 0) {
			// 发送到数据层
			status = sendTo(ibuffer);
			this.cachemanager.getRequestCache().replaceDone(this.getRequest());
		}
		return status;
	}

	/**
	 * 执行业务
	 */
	@Override
	public void run() {
		//判断缓存是否有效并连接是否合法
		if (null == this.getCacheManager() || null == this.getRequest()) {
			LoggerUtil.printInfo("找不到相应的缓存");
			invoke(-10, CommandID.HTTP_REQUEST);
			return;
		}

		cachemanager = this.getCacheManager();
		int status = doFilter();
		if (status == 0) {
			int commandId = ((TcpRequest)this.getRequest()).getCStream().getHeader().getCommandID();
			String key = LoggerUtil.sprintf("CMD.%d", commandId);
			String commandValue = AdapterConfig.getInstance().GetValue("", key);
			if (StringUtil.isNullOrEmpty(commandValue)) { //业务头为空
				status = -20;
		  	}
			else if ("LOCAL".equalsIgnoreCase(commandValue)) { //直接处理本地业务逻辑
		  		try {
					status = Dispatcher.<TcpRequest>submit(this.getRequest(), commandId,
							super.getInvokeCallback());
				} catch (InstantiationException | IllegalAccessException e) {
					status = -23;
					LoggerUtil.getLogger().error("HTTP request process error:{}", e);
				} catch (Exception e) {
					status = -23;
					LoggerUtil.getLogger().error("HTTP request process error:{}", e);
				}
		  		invoke(status, CommandID.HTTP_REQUEST);
		  		return;
		  	}
			else {
				route(commandValue);
				return;
			}
		}
		this.cachemanager.getRequestCache().removeDone(this.getRequest().getID());
		invoke(status, CommandID.HTTP_REQUEST);
		return;
	}
	
}
