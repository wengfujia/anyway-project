package org.anyway.server.data.packages.json;
/*
 * 名称: JBuffer
 * 描述: Json解析包
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2014年2月2日
 * 修改日期:
 */

@SuppressWarnings("serial")
public class JBuffer <T> implements java.io.Serializable {
	
	private int commandid;
	private String sessionid;
	private String username;
	private String password;
	private String version;
	private T body;
	
	public void Clear() {
		commandid = 0;
		sessionid = "";
		username = "";
		password = "";
		version = "";
		body = null;
	}
	/**
	 * 标识号
	 * @return String
	 */
	public int getCommandId() {
        return commandid;
    }
	/**
	 * 标识号
	 * @param sCommandId
	 */
    public void setCommandId(int commandId) {
        this.commandid = commandId;
    } 
    
    /**
	 * SessionID号 
	 * 0:pc,1:android,2:iphone,3:mac,4:web
	 * @return String
	 */
	public String getSessionId() {
        return sessionid;
    }
	/**
	 * SessionID号
	 * 0:pc,1:android,2:iphone,3:mac,4:web
	 * @param sSessionid
	 */
    public void setSessionId(String sSessionid) {
        this.sessionid = sSessionid;
    } 
    
    /**
	 * 登录帐号
	 * @return String
	 */
	public String getUserName() {
        return username;
    }
	/**
	 * 登录帐号
	 * @param username
	 */
    public void setUserName(String sUserName) {
        this.username = sUserName;
    }
    
    /**
	 * 登录密码
	 * @return String
	 */
	public String getPassWord() {
        return password;
    }
	/**
	 * 登录密码
	 * @param sPassWord
	 */
    public void setPassWord(String sPassWord) {
        this.password = sPassWord;
    }
    
    /**
	 * 版本号
	 * @return String
	 */
	public String getVersion() {
        return version;
    }
	/**
	 * 版本号
	 * @param sVersion
	 */
    public void setVersion(String sVersion) {
        this.version = sVersion;
    }
    
    /**
	 * 内容
	 * @return Object
	 */
	public T getBody() {
        return body;
    }
	/**
	 * 内容
	 * @param body
	 */
    public void setBody(T oBody) {
        this.body = oBody;
    }
}
