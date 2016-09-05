/*
 * 名称: SubscribeResponse
 * 描述: 定阅消息处理
 * 说明: 
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package io.box.weixin.executor;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.anyway.common.MessageAnnotation;
import org.anyway.server.data.packages.COMMANDID;
import org.anyway.wechat.constant.ConstantWeChat;
import org.anyway.wechat.entity.message.resp.TextMessage;
import org.anyway.wechat.service.MessageService;
import org.anyway.wechat.util.MessageUtil;

import io.box.weixin.executor.impl.WeixinMessageLocalResponse;

@MessageAnnotation(msgType = COMMANDID.WEIXIN_SUBSCRIBE)
public class SubscribeResponse extends WeixinMessageLocalResponse {
	
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
		textMessage.setContent("感谢您关注！\n【申请】注册您的个人信息，加入你所在的学校或班级.\n【消息】接收最新消息、公告与咨谒.\n【我的】个人空间、活动相册、历史消息与咨询.");
		String respMessage = MessageService.bulidSendMessage(textMessage, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
		return respMessage;
	}
	
}
