/*
 * 名称: uConfigVar
 * 描述: 系统配置信息类，从配置文件中获取的全局变量
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

package org.anyway.common;

import java.util.HashMap;
import java.util.Map;

public abstract class uConfigVar {
	
	//全局信息配置
	public static boolean DEBUG = true;	//是否启用调试
	public static String CharsetName = "utf-8";	
	
	//插件包
	public static String Logic_ExecutorPack = "com.business.executor";	//业务执行包
	public static String Logic_MessagePack = "com.business.message";	//业务执行消息包
	
	//业务标识、版本配置信息
	protected static Map<String, String> _pValueList = new HashMap<String, String>();
	protected static Map<String, String> _pBodyList = new HashMap<String, String>();
	protected static Map<String, String> _pVerList = new HashMap<String, String>();
	protected static String _arks = "";			//读取需要发送返回包的消息头
	
	public static String HBASE_CONF_DIR = "";	//hbase配置目录
	public static int DEF_RESPONSE = 0;			//默认返回的业务标识号,如果没有找到业务逻辑执行类，则查找是否有默认的业务逻辑类
	public static int SERVERTYPE = 0;			//服务端类型	0=>微信服务端，1=>其它服务端
	
	//SOCKET配置信息
	public static int US_WaitTimeOut = 3000;	//等待时间
	public static int US_IdleTimeOut = 2000;	//Idle超时时间
	public static int US_RWTimeOut = 3000;		//网络读写超时时间
	public static int US_Port = 8083; 			//侦听端口
	public static int US_WorkThreadCount = 20;  //工作线程数
	public static int US_MaxSendBufferSize = 20480;//发送缓存池大小
	public static int US_MaxReadBufferSize = 20480;//接收缓存池大小
	public static boolean US_IpFilter = false;	//是否ip过滤
	public static boolean US_Active = true;		//是否打开socket端口
	
	//HTTP配置信息
	public static int HT_Port = 8080;			//HTTP服务端端口
	public static int HT_WaitTimeOut = 30000;	//异步超时时间
	public static int HT_IdleTimeOut = 2000;	//Idle超时时间
	public static int HT_RWTimeOut = 3000;		//网络读写超时时间
	public static int HT_WorkThreadCount = 20;  //工作线程数
	public static int HT_MaxSendBufferSize = 20480;	//发送缓存池大小
	public static int HT_MaxReadBufferSize = 20480;	//接收缓存池大小
	public static int HT_Crypt = 1;					//是否加密，目 前对微信不支持
	public static boolean HT_IsHttps = false;	//是否启用HTTPS
	public static boolean HT_IpFilter = false;	//是否ip过滤
	public static boolean HT_Active = true;		//是否打开httpt端口
	
	//WEB站点配置信息
	public static int Web_Port = 80; 			 	//WEB端端口
	public static int Web_IsHttps = 0;				//是否启用HTTPS 1启用 0不启用
	public static int Web_IsUse = 1;				//是否启用WEB	
	
	//数据库配置信息
	public static String SID = "192.168.0.200"; //数据库
	public static String UserID = "appsrv";
	public static String Pwd = "appsrv123";
	public static String TbERROR = "ry_error";	//错误定义表
	public static String TbUSER = "ry_users";	//用户表
	
}
