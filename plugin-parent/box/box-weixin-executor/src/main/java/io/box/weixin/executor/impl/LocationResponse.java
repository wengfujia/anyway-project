/*
 * 名称: LocationResponse
 * 描述: 获取地理位置
 * 说明: 如果是家长，则只保存当前位置，如果是老师只要保存历史位置
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年09月15日
 */

package io.box.weixin.executor.impl;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.anyway.wechat.constant.ConstantWeChat;
import org.anyway.wechat.entity.message.resp.TextMessage;
import org.anyway.wechat.service.MessageService;
import org.anyway.wechat.util.MessageUtil;
import org.anyway.common.annotation.MessageAnnotation;
import org.anyway.common.protocol.TcpMessageCoder;
import org.anyway.server.plugin.adapter.executor.AbstractWeixinMessageResponse;

import io.box.common.CommandID;

@MessageAnnotation(msgType = CommandID.WEIXIN_LOCATION)
public class LocationResponse extends AbstractWeixinMessageResponse {
	
	@Override
	public void run() {
		super.run();
	}
	
	/**
	 * 组合成微信包
	 */
	@Override
	protected String msgEncode() {
		String respMessage = "";
		
		byte[] buffer = this.httprequest.getJBody().getBody().getBytes();
		Map<String, String> requestMap = MessageUtil.parseXml(new ByteArrayInputStream(buffer));
		
		TcpMessageCoder cstream = this.getRequest().getCStream();
		TextMessage textMessage = (TextMessage) MessageService.bulidBaseMessage(requestMap, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
	  	textMessage.setContent(cstream.GetString());
	  	respMessage = MessageService.bulidSendMessage(textMessage, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
		return respMessage;
	}
}
