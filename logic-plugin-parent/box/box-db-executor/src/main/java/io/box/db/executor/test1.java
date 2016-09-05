package io.box.db.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.anyway.common.uConfigVar;
import org.anyway.common.utils.uLogger;
import org.anyway.server.data.contracts.IChrList;
import org.anyway.server.data.packages.HEADER;
import org.anyway.server.data.packages.COMMANDID;
import org.anyway.server.dbase.Providers.db.CJdbcPool;

import io.box.db.executor.impl.MessageAnnotation;

@MessageAnnotation(msgType = COMMANDID.TEST)
public class test1 {
	/*
	 * 在原包后面需要添加班级序号
	 * */
	public int decode(HEADER header, byte[] nr, IChrList list) {
		Connection connection = null;
		try {
			connection = CJdbcPool.datasource.getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			try {
				if (connection != null && !connection.isClosed()) {
					
					Statement cs = connection.createStatement();
					ResultSet rs = cs.executeQuery("select id,ErrorCode,Description,Response from "+uConfigVar.TbERROR+" order by id desc");					
					//获取记录集
			        while (rs != null && rs.next()) {  
			        	list.Append(rs.getString(3));
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
		return 0;
	}
}
