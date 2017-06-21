/*
 * 名称: AppServer
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
import org.anyway.common.uConfigVar;
import org.anyway.common.utils.ClassUtil;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.StringUtil;
import org.anyway.server.processor.Providers.db.CJdbcPool;
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
		
		//log4的配置文件		
		PropertyConfigurator.configure( "resources/log4j.properties" );
		LoggerUtil.setDebug(uConfigVar.DEBUG);
		//读取配置信息
		ProcesserConfig.getInstance().loadJdbcConfig();
		CJdbcPool.Initial();
		//创建缓存
		DBCache.DO();
		
		//加载插件包
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

		//有效期验证，有效打开socket与http
		if (Version.check())
		{
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
		}
		
		//打开web服务
    	if (uConfigVar.Web_IsUse == 1) {
	    	WebServer web = new WebServer(uConfigVar.Web_Port);
	    	web.start(); 
    	}
    }
 
}
