/*
 * 名称: uConfigVar
 * 描述: 系统配置信息类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 * 2013.2.13
 * 版本号控制更改
 * 原先的单一版本控制改为多系统版本控制
 * 2014.6.19
 * 加入CHAT变量，用于控制是否启用聊天功能
 */

package org.anyway.server.dbase.common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.anyway.common.uConfigVar;
import org.anyway.common.enums.CryptEnum;
import org.anyway.common.utils.uHexUtils;
import org.anyway.common.utils.uSecretUtils;
import org.anyway.common.utils.uStringUtils;
import com.nikhaldimann.inieditor.IniEditor;

public class uLoadVar extends uConfigVar {

	public final static String CONFIG_FILE_NAME = "./cfg/setting.properties";
	public final static String CONFIG_XMLFILE_NAME = "./cfg/config.xml";
	public final static String CHAT_FILE_NAME = "./cfg/chat.xml";
	
	/**
	 * Load config xml
	 * @throws Exception 
	 */
	public static Map<String,String> LoadJdbc() {
		
		Map<String,String> result = new HashMap<String,String>();
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new File(CONFIG_XMLFILE_NAME));
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
	public static void LoadIni() throws Exception {
		IniEditor inifile = new IniEditor();
		inifile.load(CONFIG_FILE_NAME);
		CharsetName = inifile.get("SET", "CharsetName");
		DEBUG = inifile.get("SET", "DEBUG").equals("1") ? true:false; 
		Logic_ExecutorPack = inifile.get("SET", "LogicExecutorPack");
		Logic_MessagePack = inifile.get("SET", "LogicMessagePack");
		
		US_WaitTimeOut = Integer.parseInt(inifile.get("UCI_SOCK", "WaitTime"));
		US_IdleTimeOut = Integer.parseInt(inifile.get("UCI_SOCK", "IdleTimeOut"));
		US_RWTimeOut = Integer.parseInt(inifile.get("UCI_SOCK", "RWTimeOut"));
		US_Port = Integer.parseInt(inifile.get("UCI_SOCK", "PORT"));
		US_IpFilter = Integer.parseInt(inifile.get("UCI_SOCK", "IpFilter"))==0? false:true;
		US_WorkThreadCount = Integer.parseInt(inifile.get("UCI_SOCK", "WorkThreadCount"));
		US_MaxSendBufferSize = Integer.parseInt(inifile.get("UCI_SOCK", "MaxSendBufferSize"));
		US_MaxReadBufferSize = Integer.parseInt(inifile.get("UCI_SOCK", "MaxReaddBufferSize"));
		US_Active = inifile.get("UCI_SOCK", "Active").equals("1") ? true:false;
		
		Web_Port = Integer.parseInt(inifile.get("WEB", "PORT"));
		Web_IsHttps = Integer.parseInt(inifile.get("WEB", "HTTPS"));
		Web_IsUse = Integer.parseInt(inifile.get("WEB", "Active"));

		HT_Port = Integer.parseInt(inifile.get("HTTP", "PORT"));
		HT_WaitTimeOut = Integer.parseInt(inifile.get("HTTP", "WaitTime"));
		HT_WorkThreadCount = Integer.parseInt(inifile.get("HTTP", "WorkThreadCount"));
		HT_Active = inifile.get("HTTP", "Active").equals("1") ? true:false;
		
		SID = inifile.get("DATABASE", "SID");
		UserID = inifile.get("DATABASE", "USERID");
		Pwd = inifile.get("DATABASE", "PWD");
		if (uStringUtils.empty(Pwd)==false) { //3DES解密
			byte[] decryptData = uHexUtils.hexStringToByte(Pwd);
			decryptData = uSecretUtils.Decrypt(decryptData,CryptEnum.DES);
			Pwd = new String(decryptData);
		}
		TbERROR = inifile.get("DATABASE", "TbERROR");
		TbUSER = inifile.get("DATABASE", "TbUSER");
		
		//Version = inifile.get("OTHERS", "VERSION");
		List<String> list = new ArrayList<String>();
		//获取消息调用存储过程配置信息
		list = inifile.optionNames("OTHERS");
		_pValueList.clear();
		for (int i=0; i<list.size(); i++) {
		    String key = list.get(i).toLowerCase();
		    _pValueList.put(key, inifile.get("OTHERS", key));//key+"="+inifile.get("OTHERS", key)
		}
		//获取消息体格式配置信息
		list = inifile.optionNames("BODY");
		_pBodyList.clear();
		for (int i=0; i<list.size(); i++) {
		    String key = list.get(i).toLowerCase();
		    _pBodyList.put(key, inifile.get("BODY", key));
		}
		
		list = inifile.optionNames("VERSION");
		_pVerList.clear();
		for (int i=0; i<list.size(); i++) {
		    String key = list.get(i).toLowerCase();
		    _pVerList.put(key, inifile.get("VERSION", key));
		}
		inifile = null;
		list = null;
	}
	
	public static String GetValue(String section, String key)
	{
		return _pValueList.get(key.toLowerCase());
	}
	
	public static String GetBodyValue(String section, String key)
	{
		return _pBodyList.get(key.toLowerCase());
	}
	
	public static String GetVerValue(String section, String key)
	{
		return _pVerList.get(key.toLowerCase());
	}
}
