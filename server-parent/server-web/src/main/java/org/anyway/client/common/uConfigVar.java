/*
 * 名称: uConfigVar
 * 描述: 系统配置信息类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年6月4日
 */

package org.anyway.client.common;

import java.util.HashMap;
import java.util.Map;

import com.nikhaldimann.inieditor.IniEditor;

public class uConfigVar {
	
	public static Map<Object, Object> httplist = new HashMap<Object, Object>();

	public final static String AppPath = System.getProperty("user.dir");
	
	public static String CharsetName = "UTF-8";	//编码
	public static int DEBUG = 1;				//是否启用调试
	
	public static int US_WaitTimeOut = 3000;	//等待时间
	public static int US_IdleTimeOut = 2000;	//Idle超时时间
	public static int US_RWTimeOut = 3000;		//网络读写超时时间
	public static int US_Port = 8083; 			//侦听端口
	public static int US_WorkThreadCount = 20;  //工作线程数

	/**
	 * Load config inifile
	 * @throws Exception 
	 */
	public static void LoadIni() throws Exception {		
		IniEditor inifile = new IniEditor();
		inifile.load("./cfg/client.properties");
		CharsetName = inifile.get("SET", "CharsetName");
		DEBUG = Integer.parseInt(inifile.get("SET", "DEBUG")); 
	
		US_WaitTimeOut = Integer.parseInt(inifile.get("UCI_SOCK", "WaitTime"));
		US_IdleTimeOut = Integer.parseInt(inifile.get("UCI_SOCK", "IdleTimeOut"));
		US_RWTimeOut = Integer.parseInt(inifile.get("UCI_SOCK", "RWTimeOut"));
		US_Port = Integer.parseInt(inifile.get("UCI_SOCK", "PORT"));
		US_WorkThreadCount = Integer.parseInt(inifile.get("UCI_SOCK", "WorkThreadCount"));
		
		inifile = null;
	}

}
