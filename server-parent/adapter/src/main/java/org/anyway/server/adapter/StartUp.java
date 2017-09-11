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
import org.anyway.common.SystemConfig;
import org.anyway.common.utils.ClassUtil;
import org.anyway.common.utils.FileUtil;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.StringUtil;
import org.anyway.server.adapter.cache.CacheManager;
import org.anyway.server.adapter.moniter.MoniterManager;
import org.anyway.server.adapter.validity.Version;

public class StartUp {
	
	public static void main(String[] args) throws Exception {
		System.setProperty("net.sf.ehcache.enableShutdownHook","true");
		//记录使用日期
		//Version.serverUseDate();
		
		//读取配置信息
		AdapterConfig.getInstance().loadNettyConfig();
		
		//log4的配置文件		
		PropertyConfigurator.configure(FileUtil.toFileName("resources/log4j.properties"));
		LoggerUtil.setDebug(SystemConfig.DEBUG);
		
		if (StringUtil.empty(AdapterConfig.getInstance().getLogicMessagePack()) == false) {
			//加载消息分解类
			LoggerUtil.println("loading business message classes。。。。。。");
			ClassUtil.initTypeToMsgClassMap(AdapterConfig.getInstance().getLogicMessagePack());
		}
		if (StringUtil.empty(AdapterConfig.getInstance().getLogicExecutorPack()) == false) {
			//加载消息分发类
			LoggerUtil.println("loading business logic classes。。。。。。");
			ClassUtil.initTypeToExecutorClassMap(AdapterConfig.getInstance().getLogicExecutorPack());
		}
		
		//获取微信令牌
		if (AdapterConfig.getInstance().IsWeixinServer()) {
			LoggerUtil.println("getting the accesstoken for the weixn。。。。。。");
			CacheManager.getInstance().getConfigCache().fetchTokenFormWeixin();
		}
		
		//有效期验证，有效打开socket与http
		if (Version.check()) {
			if (AdapterConfig.getInstance().getUSActive()) {
				//打开socket服务端
				SocketServer socket = new SocketServer(AdapterConfig.getInstance().getUSPort());
				socket.start();	
			}
			if (AdapterConfig.getInstance().getHTActive()) {
				//打开http服务端
				HttpServer http = new HttpServer(AdapterConfig.getInstance().getHTPort());
				http.start(); 
			}
			MoniterManager.Start();
		}
		
    }
}
