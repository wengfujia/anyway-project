/**
 * adapter服务的配置信息
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
public class AdapterConfig extends NettyServerConfig{

	public final static String CONFIG_FILE_NAME = "./cfg/setting.properties";
	
	private static AdapterConfig instance = null;
	
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
	private String _arks = "";		//读取需要发送返回包的消息头
	
	private int DEF_RESPONSE = 0;	//默认返回的业务标识号,如果没有找到业务逻辑执行类，则查找是否有默认的业务逻辑类
	private int SERVERTYPE = 0;		//服务端类型	0=>微信服务端，1=>其它服务端
	
	public static AdapterConfig getInstance() {
		if (null == instance) {
			synchronized (AdapterConfig.class) {
				instance = new AdapterConfig();
			}
		}
		return instance;
	}
	
	/**
	 * Load config inifile
	 * @throws Exception 
	 */
	public void loadNettyConfig() {
		IniEditor inifile = new IniEditor();
		//读取版本信息
		List<String> list = new ArrayList<String>();
		try
		{
			inifile.load(FileUtil.toFileName(CONFIG_FILE_NAME));
			//读取全局配置信息
			SystemConfig.DEBUG = inifile.get("SET", "DEBUG").equals("1") ? true:false; 
			SystemConfig.CharsetName = inifile.get("SET", "CharsetName");
			this.Logic_ExecutorPack = inifile.get("SET", "LogicExecutorPack");
			this.Logic_MessagePack = inifile.get("SET", "LogicMessagePack");
			this.SERVERTYPE = Integer.parseInt(inifile.get("SET", "ServerType"));

			//读取socket相关的配置信息
			setUSWaitTimeOut(Integer.parseInt(inifile.get("UCI_SOCK", "WaitTime")));
			setUSIdleTimeOut(Integer.parseInt(inifile.get("UCI_SOCK", "IdleTimeOut")));
			setUSRWTimeOut(Integer.parseInt(inifile.get("UCI_SOCK", "RWTimeOut")));
			setUSPort(Integer.parseInt(inifile.get("UCI_SOCK", "PORT")));
			setUSWorkThreadCount(Integer.parseInt(inifile.get("UCI_SOCK", "WorkThreadCount")));
			setUSMaxSendBufferSize(Integer.parseInt(inifile.get("UCI_SOCK", "MaxSendBufferSize")));
			setUSMaxReadBufferSize(Integer.parseInt(inifile.get("UCI_SOCK", "MaxReaddBufferSize")));
			setUSActive(inifile.get("UCI_SOCK", "Active").equals("1") ? true:false);
			
			//读取Http相关的配置信息
			setHTPort(Integer.parseInt(inifile.get("HTTP", "PORT")));
			setHTWaitTimeOut(Integer.parseInt(inifile.get("HTTP", "WaitTime")));
			setHTIdleTimeOut(Integer.parseInt(inifile.get("HTTP", "IdleTimeOut")));
			setHTRWTimeOut(Integer.parseInt(inifile.get("HTTP", "RWTimeOut")));
			setHTWorkThreadCount(Integer.parseInt(inifile.get("HTTP", "WorkThreadCount")));
			setHTMaxSendBufferSize(Integer.parseInt(inifile.get("HTTP", "MaxSendBufferSize")));
			setHTMaxReadBufferSize(Integer.parseInt(inifile.get("HTTP", "MaxReaddBufferSize")));
			setHTCrypt(Integer.parseInt(inifile.get("HTTP", "Crypt")));
			setHTIsHttps(inifile.get("HTTP", "HTTPS").equals("1") ? true:false);
			setHTActive(inifile.get("HTTP", "Active").equals("1") ? true:false);
			
			list = inifile.optionNames("VERSION");
			this._pVerList.clear();
			for (int i=0; i<list.size(); i++) {
			    String key = list.get(i);
			    this._pVerList.put(key.toUpperCase(), inifile.get("VERSION", key));
			}
			
			//获取消息体格式配置信息
			list = inifile.optionNames("BODY");
			this._pBodyList.clear();
			for (int i=0; i<list.size(); i++) {
			    String key = list.get(i);
			    this._pBodyList.put(key.toUpperCase(), inifile.get("BODY", key));
			}
			
			//读取需要发送返回包的消息头
			this._arks = inifile.get("ARK", "CMDS");
			//默认返回的业务标识号
			this.DEF_RESPONSE = Integer.parseInt(inifile.get("RESPONSE", "CMD"));
			
			//获取消息调用存储过程配置信息
			list = inifile.optionNames("OTHERS");
			this._pValueList.clear();
			for (int i=0; i<list.size(); i++) {
			    String key = list.get(i);
			    this._pValueList.put(key.toUpperCase(), inifile.get("OTHERS", key));
			}
		} catch (IOException e) {
			LoggerUtil.getLogger().error("加载{}出错，{}", CONFIG_FILE_NAME, e.getMessage());
		}
		finally {
			inifile = null;
			list = null;
		}
	}
	
	/**
	 * 获取版本号
	 * @param section
	 * @param key
	 * @return
	 */
	public String GetVerValue(String section, String key)
	{
		return this._pVerList.get(key.toUpperCase());
	}
	
	/**
	 * 获取包需要解码的消息
	 * @param section
	 * @param key
	 * @return
	 */
	public String GetBodyValue(String section, String key)
	{
		return this._pBodyList.get(key.toUpperCase());
	}
	
	/**
	 * 获取消息头值
	 * @param section
	 * @param key
	 * @return
	 */
	public String GetValue(String section, String key)
	{
		return this._pValueList.get(key.toUpperCase());
	}
	
	/**
	 * 判断是否需要进行应签
	 * 包转输到服务端后，根据此配置决定是否需要应答该包
	 * @param commandid
	 * @return
	 */
	public Boolean IsArk(int commandid)
	{
		Boolean result = false;
		if (this._arks.indexOf(commandid) > 0) {
			result = true;
		}
		return result;	
	}
	
	/**
	 * 判断服务端是否为微信服务端
	 * @return
	 */
	public Boolean IsWeixinServer() {
		return this.SERVERTYPE == 0 ? true:false;
	}
	
	public String getLogicExecutorPack() {
		return this.Logic_ExecutorPack;
	}
	
	public String getLogicMessagePack() {
		return this.Logic_MessagePack;
	}
	
	public int getDefaultResponseCommandId() {
		return this.DEF_RESPONSE;
	}
	
	public int getServerType() {
		return this.SERVERTYPE;
	}
	
}
