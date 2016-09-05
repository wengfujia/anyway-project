/*
 * 名称: HttpSSL
 * 描述: jetty配置类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 日期：2013年10月15日
 * 修改日期:
 * 修改说明：
 */

package org.anyway.server.dbase.http.handler;

import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class HttpSSL {

	/**
	 * configHttp 配置http
	 * @param iPort
	 * @return
	 */
	public static HttpConfiguration configHttp(int iPort) {
		HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(iPort);
        http_config.setOutputBufferSize(32768);
        http_config.setRequestHeaderSize(8192);
        http_config.setResponseHeaderSize(8192);
        http_config.setSendServerVersion(true);
        http_config.setSendDateHeader(false);
        return http_config;
	}
	
	/**
	 * configSSLFactory 配置SSL
	 * @param keystore_path
	 * @param truststore_path
	 * @return
	 */
	public static SslContextFactory configSSLFactory(String keystore_path, String truststore_path) {
		SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keystore_path);
        sslContextFactory.setKeyStorePassword("OBF:1xfn1xu71u9h1shq1ri71shs1ua11xtb1xff");
        sslContextFactory.setKeyManagerPassword("OBF:1xfn1xu71u9h1shq1ri71shs1ua11xtb1xff");
        sslContextFactory.setTrustStorePath(truststore_path);
        sslContextFactory.setTrustStorePassword("OBF:1xfn1xu71u9h1shq1ri71shs1ua11xtb1xff");
        sslContextFactory.setExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA", "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA", "SSL_RSA_EXPORT_WITH_RC4_40_MD5", "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA", "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");
        return sslContextFactory;
	}
	
	/**
	 * configSSLFactory 配置SSL
	 * @return
	 */
	public static SslContextFactory configSSLFactory() {
        String jetty_base = System.getProperty("jetty_base", "./jetty");  
        return configSSLFactory(jetty_base + "/etc/ryccAdmin.keystore", jetty_base + "/etc/ryccAdmin.keystore");
	}
	
	/**
	 * setRequestLog配置请求Log
	 * @param jetty_home
	 * @return
	 */
	public static RequestLogHandler setRequestLog(String jetty_home) {
		NCSARequestLog requestLog = new NCSARequestLog();
        requestLog.setFilename(jetty_home + "/logs/yyyy_mm_dd.request.log");
        requestLog.setFilenameDateFormat("yyyy_MM_dd");
        requestLog.setRetainDays(90);
        requestLog.setAppend(true);
        requestLog.setExtended(true);
        requestLog.setLogCookies(false);
        requestLog.setLogTimeZone("GMT");
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(requestLog);
        return requestLogHandler;
	}
	
	/**
	 * setRequestLog配置请求Log
	 * @return
	 */
	public static RequestLogHandler setRequestLog() {
		String jetty_home = System.getProperty("jetty.home", ".");
        return setRequestLog(jetty_home);
	}
	
	/**
	 * setLoginService配置登录服务信息
	 * @param jetty_base
	 * @return
	 */
	public static HashLoginService setLoginService(String jetty_base) {
		HashLoginService login = new HashLoginService();
	    login.setName("Rycc Realm");
	    login.setConfig(jetty_base + "/etc/realm.properties");
	    login.setHotReload(true);
	    return login;
	}
	
	/**
	 * setLoginService配置登录服务信息
	 * @return
	 */
	public static HashLoginService setLoginService() {
		String jetty_base = System.getProperty("jetty.base", "./jetty");   
		return setLoginService(jetty_base);
	}
}
