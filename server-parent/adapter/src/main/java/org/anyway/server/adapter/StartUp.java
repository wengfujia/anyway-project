/*
 * 名称: StartUp
 * 描述: 主程序
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2014年6月24日
 * 修改日期:
 */

package org.anyway.server.adapter;

import org.apache.log4j.PropertyConfigurator;
import org.anyway.common.AdapterConfig;
import org.anyway.common.uConfigVar;
import org.anyway.common.utils.ClassUtil;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.StringUtil;
import org.anyway.server.adapter.cache.CacheManager;
import org.anyway.server.adapter.moniter.DispatcherExecutorMoniter;
import org.anyway.server.adapter.validity.Version;

public class StartUp {
	
	public static void main(String[] args) throws Exception {
		System.setProperty("net.sf.ehcache.enableShutdownHook","true");
		//记录使用日期
		//Version.serverUseDate();
		
		//log4的配置文件		
		PropertyConfigurator.configure( "resources/log4j.properties" );
		LoggerUtil.setDebug(uConfigVar.DEBUG);
		//读取配置信息
		AdapterConfig.getInstance().loadNettyConfig();
		
		if (StringUtil.empty(uConfigVar.Logic_MessagePack) == false) {
			//加载消息分解类
			LoggerUtil.println("loading business message classes。。。。。。");
			ClassUtil.initTypeToMsgClassMap(uConfigVar.Logic_MessagePack);
		}
		if (StringUtil.empty(uConfigVar.Logic_ExecutorPack) == false) {
			//加载消息分发类
			LoggerUtil.println("loading business logic classes。。。。。。");
			ClassUtil.initTypeToExecutorClassMap(uConfigVar.Logic_ExecutorPack);
		}
		
		//获取微信令牌
		if (AdapterConfig.getInstance().IsWeixinServer()) {
			LoggerUtil.println("getting the accesstoken for the weixn。。。。。。");
			CacheManager.getInstance().getConfigCache().fetchTokenFormWeixin();
		}
		
		//有效期验证，有效打开socket与http
		if (Version.check()) {
			if (uConfigVar.US_Active) {
				//打开socket服务端
				SocketServer socket = new SocketServer(uConfigVar.US_Port);
				socket.start();	
			}
			if (uConfigVar.HT_Active) {
				//打开http服务端
				HttpServer http = new HttpServer(uConfigVar.HT_Port);
				http.start(); 
			}
			DispatcherExecutorMoniter.Start();
		}
		
    }
}
