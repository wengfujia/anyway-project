/*
 * 名称: StartUp
 * 描述: 主程序
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2014年6月24日
 * 修改日期:
 */

package org.anyway.server.processor;

import org.apache.log4j.PropertyConfigurator;
import org.anyway.common.ProcesserConfig;
import org.anyway.common.SystemConfig;
import org.anyway.common.utils.ClassUtil;
import org.anyway.common.utils.FileUtil;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.StringUtil;
import org.anyway.plugin.processor.pool.DataSourcePool;
import org.anyway.server.processor.cache.DBCache;
import org.anyway.server.processor.validity.Version;

public class StartUp {
    /**
     * @param args
     * @throws Exception 
     */
	
	public static void main(String[] args) throws Exception {
		//记录使用日期
		Version.serverUseDate();
		//加载数据源
		DataSourcePool.getInstance().Initial();
				
		//加载配置信息
		ProcesserConfig.getInstance().loadNettyConfig();
		
		//log4的配置文件		
		PropertyConfigurator.configure(FileUtil.toFileName("resources/log4j.properties"));
		LoggerUtil.setDebug(SystemConfig.DEBUG);
		
		//创建缓存
		DBCache.DO();
		
		//加载插件包
		if (StringUtil.empty(ProcesserConfig.getInstance().getLogicMessagePack()) == false) {
			//加载消息分解类
			LoggerUtil.println("loading business message classes。。。。。。");
			ClassUtil.initTypeToMsgClassMap(ProcesserConfig.getInstance().getLogicMessagePack());
		}
		if (StringUtil.empty(ProcesserConfig.getInstance().getLogicExecutorPack()) == false) {
			//加载消息分发类
			LoggerUtil.println("loading business logic classes。。。。。。");
			ClassUtil.initTypeToExecutorClassMap(ProcesserConfig.getInstance().getLogicExecutorPack());
		}

		//有效期验证，有效打开socket与http
		if (Version.check())
		{
			if (ProcesserConfig.getInstance().getUSActive()) {
				//打开socket服务端
		    	SocketServer socket = new SocketServer(ProcesserConfig.getInstance().getUSPort());
		    	socket.start();		
			}
			if (ProcesserConfig.getInstance().getHTActive()) {
				//打开http服务端
		    	HttpServer http = new HttpServer(ProcesserConfig.getInstance().getHTPort());
		    	http.start(); 		
			}	
		}
		
		//打开web服务
    	if (ProcesserConfig.getInstance().getWebIsUse()) {
	    	WebServer web = new WebServer(ProcesserConfig.getInstance().getWebPort());
	    	web.start(); 
    	}
    }
 
}
