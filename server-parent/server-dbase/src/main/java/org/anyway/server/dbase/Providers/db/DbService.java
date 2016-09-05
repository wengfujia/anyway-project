package org.anyway.server.dbase.Providers.db;

import java.sql.SQLException;

import org.anyway.server.data.models.UserBean;
import org.anyway.server.dbase.cache.DBCache;

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
	public static UserBean SelectUser(int id) throws SQLException, Exception {
		DbProvider db = new DbProvider();
		return db.SelectUser(id);
	}
	/**
	 * 根据user删除一行记录
	 * @param user
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public static void DeleteUser(UserBean user) throws SQLException, Exception {		
		DbProvider db = new DbProvider();
		db.DeleteUser(user);
		//从缓存区中移徐
		DBCache.UsersCache.remove(user.getLoginName());
	}
	/**
	 * 根据user更新记录
	 * @param user
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public static void UpdateUser(UserBean user) throws SQLException, Exception {
		DbProvider db = new DbProvider();
		db.UpdateUser(user);
		//更新缓存区
		DBCache.UsersCache.put(user.getLoginName(), user);		
	}
	/**
	 * 取出所有的User记录
	 * @return void
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public static void FillUsers() throws SQLException, Exception {
		DbProvider db = new DbProvider();
		db.FillUsers();
	}
}
