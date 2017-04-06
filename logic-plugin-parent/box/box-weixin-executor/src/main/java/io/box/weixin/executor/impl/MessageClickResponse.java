/*
 * 名称: MessageClickResponse
 * 描述: 点微信消息菜单的返回处理
 * 说明: 
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package io.box.weixin.executor.impl;

import io.box.weixin.executor.AbstractWeixinMessageResponse;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.anyway.common.MessageAnnotation;
import org.anyway.server.api.CSHTMsgStream;
import org.anyway.server.data.packages.COMMANDID;
import org.anyway.wechat.constant.ConstantWeChat;
import org.anyway.wechat.entity.message.resp.Article;
import org.anyway.wechat.entity.message.resp.NewsMessage;
import org.anyway.wechat.entity.message.resp.TextMessage;
import org.anyway.wechat.service.MessageService;
import org.anyway.wechat.util.MessageUtil;

@MessageAnnotation(msgType = COMMANDID.WEIXIN_MSG_CLICK)
public class MessageClickResponse extends AbstractWeixinMessageResponse {
	
	@Override
	public Integer call() {
		return super.call();
	}
	
	/**
	 * 组合成微信包
	 */
	@Override
	protected String msgEncode() {
		String respMessage = "";
		
		byte[] buffer = this.httprequest.getJBody().getBody().getBytes();
		Map<String, String> requestMap = MessageUtil.parseXml(new ByteArrayInputStream(buffer));
		
		CSHTMsgStream cstream = this.getRequest().getCStream();
		if (cstream.GetStatus() == 0) {
			NewsMessage newsMessage = (NewsMessage) MessageService.bulidBaseMessage(requestMap, ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);
			List<Article> articleList = new ArrayList<Article>();
			
			//名称\t描述\tpicurl\turl
			for (int i=0; i<cstream.GetRows().length; i++) {
				String[] fields = cstream.GetRow(i);
				Article article = new Article();
				article.setTitle(fields[0]);
				article.setDescription(fields[1]);
				article.setPicUrl(fields[2]);
				article.setUrl(fields[3]);
				articleList.add(article);
			}
			newsMessage.setArticleCount(articleList.size());
			newsMessage.setArticles(articleList);
			respMessage = MessageService.bulidSendMessage(newsMessage, ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);
		}
		else {
			//获取错误解释
	  		//pstring description = new pstring(), response = new pstring();
	  		//this.manager.GetErrorInfo(cstream.GetStatus(), description, response);
	  		//组合微信文本包
	  		TextMessage textMessage = (TextMessage) MessageService.bulidBaseMessage(requestMap, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
		  	textMessage.setContent(cstream.GetString());
		  	respMessage = MessageService.bulidSendMessage(textMessage, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
		} 
		return respMessage;
	}
}
