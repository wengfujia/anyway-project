/**
 * 微信通用借口凭证
 * @author lkl
 *
 */

package org.anyway.wechat.entity;

public class AccessToken {
	/**
	 * 获取到的凭证
	 */
	private String token;  
    
	/**
	 * 凭证有效时间，单位：秒
	 */
    private int expiresIn;

	public AccessToken() {
		super();
	}
	
	public AccessToken(String token, int expiresIn) {
		super();
		this.token = token;
		this.expiresIn = expiresIn;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}
}
