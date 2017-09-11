/*
 * 名称: DbExecutorImpl
 * 描述: 数据库插件基类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年12月14日
 * 修改日期:
 */

package io.box.db.executor.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.anyway.server.dbase.Providers.db.CJdbcPool;

public abstract class DbExecutorImpl implements Runnable {
	
	/**
	 * 执行重写
	 */
	@Override
	public void run() {
		
	}
	
	/**
	 * 获取数据库连接句柄
	 * @return
	 */
	public Connection getConnection() {
		Connection connection = null;
		try {
			connection = CJdbcPool.datasource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}
}
