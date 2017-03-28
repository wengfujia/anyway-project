/*
 * 名称: RegisterClickResponse
 * 描述: 点微信申请菜单的返回处理
 * 说明: 
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package io.box.weixin.executor.impl;

import io.box.weixin.executor.AbstractWeixinMessageLocalResponse;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.anyway.common.MessageAnnotation;
import org.anyway.server.data.packages.COMMANDID;
import org.anyway.wechat.constant.ConstantWeChat;
import org.anyway.wechat.entity.message.resp.Article;
import org.anyway.wechat.entity.message.resp.NewsMessage;
import org.anyway.wechat.service.MessageService;
import org.anyway.wechat.util.MessageUtil;

@MessageAnnotation(msgType = COMMANDID.WEIXIN_REG_CLICK)
public class RegisterClickResponse extends AbstractWeixinMessageLocalResponse {
	
	@Override
	public void run() {
		super.run();
	}
	
	@Override
	protected String msgEncode() {
		byte[] buffer = this.getRequest().getJBody().getBody().getBytes();
		Map<String, String> requestMap = MessageUtil.parseXml(new ByteArrayInputStream(buffer));
		
		NewsMessage newsMessage = (NewsMessage) MessageService.bulidBaseMessage(requestMap, ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);
	
		Article article1 = new Article();
		article1.setTitle("申请介绍");
		article1.setDescription("");
		article1.setPicUrl("http://60.12.230.85/resources/images/weixin/regist.jpg");
		article1.setUrl("");
		Article article2 = new Article();
		article2.setTitle("申请学校");
		article2.setDescription("");
		article2.setPicUrl("http://60.12.230.85/resources/images/weixin/regist_school.jpg");
		article2.setUrl("http://60.12.230.85/weixin/" + requestMap.get("FromUserName") + "/add/school");

		Article article3 = new Article();
		article3.setTitle("申请班级");
		article3.setDescription("");
		article3.setPicUrl("http://60.12.230.85/resources/images/weixin/regist_class.jpg");
		article3.setUrl("http://60.12.230.85/weixin/" + requestMap.get("FromUserName") + "/choice/school/add");
		
		Article article4 = new Article();
		article4.setTitle("加入班级");
		article4.setDescription("");
		article4.setPicUrl("http://60.12.230.85/resources/images/weixin/join_class.jpg");
		article4.setUrl("http://60.12.230.85/weixin/" + requestMap.get("FromUserName") + "/choice/school/join");
		
		List<Article> articleList = new ArrayList<Article>();
		articleList.add(article1);
		articleList.add(article2);
		articleList.add(article3);
		articleList.add(article4);
		newsMessage.setArticleCount(articleList.size());

		newsMessage.setArticles(articleList);
		String respMessage = MessageService.bulidSendMessage(newsMessage, ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);

		return respMessage;
	}
}
