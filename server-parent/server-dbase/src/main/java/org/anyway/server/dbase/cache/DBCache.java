/*
 * 名称: DBCache
 * 描述: 数据库缓存类(错误信息，用户信息)
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2013年10月15日
 * 修改日期:2014年6月16日
 * 2014.06.16
 * 类名更改为cache=>dbcache,跟ehcache冲突
 * 2014.06.26
 * 将list改为ehcache
 */

package org.anyway.server.dbase.cache;

import java.sql.SQLException;

import org.anyway.cache.ehcache.EhCacheFactory;
import org.anyway.cache.ehcache.EhCacheWrapper;
import org.anyway.common.types.pstring;
import org.anyway.common.utils.uLogger;
import org.anyway.server.data.models.ErrorDescBean;
import org.anyway.server.data.models.UserBean;
import org.anyway.server.dbase.Providers.db.DbService;
import org.anyway.server.dbase.enums.StatusEnum;

public class DBCache {

	public static EhCacheWrapper<String, UserBean> UsersCache;
	public static EhCacheWrapper<Integer, ErrorDescBean> ErrorsCache;
	private static EhCacheFactory ehcachemanager = null;
	
	//创建缓存
	public static void DO() throws SQLException, Exception {
		if (ehcachemanager == null)  //创建缓存管理器(聊天缓存也用)
			ehcachemanager = new EhCacheFactory();
		
		UsersCache = new EhCacheWrapper<String, UserBean>("UsersCache", ehcachemanager.getManager());
		ErrorsCache = new EhCacheWrapper<Integer, ErrorDescBean>("ErrorsCache", ehcachemanager.getManager());
		
		DbService.FillErrors();
		DbService.FillUsers(); //取消对用户的缓存
	}
	
	public static EhCacheFactory GetEhcacheManager() {
		if (ehcachemanager == null) 
			ehcachemanager = new EhCacheFactory();
		return ehcachemanager;
	}
	
	/**
	 * 检查用户是否合格
	 * @param name
	 * @param pwd
	 * @return StatusEnum
	 */
	public static StatusEnum CheckUser(String name, String pwd) {
		
		StatusEnum result = StatusEnum.LOGINERRORPWD;
		try {
			UserBean user = UsersCache.get(name);
			if (user ==null)
				result = StatusEnum.INVALID;
			else if (user.getState() != StatusEnum.EFFECTIVE.getValue())
				result = StatusEnum.INVALID;
			else if (user.getState() == StatusEnum.LOCK.getValue())
				result = StatusEnum.LOCK;
			else if (user.getLoginName()!=name)
				result = StatusEnum.INVALID;
			else if (user.getLoginName()==name && user.getPassword()!=pwd)
				result = StatusEnum.LOGINERRORPWD;
			else
				result = StatusEnum.LOGINSUCESS;
		} catch (Exception E) {
			result = StatusEnum.LOGINEXCEPTION;
			uLogger.println(E.getMessage());
		}
		return result;
	}
	  
	/**
	 * 根据错误代码，获取错误解释
	 * @param err
	 * @param description
	 * @param response
	 * @return
	 */
	public static int GetErrorInfo(int err, pstring description, pstring response) {
		int Result = 0;
		try {
			ErrorDescBean error = ErrorsCache.get(err);
			if (error!=null)
			{
				description.setString(error.getDescription());
				response.setString(error.getResponse());
			}
		} catch (Exception E) {
			Result = -21;
			uLogger.printInfo(E.getMessage());
		}
	
	  return Result;	
	}
}
