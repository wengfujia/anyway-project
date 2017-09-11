/*
 * 名称: CommandID
 * 描述: 包头定义类
 * 消息头为正数表示是发送包，负数表示为反馈包
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

package org.anyway.common.protocol.header;

import java.io.Serializable;

public final class CommandID implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8856223275420727678L;
	
	//消 息头的定义
	public final static int TEST = 0;		//检测心跳，不用做处理

	//内部消息处理
	public final static int HTTP_REQUEST = 1;	//HTTP消息请求
	public final static int HTTP_RESPONSE = 2;	//HTTP消息返回
	public final static int TCP_REQUEST = 3;	//TCP协议请求
	public final static int TCP_RESPONSE = 4;	//TCP消息返回
	public final static int WEIXIN_REQUEST = 5;	//微信消息请求

	//其它
	public final static int DEFAULTRESPONSE = 10000;	//总的统一返回消息头,用于路由消息到处理层返回给前端处理插件头
	public final static int INIT_FINAL = 10001;
	public final static int UPGRADE_CLIENT = 10002;	//更新
	
}
