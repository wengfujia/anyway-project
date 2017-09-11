/**
 * 
 */
package org.anyway.plugin.processor;

import java.sql.Connection;
import java.sql.SQLException;

import org.anyway.common.utils.LoggerUtil;
import org.anyway.plugin.processor.pool.DataSourcePool;

/**
 * @author wengfj
 *
 */
public abstract class BaseExecutor {

	/**
	 * 获取连接池
	 * @param sessionid
	 * @param commandid
	 * @return
	 */
	protected Connection getConnection(String sessionid, int commandid) {
		try {
			return DataSourcePool.getInstance().getConnection(sessionid, commandid);
		} catch (SQLException e) {
			LoggerUtil.getLogger().error("获取连接池出错:{}", e);
		}
		return null;
	}
	
}
