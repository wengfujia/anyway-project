/**
 * adapter服务层client的配置信息
 */
package org.anyway.common;

import java.io.IOException;

import org.anyway.common.utils.FileUtil;
import org.anyway.common.utils.LoggerUtil;

import com.nikhaldimann.inieditor.IniEditor;

/**
 * @author wengfj
 *
 */
public class ClientConfig {
	private static ClientConfig instance = null;
	
	private int port = 8083; 				//侦听端口
	private int connectTimeOut = 3000;  	//连接超时时间
	private int waitTimeOut = 3000;			//等待时间
	private int idleTimeOut = 2000;			//Idle超时时间
	private int rwTimeOut = 3000;			//网络读写超时时间
	private int workThreadCount = 20;  		//工作线程数
	
	public static ClientConfig getInstance() {
		if (null == instance) {
			synchronized (ClientConfig.class) {
				if (null == instance) {
					instance = new ClientConfig();
					instance.init();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Load config inifile
	 * @throws Exception 
	 */
	public void init() {		
		IniEditor inifile = new IniEditor();
		try
		{
			inifile.load(FileUtil.toFileName("./cfg/client.properties"));
			this.connectTimeOut = Integer.parseInt(inifile.get("UCI_SOCK", "ConnectTimeOut"));
			this.waitTimeOut = Integer.parseInt(inifile.get("UCI_SOCK", "WaitTime"));
			this.idleTimeOut = Integer.parseInt(inifile.get("UCI_SOCK", "IdleTimeOut"));
			this.rwTimeOut = Integer.parseInt(inifile.get("UCI_SOCK", "RWTimeOut"));
			this.port = Integer.parseInt(inifile.get("UCI_SOCK", "PORT"));
			this.workThreadCount = Integer.parseInt(inifile.get("UCI_SOCK", "WorkThreadCount"));
		} catch (IOException e) {
			LoggerUtil.getLogger().error("加载cfg/client.properties出错，{}", e.getMessage());
		}
		finally {
			inifile = null;
		}
	}
	
	public int getPort() {
		return this.port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getConnectTimeOut() {
		return this.connectTimeOut;
	}
	public void setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}
	
	public int getWaitTimeOut() {
		return this.waitTimeOut;
	}
	public void setWaitTimeOut(int waitTimeOut) {
		this.waitTimeOut = waitTimeOut;
	}
	
	public int getIdleTimeOut() {
		return this.idleTimeOut;
	}
	public void setIdleTimeOut(int idleTimeOut) {
		this.idleTimeOut = idleTimeOut;
	}
	
	public int getRWTimeOut() {
		return this.rwTimeOut;
	}
	public void setRWTimeOut(int rwTimeOut) {
		this.rwTimeOut = rwTimeOut;
	}
	
	public int getWorkThreadCount() {
		return this.workThreadCount;
	}
	public void setWorkThreadCount(int workThreadCount) {
		this.workThreadCount = workThreadCount;
	}
	
}
