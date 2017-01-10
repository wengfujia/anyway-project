/*
 * 名称: uLogger
 * 描述: 日志打印类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年08月18日
 * 修改日期:
 */

package org.anyway.common.utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.anyway.common.uConfigVar;

public class uLogger {

	static boolean debug = false;
	static final public String RT = "\n";
	static protected Logger logger = LoggerFactory.getLogger(uLogger.class.getName());
	
	/**
	 * 
	 * @return
	 */
	static public Logger getLogger() {
		return logger;
	}

	/**
	 * 
	 * @param myLogger
	 */
	static public void setLogger(Logger myLogger) {
		logger = myLogger;
	}
	
	/**
	 * 设是否显示调试显示
	 * @param value
	 */
	public static void setDebug(boolean value) {
		debug = value;
	}
	
	/**
	 * 打印mina loginfo
	 * @param info
	 */
	public static void printInfo(String info) {
		if (debug) {
			logger.info(info);
		}
	}
	
	/**
	 * 打印mina loginfo
	 * @param info
	 * @param flag
	 * @throws UnsupportedEncodingException 
	 */
	public static void printInfo(byte[] info) throws UnsupportedEncodingException {
		if (debug) {
			String sinfo = uNetUtil.getString(info, uConfigVar.CharsetName);
			logger.info(sinfo);
		}
	}
	
	public static void printInfo(String format,Object...replace) {
		if (debug) {
			Date now = new Date();  
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd   HH:mm:ss");//可以方便地修改日期格式  
			String strDate = dateFormat.format(now);
			logger.info("[" +strDate + "]" + sprintf(format, replace));
		}
	}
	
	/**
	 * 打印消息
	 * @param log
	 */
	public static void println(String log)
	{
		if (debug && uStringUtil.empty(log)==false) {
			Date now = new Date();  
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd   HH:mm:ss");//可以方便地修改日期格式  
			String strDate = dateFormat.format(now);
			System.out.println("[" +strDate + "]" + log);  
		}
	}
	
	/**
	 * 打印消息
	 * @param format
	 * @param replace
	 */
	public static void println(String format,Object...replace)
	{
		if (debug) {
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd   HH:mm:ss");//可以方便地修改日期格式  
			String strDate = dateFormat.format(now);
			System.out.println("[" +strDate + "]" + sprintf(format, replace));  
		}
	}
	
	/**
	 * sprintf 仿c++
	 * @param result
	 * @param format
	 * @param replace
	 */
	public static void sprintf(StringBuffer result, String format,Object...replace)
	{
		if (debug) {
			result.append(sprintf(format, replace));
		}
		//println(result.toString());
	}
	
	public static String sprintf(String format,Object...replace)
	{
		return String.format(format, replace);
	}
}
