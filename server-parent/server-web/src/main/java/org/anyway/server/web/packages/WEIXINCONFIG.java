/*
 * 名称: WEIXINCONFIG
 * 描述: 微信配置表
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package org.anyway.server.web.packages;

import org.anyway.wechat.entity.AccessToken;

@SuppressWarnings("serial")
public class WEIXINCONFIG implements java.io.Serializable {
	
	private String key;
	private String appid;
	private String appsecret;
	private String token;
	private AccessToken accesstoken;
	private long time;
	
	/**
	 * 构造函数
	 */
	public WEIXINCONFIG() {
		this.time = System.currentTimeMillis();
	}
	
	/**
	 * 平台接入密钥，用于区分不同接入的公众号
	 * @return
	 */
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * 微信appid号
	 * @return
	 */
	public String getAppID() {
		return this.appid;
	}
	public void setAppID(String appId) {
		this.appid = appId;
	}
	
	/**
	 * 微信app密钥
	 * @return
	 */
	public String getAppSecret() {
		return this.appsecret;
	}
	public void setAppSecret(String appSecret) {
		this.appsecret = appSecret;
	}
	
	/**
	 * 微信地址签权口令
	 * @return
	 */
	public String getToken() {
		return this.token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	/**
	 * 微信令牌
	 * @return
	 */
	public AccessToken getAccessToken() {
		return this.accesstoken;
	}
	public void setAccessToken(AccessToken accessToken) {
		this.accesstoken = accessToken;
	}
	
	/**
	 * 获取状态变化后距现在的时间秒数
	 * @return
	 */
	public long getTimes() {
		long times = (System.currentTimeMillis() - this.time) / 1000;
		return times;
	}
}
