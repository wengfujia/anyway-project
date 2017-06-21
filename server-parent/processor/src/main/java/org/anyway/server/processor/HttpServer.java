/*
 * 名称: HttpServer
 * 描述: HTTP服务端
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

package org.anyway.server.processor;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.anyway.common.uConfigVar;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.server.processor.http.handler.AsyncHttpServlet;
import org.anyway.server.processor.http.handler.HttpSSL;

public class HttpServer {
	private Server server;
	private final int port;
	private Thread thread = null;
	
    public HttpServer(int port) {  
    	this.port = port; 
    }  
    
    Runnable OpenHttp = new Runnable(){
		public void run(){
			
			if (uConfigVar.Web_IsHttps == 1) { 
				server = new Server();
			} else {
				server = new Server(port);
			}
			
	        // 创建ServletContextHandler，设置Servlet
	        final ServletContextHandler servletcontext = new ServletContextHandler(ServletContextHandler.SESSIONS);
	        servletcontext.setContextPath("/");      
	        final ServletHolder servlet = new ServletHolder(new AsyncHttpServlet());
	        servlet.setAsyncSupported(true);
	        servletcontext.addServlet(servlet, "/*");
	        
	        // 创建ContextHandler，设置静态资源
	        ContextHandler context = new ContextHandler();
	        context.setContextPath("/");//context.setContextPath("/resources");
	        context.setResourceBase(".");//设置路径
	        context.setClassLoader(Thread.currentThread().getContextClassLoader());
	        context.setHandler(new ResourceHandler());
	        
	        // 创建ContextHandlerCollection 集合
	        ContextHandlerCollection contexts = new ContextHandlerCollection();
	        contexts.setHandlers(new Handler[] { servletcontext, context });
	        // 设置Handler
	        server.setHandler(contexts);
	        
	        /* 启用https方式
	         * 如果web启用https,则http server接口必须为https
	         * */
	        if (uConfigVar.Web_IsHttps == 1) { 
	        	HttpConfiguration http_config = HttpSSL.configHttp(port);
		        SslContextFactory sslContextFactory = HttpSSL.configSSLFactory();
		        
		        HttpConfiguration https_config = new HttpConfiguration(http_config);
		        https_config.addCustomizer(new SecureRequestCustomizer());
		        ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https_config));
		        sslConnector.setPort(port);
		        server.addConnector(sslConnector);
	        }
	        
	        try {
				server.start();				
				server.join();
			} catch (Exception e) {
				LoggerUtil.getLogger().error("Fail to open the http service!"+e.getMessage());
			}	
		}
    };
    
    public void start() throws Exception {  
    	thread = new Thread(OpenHttp);
		thread.start();
		LoggerUtil.println("The http service is runing! Port:" + uConfigVar.HT_Port);
    }  
}
