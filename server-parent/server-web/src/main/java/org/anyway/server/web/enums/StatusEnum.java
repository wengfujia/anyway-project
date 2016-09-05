/*
 * 名称: StatusEnum
 * 描述: 状态枚举类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

package org.anyway.server.web.enums;

public enum StatusEnum {
	INVALID("无效", 0),
	EFFECTIVE("有效", 1),
	LOCK("帐号锁住", 2),
	LOGINSUCESS("登录成功", 10),
	LOGINERRORPWD("登录密码错误", 11),
	LOGINEXCEPTION("登录出错", 12),
	
	EXCEPTION("系统出错", -10),
	ERROR("错误", -11),
	EMPTY("查询为空", -12);
	
	private String desc;
	private int value;
	
	StatusEnum(String desc, int value ) {
		this.desc = desc;
		this.value = value;
	}
	
	public String getDesc() {
		return this.desc;
	}
	
	public int getValue() {
		return this.value;
	}
	
}
