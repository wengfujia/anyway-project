/*
 * 名称: WeixinMessageExecutor
 * 描述: 微信消息处理器，
 * 说明: 
 * 		1、先进行微信签权
 * 		2、微信消息进行分类处理，转换成业务内部可解析包（对于文本消息进行内容关键字过滤）
 * 		3、判断是否有可用的hbase连接
 * 		4、有可用连接，通过socket把数据传至hbase，并把缓存移入已处理
 * 		5、如果没有可用连接，把缓存移回到等待缓存区
 * 版本：  1.0.0
 * 作者： 翁富家
 * 日期：2015年05月28日
 * 
 * 修改：
 * 		2015.12.7
 * 		加入decode标识符，如果遇到decode的业务标识，表示需要进行单独的消息分解
 * 		2016.9.8
 * 		doDecode函数中的Dispatcher.submit改成：Dispatcher.execute
 */

package org.anyway.server.plugin.adapter.executor.impl;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.anyway.common.AdapterConfig;
import org.anyway.common.SystemConfig;
import org.anyway.common.annotation.MessageAnnotation;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.enums.SessionEnum;
import org.anyway.common.protocol.TcpMessageCoder;
import org.anyway.common.protocol.header.CommandID;
import org.anyway.common.protocol.header.Header;
import org.anyway.common.protocol.request.HttpRequest;
import org.anyway.common.types.pstring;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.StringUtil;
import org.anyway.server.adapter.cache.CacheManager;
import org.anyway.server.plugin.adapter.AbstractHttpBusinessExecutor;
import org.anyway.server.plugin.adapter.dispatcher.Dispatcher;
import org.anyway.wechat.constant.ConstantWeChat;
import org.anyway.wechat.entity.message.resp.TextMessage;
import org.anyway.wechat.service.MessageService;
import org.anyway.wechat.service.SignService;
import org.anyway.wechat.util.MessageUtil;

@MessageAnnotation(msgType = CommandID.WEIXIN_REQUEST)
public class WeixinMessageExecutor extends AbstractHttpBusinessExecutor {

	private CacheManager cachemanager = null;
	private String key;
	
	/**
	 * 消息过滤 是否有效消息：true有效，false无效
	 * 
	 * @return
	 */
	private int doFilter() {
		// 获取参数
		QueryStringDecoder queryDecoder = new QueryStringDecoder(getRequest().getUri(), true);
		Map<String, List<String>> parameters = queryDecoder.parameters();
		this.key = parameters.containsKey("key") ? parameters.get("key").get(0) : "";
		if (StringUtil.empty(this.key)) {
			getRequest().getContext().close();
			return -23;
		}
		String echoStr = parameters.containsKey("echostr") ? parameters.get("echostr").get(0) : "";
		String timeStamp = parameters.containsKey("timestamp") ? parameters.get("timestamp").get(0) : "";
		String nonce = parameters.containsKey("nonce") ? parameters.get("nonce").get(0) : "";
		String signature = parameters.containsKey("signature") ? parameters.get("signature").get(0) : "";
		@SuppressWarnings("unused")
		String msgSignature = parameters.containsKey("msg_signature") ? parameters.get("msg_signature").get(0) : "";

		String token = this.cachemanager.getConfigCache().getToken(this.key);
		int status = 0;
		// 查看是否合法
		if (StringUtil.empty(token)) { // 非法接入
			getRequest().getContext().close();
			status = -23;
		} else if (false == SignService.checkSignature(token, signature, timeStamp, nonce)) {
			getRequest().getContext().close();
			status = -23;
		} else if (getRequest().getHttpMethod().equals(HttpMethod.GET)) { // get方式
			echoStr = StringUtil.empty(echoStr) ? this.cachemanager.getConfigCache().getErrorDescsCache().get(-11).getResponse()
					: echoStr;
			super.sendResponse(echoStr);
			status = -23;
		}
		return status;
	}
	
