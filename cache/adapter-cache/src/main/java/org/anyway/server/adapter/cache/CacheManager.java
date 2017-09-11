/*
 * 名称: CacheManager
 * 描述: 缓存管理类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package org.anyway.server.adapter.cache;

import org.anyway.common.SystemConfig;
import org.anyway.common.models.ErrorDescBean;
import org.anyway.common.models.IpTableBean;
import org.anyway.common.types.pstring;
import org.anyway.common.utils.LoggerUtil;
import org.anyway.common.utils.StringUtil;
import org.anyway.exceptions.NoCacheException;
import org.anyway.cache.ehcache.EhCacheFactory;

public class CacheManager {

	private static CacheManager INSTANCE;
	
	private EhCacheFactory ehcachemanager = null;
	
	private RequestCache requestCache = null;
	private ConfigCache configCache = null;

	/**
	 * 获取Instance
	 * @return Instance
	 * @throws NoCacheException 
	 * @throws Exception 
	 */
	public  static CacheManager getInstance() throws NoCacheException {
		if (INSTANCE == null) {
			synchronized(CacheManager.class) {
				INSTANCE = new CacheManager();
			}
		}
		return INSTANCE;
	}

	/**
	 * 创建Instance
	 * @param ehcachemanager
	 * @return Instance
	 * @throws Exception 
	 */
	public static CacheManager getInstance(EhCacheFactory ehcachemanager) throws Exception {
		synchronized(CacheManager.class) {
			if (INSTANCE == null) {
				INSTANCE = new CacheManager(ehcachemanager);
			}
			return INSTANCE;
		}
	}

	/**
	 * 构造函数
	 * @throws NoCacheException 
	 * @throws Exception
	 */
	private CacheManager() throws NoCacheException {
		//创建线程池
		if (ehcachemanager == null) 
			ehcachemanager = new EhCacheFactory();
		this.requestCache = new RequestCache(ehcachemanager);
		this.configCache = new ConfigCache(ehcachemanager);
	}
	
	/**
	 * 构造函数
	 * @param ehcachemanager
	 * @throws Exception
	 */
	private CacheManager(EhCacheFactory ehcachemanager) throws Exception {
		//创建线程池
		this.ehcachemanager = ehcachemanager;
		this.requestCache = new RequestCache(ehcachemanager);
		this.configCache = new ConfigCache(ehcachemanager);
	}
	
	/**
	 * 获取连接池缓存
	 * @return
	 */
	public RequestCache getRequestCache() {
		return this.requestCache;
	}
	
	/**
	 * 获取配置缓存
	 * @return
	 */
	public ConfigCache getConfigCache() {
		return this.configCache;
	}
	
	/**
	 * ehcache管理类
	 * @return
	 */
	public EhCacheFactory getEhcacheManager() {
		if (ehcachemanager == null) 
			ehcachemanager = new EhCacheFactory();
		return ehcachemanager;
	}	

	/**
	 * 获取地址表
	 * 获取当前线程数最小值
	 * @param sessionId
	 * @param commandId
	 * @return
	 */
	public IpTableBean getRoute(String sessionId, String commandId) {
		IpTableBean iptable = null;
		//1.根据业务标识获取路处理层路由信息
		String iptableName = this.configCache.getCommandIdRouteCache()
				.get(commandId + SystemConfig.KEY_SEPATATE + sessionId);
		if (!StringUtil.empty(iptableName)) {
			iptable = this.configCache.getRoutesCache().get(iptableName);
		} else {
			// 2.获取当前线程最小的处理层
			for (IpTableBean ip : this.configCache.getRoutesCache().values()) {
				if (ip.isSucess()) {
					if (null == iptable || ip.getValidthreads() > iptable.getValidthreads()) {
						iptable = ip;
					}
				}
			}
		}
		
		//增加线程
		if (null != iptable) {
			int threads = iptable.incCurthreads();
			LoggerUtil.println("Curthreads ip:%s,port:%s,threads:%d,maxThreads:%d", iptable.getAddress(),
					iptable.getPort(), threads, iptable.getMaxthreads());
		}
		
		return iptable;
	}
	
	/**
	 * 根据错误代码，获取错误解释
	 * @param err
	 * @param description
	 * @param response
	 * @return
	 */
	public int GetErrorInfo(int err, pstring description, pstring response) {
		int Result = 0;
		try {
			ErrorDescBean error = this.configCache.getErrorDescsCache().get(err);
			if (error!=null)
			{
				description.setString(error.getDescription());
				response.setString(error.getResponse());
			}
		} catch (Exception E) {
			Result = -201;
		}
	
	  return Result;	
	}
	
}
