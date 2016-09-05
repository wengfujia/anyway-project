package org.anyway.common.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/*
 * 名称: uFileUtils
 * 描述: 文件操作相关类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

public class uFileUtils {
	
	/**
	 * 创建文件
	 * @param fileName
	 */
	public static void createFile(String fileName) {
		File file = new File(fileName);
		try {
			if (!file.exists()) // 如果文件不存在,则新建.
			{
				file.createNewFile();
			}
			else { //已经存在，删除重建
				file.delete();
				file.createNewFile();
			}
			file = null;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 添加文件内容
	 * @param fileName
	 * @param content
	 */
	public static void appendFile(String fileName, String content) {
		Scanner sc = null;
		PrintWriter pw = null;
		File file = new File(fileName);
		try {
			if (!file.exists()) // 如果文件不存在,则新建.
			{
				file.createNewFile();
			}
			sc = new Scanner(file);
			StringBuilder sb = new StringBuilder();
			while (sc.hasNextLine())// 先读出旧文件内容,并暂存sb中;
			{
				sb.append(sc.nextLine());
				sb.append("\r\n");// 换行符作为间隔,扫描器读不出来,因此要自己添加.
			}
			sc.close();

			pw = new PrintWriter(new FileWriter(file), true);
			/*
			 * A.
			 */
			pw.println(sb.toString());// ,写入旧文件内容.
			/*
			 * B.
			 */
			pw.println(content);// 写入内容.
			/*
			 * 如果先写入A,最近日志在文件最后. 如是先写入B,最近内容在文件最前.
			 */
			pw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
}
