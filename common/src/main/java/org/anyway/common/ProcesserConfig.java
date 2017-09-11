/**
 * processer服务的配置信息
 */
package org.anyway.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.anyway.common.utils.FileUtil;
import org.anyway.common.utils.LoggerUtil;

import com.nikhaldimann.inieditor.IniEditor;

/**
 * @author wengfj
 *
 */
public class ProcesserConfig extends NettyServerConfig{

	public final static String CONFIG_FILE_NAME = "./cfg/setting.properties";
	
	private static ProcesserConfig instance = null;
	
	//插件包
	/**
	 * 业务执行包
	 */
	private String Logic_ExecutorPack = "com.business.executor";
	/**
	 * 业务执行消息包
	 */
	private String Logic_MessagePack = "com.business.message";
	
	//业务标识、版本配置信息
	private Map<String, String> _pValueList = new HashMap<String, String>();
	private Map<String, String> _pBodyList = new HashMap<String, String>();
	private Map<String, String> _pVerList = new HashMap<String, String>();
	
	private String HBASE_CONF_DIR = "";		//hbase配置目录(目前不支持)
	private int DEF_RESPONSE = 0;			//默认返回的业务标识号,如果没有找到业务逻辑执行类，则查找是否有默认的业务逻辑类
	
	//WEB站点配置信息
	private int Web_Port = 80; 				//WEB端端口
	private boolean Web_IsHttps = false;	//是否启用HTTPS true启用 false不启用
	private boolean Web_IsUse = false;		//是否启用WEB	
	
	public static ProcesserConfig getInstance() {
		if (null == instance) {
			synchronized (ProcesserConfig.class) {
				if (null == instance) {
					instance = new ProcesserConfig();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Load config inifile
	 * @throws Exception 
	 */
	public void loadNettyConfig() {
		List<String> list = new ArrayList<String>();
		IniEditor inifile = new IniEditor();
		try
		{
			inifile.load(FileUtil.toFileName(CONFIG_FILE_NAME));
			//读取全局配置信息
			SystemConfig.CharsetName = inifile.get("SET", "CharsetName");
			SystemConfig.DEBUG = inifile.get("SET", "DEBUG").equals("1") ? true:false; 
			this.Logic_ExecutorPack = inifile.get("SET", "LogicExecutorPack");
			this.Logic_MessagePack = inifile.get("SET", "LogicMessagePack");
			
			//读取socket相关的配置信息
			setUSPort(Integer.parseInt(inifile.get("UCI_SOCK", "PORT")));
			setUSWaitTimeOut(Integer.parseInt(inifile.get("UCI_SOCK", "WaitTime")));
			setUSIdleTimeOut(Integer.parseInt(inifile.get("UCI_SOCK", "IdleTimeOut")));
			setUSRWTimeOut(Integer.parseInt(inifile.get("UCI_SOCK", "RWTimeOut")));
			setUSWorkThreadCount(Integer.parseInt(inifile.get("UCI_SOCK", "WorkThreadCount")));
			setUSMaxSendBufferSize(Integer.parseInt(inifile.get("UCI_SOCK", "MaxSendBufferSize")));
			setUSMaxReadBufferSize(Integer.parseInt(inifile.get("UCI_SOCK", "MaxReaddBufferSize")));
			setUSActive(inifile.get("UCI_SOCK", "Active").equals("1") ? true:false);
			
			//读取Http相关的配置信息
			setHTPort(Integer.parseInt(inifile.get("HTTP", "PORT")));
			setHTWaitTimeOut(Integer.parseInt(inifile.get("HTTP", "WaitTime")));
			setHTWorkThreadCount(Integer.parseInt(inifile.get("HTTP", "WorkThreadCount")));
			setHTActive(inifile.get("HTTP", "Active").equals("1") ? true : false);
			
			//WEB配置信息
			this.Web_Port = Integer.parseInt(inifile.get("WEB", "PORT"));
			this.Web_IsHttps = inifile.get("WEB", "HTTPS").equals("1") ? true : false;
			this.Web_IsUse = inifile.get("WEB", "Active").equals("1") ? true : false;
			
			//获取消息调用存储过程配置信息
			list = inifile.optionNames("OTHERS");
			this._pValueList.clear();
			for (int i=0; i<list.size(); i++) {
			    String key = list.get(i);
			    this._pValueList.put(key.toUpperCase(), inifile.get("OTHERS", key));//key+"="+inifile.get("OTHERS", key)
			}
			//获取消息体格式配置信息
			list = inifile.optionNames("BODY");
			this._pBodyList.clear();
			for (int i=0; i<list.size(); i++) {
			    String key = list.get(i);
			    this._pBodyList.put(key.toUpperCase(), inifile.get("BODY", key));
			}
			
			list = inifile.optionNames("VERSION");
			this._pVerList.clear();
			for (int i=0; i<list.size(); i++) {
			    String key = list.get(i);
			    this._pVerList.put(key.toUpperCase(), inifile.get("VERSION", key));
			}
		} catch (IOException e) {
			LoggerUtil.getLogger().error("加载{}出错，{}", CONFIG_FILE_NAME, e.getMessage());
		}
		finally {
			inifile = null;
			list = null;
		}
	}
	
	public String GetValue(String section, String key)
	{
		return this._pValueList.get(key.toUpperCase());
	}
	
	public String GetBodyValue(String section, String key)
	{
		return this._pBodyList.get(key.toUpperCase());
	}
	
	public String GetVerValue(String section, String key)
	{
		return this._pVerList.get(key.toUpperCase());
	}
	
	public String getLogicExecutorPack() {
		return this.Logic_ExecutorPack;
	}
	
	public String getLogicMessagePack() {
		return this.Logic_MessagePack;
	}
	
	public String getHbaseConfigDir() {
		return this.HBASE_CONF_DIR;
	}
	
	
	public int getDefaultResponseCommandId() {
		return this.DEF_RESPONSE;
	}
	
	//WEB站点配置信息
	public int getWebPort() {
		return this.Web_Port;
	}
	public boolean getWebIsHttps() {
		return this.Web_IsHttps;
	}
	public boolean getWebIsUse() {
		return this.Web_IsUse;
	}
	
}
