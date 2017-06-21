package org.anyway.server.web.common.models;

/*
 * 名称: CategoryBean
 * 描述: 缓存数据类基类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

@SuppressWarnings("serial")
public class CategoryBean implements java.io.Serializable {
	private int key;
	private String value;
	
	/**
	 * 获取key
	 * @return int
	 */
	public int getKey() {
        return key;
    }
	/**
	 * 设置key
	 * @param ikey
	 */
    public void setKey(int ikey) {
        this.key = ikey;
    }

    /**
	 * 获取值
	 * @return String
	 */
	public String getValue() {
        return value;
    }
	/**
	 * 设置值
	 * @param svalue
	 */
    public void setValue(String svalue) {
        this.value = svalue;
    }
    
}
