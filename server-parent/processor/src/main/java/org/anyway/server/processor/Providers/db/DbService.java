package org.anyway.server.processor.Providers.db;

import java.sql.SQLException;

public class DbService {
	//synchronized用于arrarylist线程不安全
	
	/** 取出所有的TbError记录
	 * @return void
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public static void FillErrors() throws SQLException, Exception {
		DbProvider db = new DbProvider();
		db.FillErrors();
	}
	
}
