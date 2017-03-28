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

package io.box.weixin.executor;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.anyway.common.MessageAnnotation;
import org.anyway.common.uConfigVar;
import org.anyway.common.uGlobalVar;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.types.pstring;
import org.anyway.common.utils.uLogger;
import org.anyway.common.utils.uStringUtil;
import org.anyway.server.web.cache.CacheManager;
import org.anyway.server.web.common.uLoadVar;
import org.anyway.server.web.common.enums.SessionEnum;
import org.anyway.server.web.dispatcher.Dispatcher;
import org.anyway.server.web.factory.HttpBusinessExecutorBase;
import org.anyway.server.api.CSHTMsgStream;
import org.anyway.server.data.models.IpTableBean;
import org.anyway.server.data.packages.COMMANDID;
import org.anyway.server.data.packages.HEADER;
import org.anyway.server.data.packages.HTTPREQUEST;
import org.anyway.wechat.constant.ConstantWeChat;
import org.anyway.wechat.entity.message.resp.TextMessage;
import org.anyway.wechat.service.MessageService;
import org.anyway.wechat.service.SignService;
import org.anyway.wechat.util.MessageUtil;
import org.anyway.client.TcpClient;

@MessageAnnotation(msgType = COMMANDID.WEIXIN_REQUEST)
public class WeixinMessageExecutor extends HttpBusinessExecutorBase {

	private CacheManager cachemanager = null;
	private String key;
	
	/**
	 * 消息过滤 是否有效消息：true有效，false无效
	 * 
	 * @return
	 */
	private Boolean doFilter() {
		Boolean isSucess = true;
		// 获取参数
		QueryStringDecoder queryDecoder = new QueryStringDecoder(getRequest().getUri(), true);
		Map<String, List<String>> parameters = queryDecoder.parameters();
		this.key = parameters.containsKey("key") ? parameters.get("key").get(0) : "";
		if (uStringUtil.empty(this.key)) {
			getRequest().getContext().close();
			return false;
		}
		String echoStr = parameters.containsKey("echostr") ? parameters.get("echostr").get(0) : "";
		String timeStamp = parameters.containsKey("timestamp") ? parameters.get("timestamp").get(0) : "";
		String nonce = parameters.containsKey("nonce") ? parameters.get("nonce").get(0) : "";
		String signature = parameters.containsKey("signature") ? parameters.get("signature").get(0) : "";
		@SuppressWarnings("unused")
		String msgSignature = parameters.containsKey("msg_signature") ? parameters.get("msg_signature").get(0) : "";

		String token = this.cachemanager.getConfigCache().getToken(this.key);
		// 查看是否合法
		if (uStringUtil.empty(token)) { // 非法接入
			getRequest().getContext().close();
			isSucess = false;
		} else if (false == SignService.checkSignature(token, signature, timeStamp, nonce)) {
			getRequest().getContext().close();
			isSucess = false;
		} else if (getRequest().getHttpMethod().equals(HttpMethod.GET)) { // get方式
			echoStr = uStringUtil.empty(echoStr) ? this.cachemanager.getDbCache().ErrorDescsCache().get(-11).getResponse()
					: echoStr;
			super.sendResponse(echoStr);
			isSucess = false;
		}
		return isSucess;
	}
	
	/**
	 * 分解消息
	 * @param bytebuf
	 * @return 状态 0成功，其余不成功
	 */
	private int doDecode(ByteBuf bytebuf) {
		int status = 0;
		int commandId = -1;
		
		// 分解微信内容成map
		byte[] buffer = this.getRequest().getJBody().getBody().getBytes();	
		Map<String, String> requestMap = MessageUtil.parseXml(new ByteArrayInputStream(buffer));
		// 把微信消息转换成内部业务消息
		String msgType = requestMap.get("MsgType").toLowerCase();
		//对文本消息进行关键字过滤
		if (msgType.equalsIgnoreCase(ConstantWeChat.REQ_MESSAGE_TYPE_TEXT)) {
			// 判断是否有非法关键字
			if (this.cachemanager.getDbCache().hasStopWord(requestMap.get("Content"))) {
				status = -17;
			}
		}
		
		buffer = null;
		//根据weixin.xml中的commandids的配置，获取业务头
		String commandValue = this.cachemanager.getConfigCache().getWeixinCommandid(this.key, requestMap);
		if (uStringUtil.empty(commandValue)) {
			status = -20;
		}
		else {
			commandId = Integer.parseInt(commandValue);
			//判断业务是否需要传入hbase服务端
			String key = uLogger.sprintf("CMD.%d", commandId);
			commandValue = uLoadVar.GetValue("", key);
		  	if (uStringUtil.empty(commandValue)) { //业务头为空
		  		status = -20;
		  	}
		  	else if (commandValue.equalsIgnoreCase("HBASE") ) { //需要转到数据库服务层处理
		  		String content = requestMap.get("Content");
		  		if (uStringUtil.empty(content)==false) {
		  			try {		  			
						buffer = content.getBytes(uConfigVar.CharsetName);
					} catch (UnsupportedEncodingException e) {
						status = -23;
						uLogger.printInfo(e.getMessage());
					} catch (Exception e) {
						status = -23;
						uLogger.printInfo(e.getMessage());
					}
		  		}
			}
		  	else if (commandValue.equalsIgnoreCase("LOCAL")) { //直接处理本地业务逻辑
		  		try {
					Dispatcher.<HTTPREQUEST<String>>execute(this.getRequest(), commandId);
					return -1;
				} catch (InstantiationException | IllegalAccessException e) {
					status = -23;
					uLogger.printInfo(e.getMessage());
				} catch (Exception e) {
					status = -23;
					uLogger.printInfo(e.getMessage());
				}
		  	}
		  	else if (commandValue.equalsIgnoreCase("DECODE")) { //需要进行解码处理
	  			buffer = super.decodeMessage(commandId, requestMap);
	  		}
		}
		
	  	//返回结果
	  	if (status == 0) {
	  		CSHTMsgStream result = new CSHTMsgStream();
			if (null != buffer) {
				result.SetNr(buffer, buffer.length); 
			}
			//设置包头
	  		//获取版本号
	  		String key = uLogger.sprintf("VER.%s", String.valueOf(SessionEnum.WX.getSessionId()));
	  		String ver = uLoadVar.GetVerValue("", key);
			HEADER header = new HEADER();
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
		if (doFilter() == true) {
			ByteBuf ibuffer = this.getRequest().getContext().alloc().buffer();
			int status = doDecode(ibuffer); // 进行解码，转换成内部业务消息包
			if (status == 0) {
				// 获取可用的hbase服务端连接
				IpTableBean iptable = this.getIpTable();
				if (null != iptable) {
					// 提交到hbase服务端
					TcpClient client = new TcpClient(iptable.getAddress(), iptable.getPort());
					client.send(ibuffer, uGlobalVar.RETRY);
					// 设置状态为等待应答
					this.getRequest().setDoning();
				} else {
					// 设置状态为等待处理
					this.getRequest().setWait();
				}
				this.cachemanager.getHttpCache().replaceDone(this.getRequest());
				return;
			} else if (status == -1) { //处理本地业务逻辑
				return;
			}
		}
		this.cachemanager.getHttpCache().removeDone(this.getRequest().getID());
	}
	
}
