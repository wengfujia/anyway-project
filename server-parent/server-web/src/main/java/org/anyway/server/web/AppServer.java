/*
 * 名称: AppServer
 * 描述: 主程序
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2014年6月24日
 * 修改日期:
 */

package org.anyway.server.web;

import org.apache.log4j.PropertyConfigurator;
import org.anyway.common.uConfigVar;
import org.anyway.common.utils.uClassUtil;
import org.anyway.common.utils.uLogger;
import org.anyway.common.utils.uStringUtils;
import org.anyway.server.web.cache.CacheManager;
import org.anyway.server.web.cache.thread.DispatcherExecutor;
import org.anyway.server.web.cache.thread.SeqidCacheRunnable;
import org.anyway.server.web.common.uLoadVar;
import org.anyway.server.web.validity.Version;

public class AppServer {
	
	public static void main(String[] args) throws Exception {
		System.setProperty("net.sf.ehcache.enableShutdownHook","true");
		//记录使用日期
		//Version.serverUseDate();
		
		//log4的配置文件		
		PropertyConfigurator.configure( "resources/log4j.properties" );
		uLogger.setDebug(uConfigVar.DEBUG);
		//读取配置信息
		uLoadVar.LoadIni();
		
		//加载缓存
		uLogger.println("loading cache。。。。。。");
		CacheManager.getInstance().DO();
		
		if (uStringUtils.empty(uConfigVar.Logic_MessagePack) == false) {
			//加载消息分解类
			uLogger.println("loading business message classes。。。。。。");
			uClassUtil.initTypeToMsgClassMap(uConfigVar.Logic_MessagePack);
		}
		if (uStringUtils.empty(uConfigVar.Logic_ExecutorPack) == false) {
			//加载消息分发类
			uLogger.println("loading business logic classes。。。。。。");
			uClassUtil.initTypeToExecutorClassMap(uConfigVar.Logic_ExecutorPack);
		}
		
		//获取微信令牌
		if (uLoadVar.IsWeixinServer()) {
			uLogger.println("getting the accesstoken for the weixn。。。。。。");
			CacheManager.getInstance().getConfigCache().fetchTokenFormWeixin();
		}
		else { //打开自增序号定时缓存线程
			Thread t = new Thread(new SeqidCacheRunnable());
			t.start();
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
			DispatcherExecutor.Start();
		}
		
    }
}
