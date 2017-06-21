/*
 * 名称: CacheManager
 * 描述: 缓存管理类
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 */

package org.anyway.server.web.cache;

import org.anyway.common.types.pstring;
import org.anyway.exceptions.NoCacheException;
import org.anyway.server.data.models.ErrorDescBean;
import org.anyway.server.data.models.IpTableBean;
import org.anyway.server.web.providers.DbService;
import org.anyway.cache.ehcache.EhCacheFactory;

public class CacheManager {

	private static CacheManager INSTANCE;
	
	private EhCacheFactory ehcachemanager = null;
	
	private DbCache dbcache = null;
	private HttpCache httpcache = null;
	private ConfigCache configcache = null;

	/**
	 * 获取Instance
	 * @return Instance
	 * @throws NoCacheException 
	 * @throws Exception 
	 */
	public  static CacheManager getInstance() throws NoCacheException {
		synchronized(CacheManager.class) {
			if (INSTANCE == null) {
				INSTANCE = new CacheManager();
			}
			return INSTANCE;
		}
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
		this.httpcache = new HttpCache(ehcachemanager);
		this.configcache = new ConfigCache(ehcachemanager);
		
		//创建数据为缓存
		this.dbcache = new DbCache();
	}
	
	/**
	 * 构造函数
	 * @param ehcachemanager
	 * @throws Exception
	 */
	private CacheManager(EhCacheFactory ehcachemanager) throws Exception {
		//创建线程池
		this.ehcachemanager = ehcachemanager;
		this.httpcache = new HttpCache(ehcachemanager);
		this.configcache = new ConfigCache(ehcachemanager);
		
		//创建数据为缓存
		this.dbcache = new DbCache();
	}
	
	/**
	 * 获取xml数据文件缓存
	 * @return
	 */
	public DbCache getDbCache() {
		return this.dbcache;
	}
	
	/**
	 * 获取http连接池缓存
	 * @return
	 */
	public HttpCache getHttpCache() {
		return this.httpcache;
	}
	
	/**
	 * 获取配置缓存
	 * @return
	 */
	public ConfigCache getConfigCache() {
		return this.configcache;
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
	 * 创建缓存
	 * @throws NoCacheException 
	 */
	public void DO() throws NoCacheException {
		DbService.FillCategories();	//各类消息分类
		DbService.FillErrors();		//加载错误定义列表
		DbService.FillIpTables(); 	//加载hbase服务列表
		DbService.FillStopWords(); 	//加载关键字列表
	}
	
	/**
	 * 获取地址表
	 * 获取当前线程数最小值
	 * @return
	 */
	public IpTableBean getIpTable() {
		IpTableBean iptable = null;
		for (IpTableBean ip:this.dbcache.IpTablesCache().values()) {
			if (ip.isSucess()) {
				if (null == iptable || ip.getValidthreads()>iptable.getValidthreads()) {
					iptable = ip;
				}
			}
		}
		//增加线程
		if (null != iptable) {
			iptable.addCurthreads();
			//已经对变量做了线程同步
//			synchronized(iptable) {
//				iptable.addCurthreads();
//			}	
		}
		
//		//排序
//		List<Map.Entry<String, IpTableBean>> sortList =
//			    new ArrayList<Map.Entry<String, IpTableBean>>(this.dbcache.IpTablesCache().entrySet());
//		Collections.sort(sortList, new Comparator<Map.Entry<String, IpTableBean>>() {   
//		    public int compare(Map.Entry<String, IpTableBean> o1, Map.Entry<String, IpTableBean> o2) { 
//		    	return (o1.getValue().getValidthreads() - o2.getValue().getValidthreads()); 
//		        //return (o1.getKey()).toString().compareTo(o2.getKey());
//		    }
//		}); 
//		iptable = sortList.get(0).getValue();	
//		iptable.addCurthreads();
		
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
			ErrorDescBean error = this.dbcache.ErrorDescsCache().get(err);
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
