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
 * 2016.6.26
 * 全局变量移到common.uConfigVar类中
 */

package org.anyway.server.web.common;

import java.util.ArrayList;
import java.util.List;

import com.nikhaldimann.inieditor.IniEditor;

import org.anyway.common.uConfigVar;

public class uLoadVar extends uConfigVar {
	
	public final static String CONFIG_FILE_NAME = "./cfg/setting.properties";

	/**
	 * Load config inifile
	 * @throws Exception 
	 */
	public static void LoadIni() throws Exception {
		
		IniEditor inifile = new IniEditor();
		inifile.load(CONFIG_FILE_NAME);
		//读取全局配置信息
		CharsetName = inifile.get("SET", "CharsetName");
		Logic_ExecutorPack = inifile.get("SET", "LogicExecutorPack");
		Logic_MessagePack = inifile.get("SET", "LogicMessagePack");
		SERVERTYPE = Integer.parseInt(inifile.get("SET", "ServerType"));
		DEBUG = inifile.get("SET", "DEBUG").equals("1") ? true:false; 
	
		//读取hbase配置信息
		HBASE_CONF_DIR = inifile.get("HBASE", "HbaseConfDir");
		
		//读取socket相关的配置信息
		US_WaitTimeOut = Integer.parseInt(inifile.get("UCI_SOCK", "WaitTime"));
		US_IdleTimeOut = Integer.parseInt(inifile.get("UCI_SOCK", "IdleTimeOut"));
		US_RWTimeOut = Integer.parseInt(inifile.get("UCI_SOCK", "RWTimeOut"));
		US_Port = Integer.parseInt(inifile.get("UCI_SOCK", "PORT"));
		US_WorkThreadCount = Integer.parseInt(inifile.get("UCI_SOCK", "WorkThreadCount"));
		US_MaxSendBufferSize = Integer.parseInt(inifile.get("UCI_SOCK", "MaxSendBufferSize"));
		US_MaxReadBufferSize = Integer.parseInt(inifile.get("UCI_SOCK", "MaxReaddBufferSize"));
		US_Active = inifile.get("UCI_SOCK", "Active").equals("1") ? true:false;
		
		//读取Http相关的配置信息
		HT_Port = Integer.parseInt(inifile.get("HTTP", "PORT"));
		HT_WaitTimeOut = Integer.parseInt(inifile.get("HTTP", "WaitTime"));
		HT_IdleTimeOut = Integer.parseInt(inifile.get("HTTP", "IdleTimeOut"));
		HT_RWTimeOut = Integer.parseInt(inifile.get("HTTP", "RWTimeOut"));
		HT_WorkThreadCount = Integer.parseInt(inifile.get("HTTP", "WorkThreadCount"));
		HT_MaxSendBufferSize = Integer.parseInt(inifile.get("HTTP", "MaxSendBufferSize"));
		HT_MaxReadBufferSize = Integer.parseInt(inifile.get("HTTP", "MaxReaddBufferSize"));
		HT_Crypt = Integer.parseInt(inifile.get("HTTP", "Crypt"));
		HT_IsHttps = inifile.get("HTTP", "HTTPS").equals("1") ? true:false;
		HT_Active = inifile.get("HTTP", "Active").equals("1") ? true:false;
		
		//读取版本信息
		List<String> list = new ArrayList<String>();
		list = inifile.optionNames("VERSION");
		_pVerList.clear();
		for (int i=0; i<list.size(); i++) {
		    String key = list.get(i).toLowerCase();
		    _pVerList.put(key, inifile.get("VERSION", key));
		}
		
		//获取消息体格式配置信息
		list = inifile.optionNames("BODY");
		_pBodyList.clear();
		for (int i=0; i<list.size(); i++) {
		    String key = list.get(i).toLowerCase();
		    _pBodyList.put(key, inifile.get("BODY", key));
		}
		
		//读取需要发送返回包的消息头
		_arks = inifile.get("ARK", "CMDS");
		//默认返回的业务标识号
		DEF_RESPONSE = Integer.parseInt(inifile.get("RESPONSE", "CMD"));
		
		//获取消息调用存储过程配置信息
		list = inifile.optionNames("OTHERS");
		_pValueList.clear();
		for (int i=0; i<list.size(); i++) {
		    String key = list.get(i).toLowerCase();
		    _pValueList.put(key, inifile.get("OTHERS", key));
		}

		inifile = null;
		list = null;
	}
	
	/**
	 * 获取版本号
	 * @param section
	 * @param key
	 * @return
	 */
	public static String GetVerValue(String section, String key)
	{
		return _pVerList.get(key.toLowerCase());
	}
	
	/**
	 * 获取包需要解码的消息
	 * @param section
	 * @param key
	 * @return
	 */
	public static String GetBodyValue(String section, String key)
	{
		return _pBodyList.get(key.toLowerCase());
	}
	
	/**
	 * 获取消息头值
	 * @param section
	 * @param key
	 * @return
	 */
	public static String GetValue(String section, String key)
	{
		return _pValueList.get(key.toLowerCase());
	}
	
	/**
	 * 判断是否需要进行应签
	 * 包转输到服务端后，根据此配置决定是否需要应答该包
	 * @param commandid
	 * @return
	 */
	public static Boolean IsArk(int commandid)
	{
		Boolean result = false;
		if (_arks.indexOf(commandid) > 0) {
			result = true;
		}
		return result;	
	}
	
	/**
	 * 判断服务端是否为微信服务端
	 * @return
	 */
	public static Boolean IsWeixinServer() {
		return SERVERTYPE == 0 ? true:false;
	}
	
}
