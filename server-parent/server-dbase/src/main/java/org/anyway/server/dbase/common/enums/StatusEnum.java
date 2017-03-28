/*
 * 名称: StatusEnum
 * 描述: 状态枚举类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

package org.anyway.server.dbase.common.enums;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.enums.ValuedEnum;

public class StatusEnum  extends ValuedEnum{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final StatusEnum INVALID = new StatusEnum("INVALID", 0); //无效
	public static final StatusEnum EFFECTIVE = new StatusEnum("EFFECTIVE", 1);//有效
	public static final StatusEnum LOCK = new StatusEnum("LOCK", 2);
	
	public static final StatusEnum LOGINSUCESS = new StatusEnum("LOGINSUCESS", 10);
	public static final StatusEnum LOGINERRORPWD = new StatusEnum("LOGINERRORPWD", 11);
	public static final StatusEnum LOGINEXCEPTION = new StatusEnum("LOGINEXCEPTION", 12);
		
	private StatusEnum(String name, int value) {
		super(name, value);
	}
 
	public static StatusEnum getEnum(String Status) {
	    return (StatusEnum) getEnum(StatusEnum.class, Status);
	}

	public static StatusEnum getEnum(int Status) {
	    return (StatusEnum) getEnum(StatusEnum.class, Status);
	}

	@SuppressWarnings("rawtypes")
	public static Map getEnumMap() {
		return getEnumMap(StatusEnum.class);
	}

	@SuppressWarnings("rawtypes")
	public static List getEnumList() {
	    return getEnumList(StatusEnum.class);
	}

	@SuppressWarnings("rawtypes")
	public static Iterator iterator() {
	    return iterator(StatusEnum.class);
	}
}
