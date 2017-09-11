/*
 * 名称: test
 * 描述: 这是一个测试类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年12月14日
 * 修改日期:
 */

package io.box.db.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.anyway.common.MessageAnnotation;
import org.anyway.common.uConfigVar;
import org.anyway.common.utils.uLogger;
import org.anyway.server.dbase.cache.DBCache;
import org.anyway.server.data.models.ErrorDescBean;
import org.anyway.server.data.packages.COMMANDID;

import io.box.db.executor.impl.DbExecutorImpl;

@MessageAnnotation(msgType = COMMANDID.TEST)
public final class test extends DbExecutorImpl {
	
	/**
	 * 执行重写
	 */
	@Override
	public void run() {
		Connection connection = super.getConnection();
		try {
			try {
				if (connection != null && !connection.isClosed()) {
					
					Statement cs = connection.createStatement();
					ResultSet rs = cs.executeQuery("select id,ErrorCode,Description,Response from "+uConfigVar.TbERROR+" order by id desc");					
					//获取记录集
			        while (rs != null && rs.next()) {  
			        	ErrorDescBean tberror = new ErrorDescBean();
			        	tberror.setRowID(rs.getInt(1));
			        	tberror.setErrorCode(rs.getInt(2));
			        	tberror.setDescription(rs.getString(3));
			        	tberror.setResponse(rs.getString(4));
			        	DBCache.ErrorsCache.put(tberror.getErrorCode(), tberror);
			        } 
			        rs.close();
			        cs.close();
				}
			} catch (Exception e) {
				uLogger.println(e.getMessage());
			}
		}
		finally {
			if (connection!=null) try {connection.close();}catch (Exception ignore) {}
		}
	}
}
