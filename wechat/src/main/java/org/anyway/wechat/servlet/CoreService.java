package org.anyway.wechat.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.anyway.wechat.constant.ConstantWeChat;
import org.anyway.wechat.entity.message.resp.Article;
import org.anyway.wechat.entity.message.resp.NewsMessage;
import org.anyway.wechat.entity.message.resp.TextMessage;
import org.anyway.wechat.service.MessageService;
import org.anyway.wechat.util.MessageUtil;
import org.apache.log4j.Logger;

/**
 * 处理微信核心业务的service
 * @author beinfo
 *
 */
public class CoreService {
	public static Logger log = Logger.getLogger(CoreService.class);
	
	/**
	 * 处理微信发来的请求
	 * @param request
	 * @return
	 */
	public static String processRequest(HttpServletRequest request) {
		String respMessage = null;
		System.out.println("-------------------------------处理微信发来的请求------------------------------------" ) ;
		try{
			// xml请求解析  
            Map<String, String> requestMap = MessageUtil.parseXml(request);  
            System.out.println("requestMap ===" + requestMap);
	         // 消息类型
	         String msgType = requestMap.get("MsgType");
	         System.out.println("msgType ===" + msgType);
	         
	         TextMessage textMessage = (TextMessage) MessageService.bulidBaseMessage(requestMap, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
	         NewsMessage newsMessage = (NewsMessage) MessageService.bulidBaseMessage(requestMap, ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);
	         
	         String respContent = "";
			// 文本消息
			if (msgType.equals(ConstantWeChat.REQ_MESSAGE_TYPE_TEXT)) {
				// 接收用户发送的文本消息内容
				String content = requestMap.get("Content");
				// 创建图文消息
				List<Article> articleList = new ArrayList<Article>();
				
				if ("1".equals(content)) {	// 单图文消息
					Article article = new Article();
					article.setTitle("我是一条单图文消息");
					article.setDescription("我是描述信息，哈哈哈哈哈哈哈。。。");
					article.setPicUrl("http://www.diskes.com/resources/images/denglu/yun.png");
					article.setUrl("http://www.diskes.com");
					articleList.add(article);
					// 设置图文消息个数
					newsMessage.setArticleCount(articleList.size());
					// 设置图文消息包含的图文集合
					newsMessage.setArticles(articleList);
					// 将图文消息对象转换成xml字符串
					respMessage = MessageService.bulidSendMessage(newsMessage, ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);
				}else if("3".equals(content)){	//多图文消息
					Article article1 = new Article();
					article1.setTitle("我是一条多图文消息");
					article1.setDescription("");
					article1.setPicUrl("http://www.diskes.com/resources/images/denglu/yun.png");
					article1.setUrl("http://www.diskes.com");

					Article article2 = new Article();
					article2.setTitle("系统升级中！敬请期待");
					article2.setDescription("");
					article2.setPicUrl("http://www.qcllk.com/resources/image/face.png");
					article2.setUrl("http://www.qcllk.com/web/other/about_update");

					Article article3 = new Article();
					article3.setTitle("汽车连连看");
					article3.setDescription("");
					article3.setPicUrl("http://www.qcllk.com/resources/image/backgrounds/lx.png");
					article3.setUrl("http://www.qcllk.com");

					articleList.add(article1);
					articleList.add(article2);
					articleList.add(article3);
					newsMessage.setArticleCount(articleList.size());

					newsMessage.setArticles(articleList);
					respMessage = MessageService.bulidSendMessage(newsMessage, ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);
				}
			}else if (msgType.equals(ConstantWeChat.REQ_MESSAGE_TYPE_VOICE)) {	//语音消息
				textMessage.setContent("您说的是：" + requestMap.get("Recognition"));
				respMessage = MessageService.bulidSendMessage(textMessage, ConstantWeChat.RESP_MESSAGE_TYPE_TEXT);
			}else if (msgType.equals(ConstantWeChat.REQ_MESSAGE_TYPE_EVENT)) {	//事件
				System.out.println(msgType.equals(ConstantWeChat.REQ_MESSAGE_TYPE_EVENT));
				// 事件类型
				String eventType = requestMap.get("Event");
				System.out.println("eventType:::" + eventType);
				if (eventType.equals(ConstantWeChat.EVENT_TYPE_SUBSCRIBE)) {
					// 关注
					respContent = "感谢您关注！\n";
					StringBuffer contentMsg = new StringBuffer();
					contentMsg.append("您还可以回复下列数字，体验相应服务").append("\n\n");
					contentMsg.append("1  我就是个测试的").append("\n");
					contentMsg.append("2  我啥都木有").append("\n");
					contentMsg.append("3  我是多图文").append("\n");
					respContent = respContent + contentMsg.toString();

				} else if (eventType.equals(ConstantWeChat.EVENT_TYPE_UNSUBSCRIBE)) {
					// 取消关注,用户接受不到我们发送的消息了，可以在这里记录用户取消关注的日志信息
					System.out.println("用户" + requestMap.get("FromUserName") + "已经取消关注");
					log.info("用户" + requestMap.get("FromUserName") + "已经取消关注");
				} else if (eventType.equals(ConstantWeChat.EVENT_TYPE_CLICK)) {

					// 事件KEY值，与创建自定义菜单时指定的KEY值对应
					String eventKey = requestMap.get("EventKey");

					// 自定义菜单点击事件
					if (eventKey.equals("apply_id")) {
						//respContent = "申请！";
						Article article1 = new Article();
						article1.setTitle("申请介绍");
						article1.setDescription("");
						article1.setPicUrl("http://www.diskes.com/resources/images/denglu/yun.png");
						article1.setUrl("http://www.diskes.com");

						Article article2 = new Article();
						article2.setTitle("申请学校");
						article2.setDescription("");
						article2.setPicUrl("http://www.qcllk.com/resources/image/face.png");
						article2.setUrl("http://www.qcllk.com/web/other/about_update");

						Article article3 = new Article();
						article3.setTitle("申请班级");
						article3.setDescription("");
						article3.setPicUrl("http://www.qcllk.com/resources/image/backgrounds/lx.png");
						article3.setUrl("http://www.qcllk.com");
						
						Article article4 = new Article();
						article4.setTitle("加入班级");
						article4.setDescription("");
						article4.setPicUrl("http://www.qcllk.com/resources/image/backgrounds/lx.png");
						article4.setUrl("http://www.qcllk.com");
						
						// 创建图文消息
						List<Article> articleList = new ArrayList<Article>();
						
						articleList.add(article1);
						articleList.add(article2);
						articleList.add(article3);
						articleList.add(article4);
						newsMessage.setArticleCount(articleList.size());

						newsMessage.setArticles(articleList);
						respMessage = MessageService.bulidSendMessage(newsMessage, ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);
					} else if (eventKey.equals("the_news_id")) {
						// respContent = "消息！";
						Article article1 = new Article();
						article1.setTitle("消息");
						article1.setDescription("");
						article1.setPicUrl("http://www.diskes.com/resources/images/denglu/yun.png");
						article1.setUrl("http://www.diskes.com");

						Article article2 = new Article();
						article2.setTitle("我的消息");
						article2.setDescription("");
						article2.setPicUrl("http://www.qcllk.com/resources/image/face.png");
						article2.setUrl("http://www.qcllk.com/web/other/about_update");

						Article article3 = new Article();
						article3.setTitle("我的咨询");
						article3.setDescription("");
						article3.setPicUrl("http://www.qcllk.com/resources/image/backgrounds/lx.png");
						article3.setUrl("http://www.qcllk.com");
						
						// 创建图文消息
						List<Article> articleList = new ArrayList<Article>();
						
						articleList.add(article1);
						articleList.add(article2);
						articleList.add(article3);
						newsMessage.setArticleCount(articleList.size());

						newsMessage.setArticles(articleList);
						respMessage = MessageService.bulidSendMessage(newsMessage, ConstantWeChat.RESP_MESSAGE_TYPE_NEWS);
					}
				}

			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("-------------------------------处理结束------------------------------------" ) ;
		System.out.println("respMessage:::" + respMessage);
		return respMessage;
	}
}
