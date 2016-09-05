package org.anyway.server.dbase.Providers.db;
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

import org.anyway.server.data.models.UserBean;

public interface Provider {
	
	/**
	 * ry_errors表的操作
	 */
	/** 取出所有的TbError记录
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void FillErrors() throws SQLException, Exception;
	
	/**
	 * ry_users表的操作
	 */
	/**
	 * 根据rowid查找User
	 * @param id
	 * @return User
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public UserBean SelectUser(int id) throws SQLException, Exception;
	/**
	 * 根据user删除一行记录
	 * @param user
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void DeleteUser(UserBean user) throws SQLException, Exception;
	/**
	 * 根据user更新记录
	 * @param user
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void UpdateUser(UserBean user) throws SQLException, Exception;
	/**
	 * 取出所有的User记录
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void FillUsers() throws SQLException, Exception;
	
}
