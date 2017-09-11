package org.anyway.common;

/*
 * 名称: uGlobalVar
 * 描述: 全局变量类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 * 	2015.8.19
 * 		用户名长度由17位修改为35位，HEADER_LENGTH增加18位由156位调整为174位
 */

public class SystemConfig {
	
	public static boolean DEBUG = true;	//是否启用调试
	public static String CharsetName = "utf-8";
	
	//包长度定义
	public final static int HEADER_LENGTH = 198;
	public final static int MSG_SEPATATE_LEN = 1;
	public final static char MSG_SEPATATE = '\t';
	public final static int MSG_SEPATATE_LINE_LEN = 1;
	public final static char MSG_SEPATATE_LINE = '\n';	

	/**
	 * map key的分隔符
	 */
	public final static String KEY_SEPATATE = "@";
	
	//线程相关标识定义
	public final static int RETURN_SUCCESS = 0;
	public final static int TRACE_ERROR  = -1;
	public final static int TRACE_NORMAL = 0;
	public final static int QUERY_LINK_PACKET = 1;
	public final static int RECEIVE_NORMAL_PACKET = 2;
	public final static int RECEIVE_RIGHT_PACKET = 3;
	public final static int RECEIVE_WRONG_PACKET = 4;
	public final static int RECEIVE_REPEAT_PACKET = 5;	
		
	public final static int THREAD_NORMAL_PACKET = 2;
	public final static int THREAD_RIGHT_PACKET = 3;
	public final static int THREAD_WRONG_PACKET = 4;
	public final static int THREAD_USERNAME = 5;
	public final static int THREAD_RUNNING = 6;
	public final static int THREAD_IDLE = 7;
	public final static int THREAD_ERROR = 8;
	public final static int THREAD_START = 11;
	public final static int THREAD_STOP = 12;

	//其它定义
	public final static int RETRY = 3; //重试次数
	
}
