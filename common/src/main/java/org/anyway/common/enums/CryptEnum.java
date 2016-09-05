/*
 * 名称: CryptEnum
 * 描述: CryptEnum的枚举类型类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年08月18日
 * 修改日期:
 */

package org.anyway.common.enums;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.enums.ValuedEnum;

/**
 * 
 * @author wengfj
 *
 */
public final class CryptEnum  extends ValuedEnum {
	/**
	 * 序列化版本号 无用处
	 */
	private static final long serialVersionUID = 3087290783972247136L;
	public static final CryptEnum NONE = new CryptEnum("NONE", 0);
	public static final CryptEnum DES = new CryptEnum("3DES", 1);
	public static final CryptEnum Crypt = new CryptEnum("Crypt", 2);

	private CryptEnum(String name, int value) {
		super(name, value);
	}
 
	public static CryptEnum getEnum(String CryptName) {
	    return (CryptEnum) getEnum(CryptEnum.class, CryptName);
	}

	public static CryptEnum getEnum(int CryptName) {
	    return (CryptEnum) getEnum(CryptEnum.class, CryptName);
	}

	@SuppressWarnings("rawtypes")
	public static Map getEnumMap() {
		return getEnumMap(CryptEnum.class);
	}

	@SuppressWarnings("rawtypes")
	public static List getEnumList() {
	    return getEnumList(CryptEnum.class);
	}

	@SuppressWarnings("rawtypes")
	public static Iterator iterator() {
	    return iterator(CryptEnum.class);
	}
}
