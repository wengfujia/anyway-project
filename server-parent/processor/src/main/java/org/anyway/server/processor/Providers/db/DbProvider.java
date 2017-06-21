package org.anyway.server.processor.Providers.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.anyway.common.uConfigVar;
import org.anyway.common.models.ErrorDescBean;
import org.anyway.common.models.UserBean;
import org.anyway.common.utils.DateUtil;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.StringUtil;
import org.anyway.server.processor.cache.DBCache;

public class DbProvider implements Provider{

	/**
	 * 读取错误定义表
	 */
	@Override
	public void FillErrors() throws SQLException, Exception {
		if (StringUtil.empty(uConfigVar.TbERROR)) {
			return;
		}
		
		Connection connection = null;
		try {
			try {
				connection = CJdbcPool.datasource.getConnection();
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
				LoggerUtil.println(e.getMessage());
			}
		}
		finally {
			if (connection!=null) try {connection.close();}catch (Exception ignore) {}
		}
	}

	/**
	 * 根据条件读取一条用户记录
	 */
	@Override
	public UserBean SelectUser(int id) throws SQLException, Exception {
		Connection connection = null;
		UserBean user = new UserBean();
		try {
			try {
				connection = CJdbcPool.datasource.getConnection();
				if (connection != null && !connection.isClosed()) {
					String sql = "select UserID,LoginName,Password,LastLoginTime,LastLoginIP,State " +
							"from "+uConfigVar.TbUSER+" where UserID=" + String.valueOf(id) + "";
					Statement cs = connection.createStatement();
					ResultSet rs = cs.executeQuery(sql);	
					if (rs != null) {
				        user.setRowID(rs.getInt(1));
				        user.setLoginName(rs.getString(2));
				        user.setPassword(rs.getString(3));
				        user.setLastLoginTime(rs.getDate(4)==null?DateUtil.getDateTime():rs.getDate(4));
				        user.setLastLoginIP(rs.getString(5));
				        user.setState(rs.getInt(6));
					}
					rs.close();
					cs.close();
				}
			} catch (Exception e) {
				LoggerUtil.println(e.getMessage());
			}
		}
		finally {
			if (connection!=null) try {connection.close();}catch (Exception ignore) {}
		}
		return user;
	}

	/**
	 * 删除用户
	 */
	@Override
	public void DeleteUser(UserBean user) throws SQLException, Exception {
		Connection connection = null;
		try {
			try {
				connection = CJdbcPool.datasource.getConnection();
				if (connection != null && !connection.isClosed()) {
					String sql = "delete form "+uConfigVar.TbUSER+" where UserID="+ String.valueOf(user.getRowID())+"";
					Statement cs = connection.createStatement(); 
					cs.executeUpdate(sql);	
					cs.close();
				}
				
			} catch (Exception e) {
				LoggerUtil.println(e.getMessage());
			}
		}
		finally {
			if (connection!=null) try {connection.close();}catch (Exception ignore) {}
		}
	}

	/**
	 * 更新用户
	 */
	@Override
	public void UpdateUser(UserBean user) throws SQLException, Exception {
		Connection connection = null;
		try {
			try {
				connection = CJdbcPool.datasource.getConnection();
				if (connection != null && !connection.isClosed()) {
					String sql = "update "+uConfigVar.TbUSER+" set " +
							"LoginName='"+user.getLoginName()+"',Password='"+user.getPassword()+"'," +
							"LastLoginTime='"+user.getLastLoginTime()+"',LastLoginIP='"+user.getLastLoginIP()+"',State="+user.getState()+" " +
							"where UserID="+user.getRowID()+"";
					Statement cs = connection.createStatement();				
					cs.executeUpdate(sql);		
					cs.close();
				}
			} catch (Exception e) {
				LoggerUtil.println(e.getMessage());
			}
		}
		finally {
			if (connection!=null) try {connection.close();}catch (Exception ignore) {}
		}
		
	}

	/**
	 * 读取用户记录集
	 */
	@Override
	public void FillUsers() throws SQLException, Exception {
		if (StringUtil.empty(uConfigVar.TbUSER)) {
			return;
		}
		
		Connection connection = null;
		try {
			try {
				connection = CJdbcPool.datasource.getConnection();
				if (connection != null && !connection.isClosed()) {
					
					Statement cs = connection.createStatement();					
					ResultSet rs = cs.executeQuery("select UserID,LoginName,Password,LastLoginTime,LastLoginIP,State " +
							"from "+uConfigVar.TbUSER+""); 				 
			        while (rs != null && rs.next()) {  			        	
			        	UserBean user = new UserBean();
			        	user.setRowID(rs.getInt(1));
				        user.setLoginName(rs.getString(2));
				        user.setPassword(rs.getString(3));
				        user.setLastLoginTime(rs.getDate(4)==null?DateUtil.getDateTime():rs.getDate(4));
				        user.setLastLoginIP(rs.getString(5));
				        user.setState(rs.getInt(6));
				        DBCache.UsersCache.put(user.getLoginName(), user);
			        }  
			        cs.close();
			        rs.close();
				}
			} catch (Exception e) {
				LoggerUtil.println(e.getMessage());
			}
		}
		finally {
			if (connection!=null) try {connection.close();}catch (Exception ignore) {}
		}
	}

}
