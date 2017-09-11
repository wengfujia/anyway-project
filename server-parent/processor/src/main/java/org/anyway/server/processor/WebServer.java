/*
 * 名称: AsyncHttpServlet
 * 描述: HTTP接收与解析类
 * 传入：commandid,user,pwd,body(3DES加密串)
 * 版本：  1.0.1
 * 作者： 翁富家
 * 修改:  翁富家
 * 日期：2013年10月15日
 * 修改日期:2014年1月15日
 * 修改说明：
 * 2014年1月15日
 * 增加sessionid与version两个字段
 * sessionid用于标识终端类型
 */

package org.anyway.server.processor;

import java.lang.management.ManagementFactory;

import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.deploy.PropertiesConfigurationManager;
import org.eclipse.jetty.deploy.providers.WebAppProvider;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.LowResourceMonitor;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.anyway.common.ProcesserConfig;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.server.processor.http.handler.HttpSSL;

public class WebServer {
	
	private Server server;
	private final int port;
	
	@SuppressWarnings("unused")
	private void setResourceMonitor() {
		LowResourceMonitor lowResourcesMonitor = new LowResourceMonitor(server);
        lowResourcesMonitor.setPeriod(1000);
        lowResourcesMonitor.setLowResourcesIdleTimeout(200);
        lowResourcesMonitor.setMonitorThreads(true);
        lowResourcesMonitor.setMaxConnections(0);
        lowResourcesMonitor.setMaxMemory(0);
        lowResourcesMonitor.setMaxLowResourcesTime(5000);
        server.addBean(lowResourcesMonitor);
	}
	
    public WebServer(int port) {  
    	this.port = port; 
    }  
    
    Runnable OpenWeb = new Runnable(){
    	
		public void run(){        
	        
			String jetty_home = System.getProperty("jetty.home", ".");
	        String jetty_base = System.getProperty("jetty.base", "./jetty");
	        System.setProperty("jetty.home", jetty_home);
	        System.setProperty("jetty.base", jetty_base);        
	        
	        //QueuedThreadPool threadPool = new QueuedThreadPool();
	        //threadPool.setMaxThreads(200);
	        server = new Server();//threadPool
	        server.addBean(new ScheduledExecutorScheduler());
	        
	        //设置handler
	        HandlerCollection handlers = new HandlerCollection();
	        ContextHandlerCollection contexts = new ContextHandlerCollection();
	        handlers.setHandlers(new Handler[] { contexts, new DefaultHandler() });
	        server.setHandler(handlers);
	        server.setDumpAfterStart(false);
	        server.setDumpBeforeStop(false);
	        server.setStopAtShutdown(true);
	        MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
	        server.addBean(mbContainer);
	        
	        if (ProcesserConfig.getInstance().getWebIsHttps()) { //https方式
	        	HttpConfiguration http_config = HttpSSL.configHttp(port);
		        SslContextFactory sslContextFactory = HttpSSL.configSSLFactory();
		        
		        HttpConfiguration https_config = new HttpConfiguration(http_config);
		        https_config.addCustomizer(new SecureRequestCustomizer());
		        ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https_config));
		        sslConnector.setPort(port);
		        sslConnector.setIdleTimeout(500000);
		        server.addConnector(sslConnector);
	        } else { //http方式
	        	ServerConnector http = new ServerConnector(server);//, new HttpConnectionFactory(http_config));
		        http.setPort(port);
		        http.setIdleTimeout(30000);
		        server.addConnector(http);
	        }     
	        
	        /*设置webRoot，并可以运行war */         
	        DeploymentManager deployer = new DeploymentManager();
	        deployer.setContexts(contexts);
	        deployer.setContextAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*/servlet-api-[^/]*\\.jar$");
	        WebAppProvider webapp_provider = new WebAppProvider();
	        webapp_provider.setMonitoredDirName(jetty_home + "/webRoot");
	        webapp_provider.setDefaultsDescriptor(jetty_base + "/etc/webdefault.xml");
	        //webapp_provider.setScanInterval(1);
	        webapp_provider.setExtractWars(true);
	        webapp_provider.setConfigurationManager(new PropertiesConfigurationManager());
	        deployer.addAppProvider(webapp_provider);	        
	        server.addBean(deployer);

	        //setResourceMonitor();//设置资源变化监控	        
	        HashLoginService login = HttpSSL.setLoginService();//设置登录信息

	        server.addBean(login);
			try {
				server.start();
				server.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    };
    
    public void start() throws Exception {  
    	Thread thread = new Thread(OpenWeb);
		thread.start();
		LoggerUtil.println("The web servlet is runing! Port:" + ProcesserConfig.getInstance().getWebPort());
    }  
}
