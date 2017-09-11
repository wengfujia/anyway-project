/*
 * 名称: SubscribeResponse
 * 描述: 定阅消息处理
 * 说明: 
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package io.box.weixin.executor.impl;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.anyway.common.annotation.MessageAnnotation;
import org.anyway.server.plugin.adapter.executor.AbstractWeixinMessageLocalResponse;
import org.anyway.wechat.constant.ConstantWeChat;
import org.anyway.wechat.entity.message.resp.TextMessage;
import org.anyway.wechat.service.MessageService;
import org.anyway.wechat.util.MessageUtil;

import io.box.common.CommandID;

@MessageAnnotation(msgType = CommandID.WEIXIN_UNSUBSCRIBE)
public class UnsubscribeResponse extends AbstractWeixinMessageLocalResponse {
	
	@Override
	public void run() {
		super.run();
	}
	
	/**
	 * 返回定阅欢迎消息
	 */
	@Override
	protected String msgEncode() {
		byte[] buffer = this.getRequest().getJBody().getBody().getBytes();
		Map<String, String> requestMap = MessageUtil.parseXml(new ByteArrayInputStream(buffer));
		
		TextMessage textMessage = (TextMessage) MessageService.bulidBaseMessage(requestMap, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
		textMessage.setContent("感谢您一路对我们的支持.");
		String respMessage = MessageService.bulidSendMessage(textMessage, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
		return respMessage;
	}
	
}
