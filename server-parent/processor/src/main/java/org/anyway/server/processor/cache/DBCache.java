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

package org.anyway.server.processor.cache;

import java.sql.SQLException;

import org.anyway.cache.ehcache.EhCacheFactory;
import org.anyway.cache.ehcache.EhCacheWrapper;
import org.anyway.common.SystemConfig;
import org.anyway.common.models.ErrorDescBean;
import org.anyway.common.types.pstring;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.plugin.processor.pool.DataSourcePool;
import org.anyway.server.processor.Providers.db.DbService;

public class DBCache {

	public static EhCacheWrapper<String, ErrorDescBean> ErrorsCache;
	private static EhCacheFactory ehcachemanager = null;
	
	//创建缓存
	public static void DO() throws SQLException, Exception {
		if (ehcachemanager == null)  //创建缓存管理器(聊天缓存也用)
			ehcachemanager = new EhCacheFactory();
		
		ErrorsCache = new EhCacheWrapper<String, ErrorDescBean>("ErrorsCache", ehcachemanager.getManager());
		
		DbService.FillErrors();
	}
	
	public static EhCacheFactory GetEhcacheManager() {
		if (ehcachemanager == null) 
			ehcachemanager = new EhCacheFactory();
		return ehcachemanager;
	}
	
	/**
	 * 根据错误代码，获取错误解释
	 * @param sessionid
	 * @param commandid
	 * @param errorcode
	 * @param description
	 * @param response
	 * @return
	 */
	public static int GetErrorInfo(String sessionid, int commandid, int errorcode, pstring description, pstring response) {
		int Result = 0;
		try {
			String dataName = DataSourcePool.getInstance().getDataSourceName(sessionid, commandid);
			ErrorDescBean error = ErrorsCache.get(errorcode + SystemConfig.KEY_SEPATATE + dataName);
			if (error!=null)
			{
				description.setString(error.getDescription());
				response.setString(error.getResponse());
			}
		} catch (Exception E) {
			Result = -21;
			LoggerUtil.printInfo(E.getMessage());
		}
	
	  return Result;	
	}
}
