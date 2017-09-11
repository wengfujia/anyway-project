package org.anyway.server.processor.Providers.db;
/*
 * 名称: Provider
 * 描述: 数据库读取表的基类，未完待续
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:
 */

import java.sql.SQLException;

public interface Provider {
	
	/**
	 * ry_errors表的操作
	 */
	/** 取出所有的TbError记录
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void FillErrors() throws SQLException, Exception;
	
}
