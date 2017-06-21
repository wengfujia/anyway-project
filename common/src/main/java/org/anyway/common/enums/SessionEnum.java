/**
 * 终端类型：0:pc,1:android,2:iphone,3:mac,4:web，5：微信
 * 即sessionid的枚举
 * @author wengfj
 *
 */

package org.anyway.server.web.common.enums;

public enum SessionEnum {
	
	PC("WIN客户端", 0),
	ANDROID("安卓手机端", 1),
	IPHONE("苹果手机端", 2),
	MAC("MAC客户端", 3),
	WEB("WEB客户端", 4),
	WX("微信客户端", 5);
	
	private String description;
	private int sessionid;
	
	private SessionEnum(String description, int sessionid) {
		this.description = description;
        this .sessionid = sessionid;  
    }
	
	//获取描述
	public String getDescription() {
		return this.description;
	}
	//获取标识号
	public int getSessionId() {
		return this.sessionid;
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.sessionid);
	}
}
