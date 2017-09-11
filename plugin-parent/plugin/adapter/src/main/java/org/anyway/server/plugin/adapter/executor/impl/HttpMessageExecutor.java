/*
 * 名称: HttpMessageExecutor
 * 描述: HTTP消息处理器，
 * 说明: 
 * 		1、先进行消息合法过滤
 * 		2、转换成网络传输包
 * 		3、判断是否有可用的hbase连接
 * 		4、有可用连接，通过socket把数据传至hbase，并把缓存移入已处理
 * 		5、如果没有可用连接，把缓存移回到等待缓存区
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 * 
 */

package org.anyway.server.plugin.adapter.executor.impl;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;

import org.anyway.common.AdapterConfig;
import org.anyway.common.SystemConfig;
import org.anyway.common.annotation.MessageAnnotation;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.protocol.TcpMessageCoder;
import org.anyway.common.protocol.HttpMessageCoder;
import org.anyway.common.protocol.body.JBuffer;
import org.anyway.common.protocol.header.CommandID;
import org.anyway.common.protocol.header.Header;
import org.anyway.common.protocol.request.HttpRequest;
import org.anyway.common.types.pstring;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.StringUtil;
import org.anyway.server.adapter.cache.CacheManager;
import org.anyway.server.plugin.adapter.AbstractHttpBusinessExecutor;
import org.anyway.server.plugin.adapter.dispatcher.Dispatcher;

@MessageAnnotation(msgType = CommandID.HTTP_REQUEST)
public class HttpMessageExecutor extends AbstractHttpBusinessExecutor {

	private CacheManager cachemanager = null;
	
	/**
	 * 消息过滤 是否有效消息：true有效，false无效
	 * 
	 * @return
	 */
	private int doFilter() {
		int status = 0;
		JBuffer<String> LoginBuf = getRequest().getJBody();

		// 判断用户名与密码
		if (StringUtil.empty(LoginBuf.getUserName())) {
			status = -18;
		} else {
			// 判断版本号
			String key = LoggerUtil.sprintf("VER.%s", LoginBuf.getSessionId());
			String ver = AdapterConfig.getInstance().GetVerValue("", key);
			if (StringUtil.empty(ver) || ver.compareTo(LoginBuf.getVersion()) > 0) {
				LoggerUtil.sprintf("错误的版本号,sessionid:%s", LoginBuf.getSessionId());
				status = -12;
			}
		}

		if (status != 0) {
			this.sendError(status);// 发送错误消息
		}
		return status;
	}

	/**
	 * 消息分解
	 */
	private int doDecode(String commandValue, ByteBuf bytebuf) {
		JBuffer<String> LoginBuf = this.getRequest().getJBody();
		
		int status = 0;
		byte[] buffer = null;
		
		//判断业务是否需要传入hbase服务端
		if ("HBASE".equalsIgnoreCase(commandValue)) { //需要转到数据库服务层处理
			String body = LoginBuf.getBody();
			if (StringUtil.empty(body)==false) {
				try {
					buffer = body.getBytes(SystemConfig.CharsetName); //转换成byte[]
				} catch (UnsupportedEncodingException e) {
					status = -23;
					LoggerUtil.printInfo(e.getMessage());
				} catch (Exception e) {
					status = -23;
					LoggerUtil.printInfo(e.getMessage());
				}
			}
		}
		else if ("DECODE".equalsIgnoreCase(commandValue)) { //需要进行解码处理
			buffer = super.decodeMessage(LoginBuf.getCommandId(), LoginBuf.getBody());
		}	
		
	  	if (status == 0) {
	  		// 组合tcp网络包,用于传输到处理层
			Header header = new Header();
			header.setCommandID(LoginBuf.getCommandId());
			header.setStatus(0);
			header.setResptype(1);
			header.setUser(LoginBuf.getUserName());
			header.setPwd(LoginBuf.getPassWord());
			header.setSessionid(LoginBuf.getSessionId());
			header.setSequence(getRequest().getID());
			header.setVersion(LoginBuf.getVersion());
			TcpMessageCoder messageCoder = new TcpMessageCoder(AdapterConfig.getInstance().getUSMaxSendBufferSize());
			if (null != buffer) {
				messageCoder.SetNr(buffer, buffer.length);
			}
			messageCoder.EncodeHeader(header);
			int len = messageCoder.LoadFromStream(bytebuf, CryptEnum.DES);
			if (len <= 0) {
				status = -23;
			}
			//清空缓存
			messageCoder.ClearStream();
			messageCoder = null;
	  	}
	  	else {
	  		this.sendError(status);
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
			int commandId = this.getRequest().getJBody().getCommandId();
			String key = LoggerUtil.sprintf("CMD.%d", commandId);
			String commandValue = AdapterConfig.getInstance().GetValue("", key);
			if (StringUtil.empty(commandValue)) { //业务头为空
				status = -20;
		  	}
			else if ("LOCAL".equalsIgnoreCase(commandValue)) { //直接处理本地业务逻辑
		  		try {
					status = Dispatcher.<HttpRequest<String>>submit(this.getRequest(), commandId,
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
	
	/**
	 * 发送错误消息
	 * @param status
	 */
	void sendError(int status) {
		//获取错误解释
  		pstring description = new pstring(), response = new pstring();
  		this.cachemanager.GetErrorInfo(status, description, response);
		super.sendResponse(HttpMessageCoder.toJsonString(-12, response.getString()));
	}
	
}
