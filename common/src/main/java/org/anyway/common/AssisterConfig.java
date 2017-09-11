/**
 * AssisterConfig附助服务的配置信息
 */
package org.anyway.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.anyway.common.utils.FileUtil;
import org.anyway.common.utils.LoggerUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.nikhaldimann.inieditor.IniEditor;

/**
 * @author wengfj
 *
 */
public class AssisterConfig extends NettyServerConfig{

	public final static String CONFIG_FILE_NAME = "./cfg/setting.properties";
	private final String CONFIG_XMLFILE_NAME = "./cfg/config.xml";
	
	private static AssisterConfig instance = null;
	
	private String SMSURL = "";
	private String SMSAppKey = "";
	private String SMSAppSecret = "";
	
	//数据库配置信息
	private String SID = "192.168.0.200"; //数据库
	private String UserID = "appsrv";
	private String Pwd = "appsrv123";
	private String TbERROR = "ry_error";	//错误定义表
	private String TbUSER = "ry_users";	//用户表
	
	public static AssisterConfig getInstance() {
		if (null == instance) {
			synchronized (AssisterConfig.class) {
				instance = new AssisterConfig();
			}
		}
		return instance;
	}
	
	
	/**
	 * Load config xml
	 * @throws Exception 
	 */
	public Map<String,String> loadJdbcConfig() {
		Map<String,String> result = new HashMap<String,String>();
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(FileUtil.toFileName(CONFIG_XMLFILE_NAME)));
			Element root =doc.getRootElement();
	
			Element jdbc = (Element)root.element("jdbc");
			result.put("testWhileIdle", jdbc.attributeValue("testWhileIdle"));
			result.put("testOnBorrow", jdbc.attributeValue("testOnBorrow"));
			result.put("testOnReturn", jdbc.attributeValue("testOnReturn"));
			result.put("testOnConnect", jdbc.attributeValue("testOnConnect"));
			result.put("validationQuery", jdbc.attributeValue("validationQuery"));
			result.put("validationInterval", jdbc.attributeValue("validationInterval"));
			result.put("initialSize", jdbc.attributeValue("initialSize"));
			result.put("minIdle", jdbc.attributeValue("minIdle"));
			result.put("maxActive", jdbc.attributeValue("maxActive"));
			result.put("maxIdle", jdbc.attributeValue("maxIdle"));
			result.put("maxWait", jdbc.attributeValue("maxWait"));
			result.put("timeBetweenEvictionRunsMillis", jdbc.attributeValue("timeBetweenEvictionRunsMillis"));
			result.put("minEvictableIdleTimeMillis", jdbc.attributeValue("minEvictableIdleTimeMillis"));        
			result.put("removeAbandonedTimeout", jdbc.attributeValue("removeAbandonedTimeout"));
			result.put("removeAbandoned", jdbc.attributeValue("removeAbandoned"));
			result.put("logAbandoned", jdbc.attributeValue("logAbandoned"));
			result.put("jmxEnabled", jdbc.attributeValue("jmxEnabled"));
			result.put("jdbcInterceptors", jdbc.attributeValue("jdbcInterceptors"));
			result.put("driverClassName", jdbc.attributeValue("driverClassName"));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			reader = null;
		}
		return result;
	}
	
	/**
	 * Load config inifile
	 * @throws Exception 
	 */
	public void loadNettyConfig() {
		IniEditor inifile = new IniEditor();
		try
		{
			inifile.load(FileUtil.toFileName(CONFIG_FILE_NAME));
			//读取全局配置信息
			SystemConfig.CharsetName = inifile.get("SET", "CharsetName");
			SystemConfig.DEBUG = inifile.get("SET", "DEBUG").equals("1") ? true:false; 
			
			SID = inifile.get("DATABASE", "SID");
			UserID = inifile.get("DATABASE", "USERID");
			Pwd = inifile.get("DATABASE", "PWD");
			TbERROR = inifile.get("DATABASE", "TbERROR");
			
			//阿里大于短信配置
			SMSURL = inifile.get("SMS", "URL");
			SMSAppKey = inifile.get("SMS", "AppKey");
			SMSAppSecret = inifile.get("SMS", "AppSecret");
			
			//读取Http相关的配置信息
			setHTPort(Integer.parseInt(inifile.get("HTTP", "PORT")));
			setHTWaitTimeOut(Integer.parseInt(inifile.get("HTTP", "WaitTime")));
			setHTIdleTimeOut(Integer.parseInt(inifile.get("HTTP", "IdleTimeOut")));
			setHTRWTimeOut(Integer.parseInt(inifile.get("HTTP", "RWTimeOut")));
			setHTWorkThreadCount(Integer.parseInt(inifile.get("HTTP", "WorkThreadCount")));
			setHTActive(inifile.get("HTTP", "Active").equals("1") ? true:false);
		} catch (IOException e) {
			LoggerUtil.getLogger().error("加载{}出错，{}", CONFIG_FILE_NAME, e.getMessage());
		}
		finally {
			inifile = null;
		}
	}
	
	//数据库配置信息
	public String getSID() {
		return this.SID;
	}
	public String getUserID() {
		return this.UserID;
	}
	public String getPwd() {
		return this.Pwd;
	}
	public String getTbERROR() {
		return this.TbERROR;
	}
	public String getTbUSER() {
		return this.TbUSER;
	}
	
	public String getSMSURL() {
		return this.SMSURL;
	}
	public String getSMSAppKey() {
		return this.SMSAppKey;
	}
	public String getSMSAppSecret() {
		return this.SMSAppSecret;
	}
	
}
