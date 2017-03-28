/*
 * 名称: FastJoinResponse
 * 描述: 根据认证码加入班级
 * 说明: 
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年09月15日
 */

package io.box.weixin.executor.impl;

import io.box.weixin.executor.AbstractWeixinMessageResponse;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.anyway.wechat.constant.ConstantWeChat;
import org.anyway.wechat.entity.message.resp.Article;
import org.anyway.wechat.entity.message.resp.NewsMessage;
import org.anyway.wechat.entity.message.resp.TextMessage;
import org.anyway.wechat.service.MessageService;
import org.anyway.wechat.util.MessageUtil;
import org.anyway.common.MessageAnnotation;
import org.anyway.server.api.CSHTMsgStream;
import org.anyway.server.data.packages.COMMANDID;

@MessageAnnotation(msgType = COMMANDID.CLASS_JOIN_VALIDATECODE)
public class FastJoinResponse extends AbstractWeixinMessageResponse {
	
	@Override
	public void run() {
		super.run();
	}
	
	/**
	 * 组合成微信包
	 * 班级编号\t状态 （1表示该用户资料存在，0表示不存在）
	 */
	@Override
	protected String msgEncode() {
		String respMessage = "";
		
		byte[] buffer = this.httprequest.getJBody().getBody().getBytes();
		Map<String, String> requestMap = MessageUtil.parseXml(new ByteArrayInputStream(buffer));
		
		CSHTMsgStream cstream = this.getRequest().getCStream();	
		if (cstream.getHeader().getStatus() == 0) {
			String[] results = cstream.GetRow(0);
			if (null != results && results.length == 2) {
				if (results[0].equals("")) {
					TextMessage textMessage = (TextMessage) MessageService.bulidBaseMessage(requestMap, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
				  	textMessage.setContent("非法认证码或认证码已经失败，请确认后重试。");
				  	respMessage = MessageService.bulidSendMessage(textMessage, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);	
				}
				else if (results[1].equals("0")) { //表示用户资料不存在
					NewsMessage newsMessage = (NewsMessage) MessageService.bulidBaseMessage(requestMap, ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);
					List<Article> articleList = new ArrayList<Article>();
					
					Article article = new Article();
					article.setTitle("请点我填写您的个人资料");
					article.setDescription("");
					article.setPicUrl("");
					article.setUrl("http://60.12.230.85/weixin/join/class/"+newsMessage.getToUserName()+"/"+results[0]);
					articleList.add(article);
					
					newsMessage.setArticleCount(1);
					newsMessage.setArticles(articleList);
					respMessage = MessageService.bulidSendMessage(newsMessage, ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);	
				}
				else {
					TextMessage textMessage = (TextMessage) MessageService.bulidBaseMessage(requestMap, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
				  	textMessage.setContent("加入成功，感谢您的支持！");
				  	respMessage = MessageService.bulidSendMessage(textMessage, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
				}
			}
		}
		else {
			TextMessage textMessage = (TextMessage) MessageService.bulidBaseMessage(requestMap, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
		  	textMessage.setContent(cstream.GetString());
		  	respMessage = MessageService.bulidSendMessage(textMessage, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
		}
		
		return respMessage;
	}
}