	/**
	 * 分解消息
	 * @param bytebuf
	 * @param requestMap 微信请求包
	 * @return 状态 0成功，其余不成功
	 */
	private int doDecode(ByteBuf bytebuf, Map<String, String> requestMap) {

		int status = 0;
		int commandId = -1;
		
		byte[] buffer = null;

		commandId = Integer.parseInt(requestMap.get("commandId"));
		//判断业务是否需要传入hbase服务端
		String key = LoggerUtil.sprintf("CMD.%d", commandId);
		key = AdapterConfig.getInstance().GetValue("", key);
	  	if (StringUtil.empty(key)) { //业务头为空
	  		status = -20;
	  	}
	  	else if ("HBASE".equalsIgnoreCase(key) ) { //需要转到数据库服务层处理
	  		String content = requestMap.get("Content");
	  		if (StringUtil.empty(content)==false) {
	  			try {		  			
					buffer = content.getBytes(SystemConfig.CharsetName);
				} catch (UnsupportedEncodingException e) {
					status = -23;
					LoggerUtil.printInfo(e.getMessage());
				} catch (Exception e) {
					status = -23;
					LoggerUtil.printInfo(e.getMessage());
				}
	  		}
		}
	  	else if ("DECODE".equalsIgnoreCase(key)) { //需要进行解码处理
  			buffer = super.decodeMessage(commandId, requestMap);
  		}
		
	  	//返回结果
	  	if (status == 0) {
	  		TcpMessageCoder result = new TcpMessageCoder(AdapterConfig.getInstance().getUSMaxSendBufferSize());
			if (null != buffer) {
				result.SetNr(buffer, buffer.length); 
			}
			//设置包头
	  		//获取版本号
	  		String ver = LoggerUtil.sprintf("VER.%s", String.valueOf(SessionEnum.WX.getSessionId()));
	  		ver = AdapterConfig.getInstance().GetVerValue("", ver);
			Header header = new Header();
			header.setCommandID(commandId);
			header.setStatus(0);
			header.setResptype(1);
			header.setUser(requestMap.get("FromUserName"));
			header.setSessionid(String.valueOf(SessionEnum.WX.getSessionId())); // weixin
			header.setSequence(this.getRequest().getID());
			header.setVersion(ver);
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
			//获取错误解释
	  		pstring description = new pstring(), response = new pstring();
	  		this.cachemanager.GetErrorInfo(status, description, response);
	  		//组合微信文本包
	  		TextMessage textMessage = (TextMessage) MessageService.bulidBaseMessage(requestMap, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
		  	textMessage.setContent(response.getString());
			String echostr = MessageService.bulidSendMessage(textMessage, ConstantWeChat.REQ_MESSAGE_TYPE_TEXT);
			super.sendResponse(echostr);
	  	}
		return status;
	}

	private int route(Map<String, String> requestMap) {
		ByteBuf ibuffer = this.getRequest().getContext().alloc().buffer();
		int status = doDecode(ibuffer, requestMap); // 进行解码，转换成内部业务消息包
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
		if (null == this.getCacheManager()) {
			return;
		}

		cachemanager = this.getCacheManager();
		int status = doFilter();
		if (status == 0) {
			// 分解微信内容成map
			byte[] buffer = this.getRequest().getJBody().getBody().getBytes();	
			Map<String, String> requestMap = MessageUtil.parseXml(new ByteArrayInputStream(buffer));
			// 把微信消息转换成内部业务消息
			//根据weixin.xml中的commandids的配置，获取业务头
			String commandId = this.cachemanager.getConfigCache().getWeixinCommandid(this.key, requestMap);
			if (StringUtil.empty(commandId)) {
				return;
			}
			//判断业务是否需要传入hbase服务端
			String commandValue = LoggerUtil.sprintf("CMD.%s", commandId);
			commandValue = AdapterConfig.getInstance().GetValue("", commandValue);
		  	if (StringUtil.empty(commandValue)) { //业务头为空
		  		status = -20;
		  	}
		  	else if ("LOCAL".equalsIgnoreCase(commandValue)) { //直接处理本地业务逻辑
		  		try {
		  			status = Dispatcher.<HttpRequest<String>>submit(this.getRequest(), Integer.parseInt(commandId));
				} catch (InstantiationException | IllegalAccessException e) {
					status = -23;
					LoggerUtil.printInfo(e.getMessage());
				} catch (Exception e) {
					status = -23;
					LoggerUtil.printInfo(e.getMessage());
				}
		  	}
		  	else {
		  		requestMap.put("commandId", commandId);
		  		requestMap.put("commandValue", commandValue);
		  		route(requestMap);
		  		return;
		  	}
		}
		this.cachemanager.getRequestCache().removeDone(this.getRequest().getID());
		return;// status;
	}
	
}
