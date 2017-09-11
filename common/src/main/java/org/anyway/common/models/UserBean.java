/*
 * 名称: UserBean
 * 描述: 用户类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

package org.anyway.common.models;

import java.util.Date;

@SuppressWarnings("serial")
public class UserBean extends BeanBase {

	private String loginname;
	private String password;
	private Date lastlogintime;
	private String lastloginIP;
	private int state;
	
	/**
	 * 登录帐号
	 * @return String
	 */
	public String getLoginName() {
        return loginname;
    }
	/**
	 * 登录帐号
	 * @param sLoginName
	 */
    public void setLoginName(String sLoginName) {
        this.loginname = sLoginName;
    } 
    
    /**
	 * 登录密码
	 * @return String
	 */
	public String getPassword() {
        return password;
    }
	/**
	 * 登录密码
	 * @param sPassword
	 */
    public void setPassword(String sPassword) {
        this.password = sPassword;
    } 
    
    /**
	 * 最后登录时间
	 * @return Date
	 */
	public Date getLastLoginTime() {
        return lastlogintime;
    }
	/**
	 * 最后登录时间
	 * @param LastLoginTime
	 */
    public void setLastLoginTime(Date LastLoginTime) {
        this.lastlogintime = LastLoginTime;
    }
    
    /**
	 * 最后登录IP
	 * @return String
	 */
	public String getLastLoginIP() {
        return lastloginIP;
    }
	/**
	 * 最后登录IP
	 * @param sLastLoginIP
	 */
    public void setLastLoginIP(String sLastLoginIP) {
        this.lastloginIP = sLastLoginIP;
    }
    
    /**
	 * 用户的状态
	 * @return int
	 */
	public int getState() {
        return state;
    }
	/**
	 * 用户的状态
	 * @param sLastLoginIP
	 */
    public void setState(int iState) {
        this.state = iState;
    }
}
