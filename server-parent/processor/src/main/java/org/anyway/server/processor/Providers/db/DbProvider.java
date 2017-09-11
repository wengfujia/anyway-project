package org.anyway.server.processor.Providers.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.anyway.common.SystemConfig;
import org.anyway.common.models.ErrorDescBean;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.plugin.processor.pool.DataSourcePool;
import org.anyway.plugin.processor.pool.DataSourcePool.DataSource;
import org.anyway.server.processor.cache.DBCache;

public class DbProvider implements Provider{

	/**
	 * 读取错误定义表
	 */
	@Override
	public void FillErrors() throws SQLException, Exception {
		Map<String, DataSource> dataSources = DataSourcePool.getInstance().getDataSources();
		for (Map.Entry<String, DataSource> entry : dataSources.entrySet()) {
			Connection connection = null;
			try {
				connection = entry.getValue().getDataSource().getConnection();
				if (connection != null && !connection.isClosed()) {
					Statement cs = connection.createStatement();
					ResultSet rs = cs.executeQuery("select id,ErrorCode,Description,Response from anyway_error order by id desc");
					// 获取记录集
					while (rs != null && rs.next()) {
						ErrorDescBean tberror = new ErrorDescBean();
						tberror.setRowID(rs.getInt(1));
						tberror.setErrorCode(rs.getInt(2));
						tberror.setDescription(rs.getString(3));
						tberror.setResponse(rs.getString(4));
						DBCache.ErrorsCache.put(tberror.getErrorCode() + SystemConfig.KEY_SEPATATE + entry.getKey(), tberror);
					}
					rs.close();
					cs.close();
				}
			} catch (Exception e) {
				LoggerUtil.println(e.getMessage());
			} finally {
				if (connection != null)
					try {
						connection.close();
					} catch (Exception ignore) {
					}
			}
		}
	}

}
