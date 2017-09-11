/**
 * 服务端网络配置信息
 */
package org.anyway.common;

/**
 * @author wengfj
 *
 */
public class NettyServerConfig {

	//SOCKET配置信息
	private int US_WaitTimeOut = 3000;	//等待时间
	private int US_IdleTimeOut = 2000;	//Idle超时时间
	private int US_RWTimeOut = 3000;		//网络读写超时时间
	private int US_Port = 8083; 			//侦听端口
	private int US_WorkThreadCount = 20;  //工作线程数
	private int US_MaxSendBufferSize = 20480;//发送缓存池大小
	private int US_MaxReadBufferSize = 20480;//接收缓存池大小
	private boolean US_IpFilter = false;	//是否ip过滤
	private boolean US_Active = true;		//是否打开socket端口
	
	//HTTP配置信息
	private int HT_Port = 8080;			//HTTP服务端端口
	private int HT_WaitTimeOut = 30000;	//异步超时时间
	private int HT_IdleTimeOut = 2000;	//Idle超时时间
	private int HT_RWTimeOut = 3000;		//网络读写超时时间
	private int HT_WorkThreadCount = 20;  //工作线程数
	private int HT_MaxSendBufferSize = 20480;	//发送缓存池大小
	private int HT_MaxReadBufferSize = 20480;	//接收缓存池大小
	private int HT_Crypt = 1;					//是否加密，目 前对微信不支持
	private boolean HT_IsHttps = false;	//是否启用HTTPS
	private boolean HT_IpFilter = false;	//是否ip过滤
	private boolean HT_Active = true;		//是否打开httpt端口
	
	//SOCKET配置信息
	public int getUSWaitTimeOut() {
		return this.US_WaitTimeOut;
	}
	public void setUSWaitTimeOut(int waitTimeOut) {
		this.US_WaitTimeOut = waitTimeOut;
	}
	
	public int getUSIdleTimeOut() {
		return this.US_IdleTimeOut;
	}
	public void setUSIdleTimeOut(int idleTimeOut) {
		this.US_IdleTimeOut = idleTimeOut;
	}
	
	public int getUSRWTimeOut() {
		return this.US_RWTimeOut;
	}
	public void setUSRWTimeOut(int rWTimeOut) {
		this.US_RWTimeOut = rWTimeOut;
	}
	
	public int getUSPort() {
		return this.US_Port;
	}
	public void setUSPort(int port) {
		this.US_Port = port;
	}
	
	public int getUSWorkThreadCount() {
		return this.US_WorkThreadCount;
	}
	public void setUSWorkThreadCount(int workThreadCount) {
		this.US_WorkThreadCount = workThreadCount;
	}
	
	public int getUSMaxSendBufferSize() {
		return this.US_MaxSendBufferSize;
	}
	public void setUSMaxSendBufferSize(int maxSendBufferSize) {
		this.US_MaxSendBufferSize = maxSendBufferSize;
	}
	
	public int getUSMaxReadBufferSize() {
		return this.US_MaxReadBufferSize;
	}
	public void setUSMaxReadBufferSize(int maxReadBufferSize) {
		this.US_MaxReadBufferSize = maxReadBufferSize;
	}
	
	public boolean getUSIpFilter() {
		return this.US_IpFilter;
	}
	public void setUSIpFilter(boolean ipFilter) {
		this.US_IpFilter = ipFilter;
	}
	
	public boolean getUSActive() {
		return this.US_Active;
	}
	public void setUSActive(boolean active) {
		this.US_Active = active;
	}
	
	//HTTP配置信息
	public int getHTPort() {
		return this.HT_Port;
	}
	public void setHTPort(int port) {
		this.HT_Port = port;
	}
	
	public int getHTWaitTimeOut() {
		return this.HT_WaitTimeOut;
	}
	public void setHTWaitTimeOut(int waitTimeOut) {
		this.HT_WaitTimeOut = waitTimeOut;
	}
	
	public int getHTIdleTimeOut() {
		return this.HT_IdleTimeOut;
	}
	public void setHTIdleTimeOut(int idleTimeOut) {
		this.HT_IdleTimeOut = idleTimeOut;
	}
	
	public int getHTRWTimeOut() {
		return this.HT_RWTimeOut;
	}
	public void setHTRWTimeOut(int rWTimeOut) {
		this.HT_RWTimeOut = rWTimeOut;
	}
	
	public int getHTWorkThreadCount() {
		return this.HT_WorkThreadCount;
	}
	public void setHTWorkThreadCount(int workThreadCount) {
		this.HT_WorkThreadCount = workThreadCount;
	}
	
	public int getHTMaxSendBufferSize() {
		return this.HT_MaxSendBufferSize;
	}
	public void setHTMaxSendBufferSize(int maxSendBufferSize) {
		this.HT_MaxSendBufferSize = maxSendBufferSize;
	}
	
	public int getHTMaxReadBufferSize() {
		return this.HT_MaxReadBufferSize;
	}
	public void setHTMaxReadBufferSize(int maxReadBufferSize) {
		this.HT_MaxReadBufferSize = maxReadBufferSize;
	}
	
	public int getHTCrypt() {
		return this.HT_Crypt;
	}
	public void setHTCrypt(int crypt) {
		this.HT_Crypt = crypt;
	}
	
	public boolean getHTIsHttps() {
		return this.HT_IsHttps;
	}
	public void setHTIsHttps(boolean isHttps) {
		this.HT_IsHttps = isHttps;
	}
	
	public boolean getHTIpFilter() {
		return this.HT_IpFilter;
	}
	public void setHTIpFilter(boolean ipFilter) {
		this.HT_IpFilter = ipFilter;
	}
	
	public boolean getHTActive() {
		return this.HT_Active;
	}
	public void setHTActive(boolean active) {
		this.HT_Active = active;
	}
	
}
