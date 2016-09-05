/*
 * 名称: HEADER.java
 * 描述: 包头定义 ，用于网络包传输类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年08月16日
 * 修改日期:
 */

package org.anyway.server.data.packages;

@SuppressWarnings("serial")
public class HEADER implements java.io.Serializable {

	//包头内部字段定义
	private int len = 0; 
	private int commandid = 0;
	private int status = 0;
	private String sequence = "";     	//序列号 17位长
	private String acknowledge = "";  	//确认
	private String sessionid = "";		//session标识  0:pc,1:android,2:iphone,3:mac,4:web,5:weixin;
	private int resptype = 0;      		//反馈类型: 0 表示还有后续包，1表示是最后一个包了
	private String user = "";
	private String pwd = "";
	private String ip = "";
	private String version = "";       	//版本信息
	private String reserve = "";
	
	/**
	 * 空的构造函数
	 */
	public HEADER() {
		
	}
	
	/**
	 * 构造函数
	 * @param header
	 */
	public HEADER(HEADER header) {
		this.len = header.getLen();
		this.commandid = header.getCommandID();
		this.status = header.getStatus();
		this.sequence = header.getSequence();
		this.acknowledge = header.getAcknowledge();
		this.sessionid = header.getSessionid();
		this.resptype = header.getResptype();
		this.user = header.getUser();
		this.pwd = header.getPwd();
		this.ip = header.getIP();
		this.version = header.getVersion();
		this.reserve = header.getReserve();
	}
	
	//字段定义，允许外部访问
	//包长度 7位
	public int getLen() {
        return len;
    }
    public void setLen(int iLen) {
        this.len = iLen;
    }
    
    //包标识号  6位
    public int getCommandID() {
        return commandid;
    }
    public void setCommandID(int iCommandid) {
        this.commandid = iCommandid;
    }
    
    //包标状态  6位
    public int getStatus() {
        return status;
    }
    public void setStatus(int iStatus) {
        this.status = iStatus;
    }
    //设置状态并返回头部
    public HEADER Status(int iStatus) {
    	this.status = iStatus;
    	return this;
    }
    
    //包标序列号  17位
    public String getSequence() {
        return sequence;
    }
    public void setSequence(String sSequence) {
        this.sequence = sSequence.trim();
    } 
    
    //确认  17位
    public String getAcknowledge() {
        return acknowledge;
    }
    public void setAcknowledge(String sAcknowledge) {
        this.acknowledge = sAcknowledge.trim();
    }    
     
    //session标识  5位
    public String getSessionid() {
        return sessionid;
    }
    public void setSessionid(String sSessionid) {
        this.sessionid = sSessionid.trim();
    }   
    
    //反馈类型: 0 表示还有后续包，1表示是最后一个包了 3位
    public int getResptype() {
        return resptype;
    }
    public void setResptype(int iResptype) {
        this.resptype = iResptype;
    }
    
    //登录帐号  17位
    public String getUser() {
        return user;
    }
    public void setUser(String sUser) {
        this.user = sUser.trim();
    } 
    
    //登录密码  35位
    public String getPwd() {
        return pwd;
    }
    public void setPwd(String sPwd) {
        this.pwd = sPwd.trim();
    } 
    
    //IP地址
    public String getIP() {
        return ip;
    }
    public void setIP(String sIP) {
        this.ip = sIP.trim();
    } 
    
    //版本信息  4位
    public String getVersion() {
        return version;
    }
    public void setVersion(String sVersion) {
        this.version = sVersion.trim();
    } 
    
    //保留  39位
    public String getReserve() {
        return reserve;
    }
    public void setReserve(String sReserve) {
        this.reserve = sReserve.trim();
    }    
    
    //清空函数
    public void Clear() {
    	len = 0; 
    	commandid = 0;
    	status = 0;
    	sequence = "";     //序列号
    	acknowledge = "";  //确认
    	sessionid = "";     //session标识
    	resptype = 0;      //反馈类型: 0 表示还有后续包，1表示是最后一个包了
    	user = "";
    	pwd = "";
    	ip = "";
    	version = "";       //版本信息
    	reserve = "";  
    }
    
}
