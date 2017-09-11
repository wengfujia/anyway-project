/*
 * 名称: ThreadCache
 * 描述: 线程池类（hbase服务端连接池、微信连接池）
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年05月28日
 * 
 * 修改：
 * 		2015.12.3
 * 		取消待处理缓存，统一由已处理缓存存放
 */

package org.anyway.server.adapter.cache;

import java.util.List;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Results;

import org.anyway.exceptions.NoCacheException;
import org.anyway.cache.ehcache.EhCacheFactory;
import org.anyway.cache.ehcache.EhCacheWrapper;
import org.anyway.common.protocol.request.BaseRequest;

public class RequestCache {
	
	private EhCacheFactory ehcachemanager = null;
	private EhCacheWrapper<String, BaseRequest> donecache; //已处理缓存
	
	/**
	 * 构造函数
	 * @throws Exception 
	 */
	public RequestCache(EhCacheFactory manager) throws NoCacheException {
		ehcachemanager = null;
		if (null != manager) {
			this.ehcachemanager = manager;
				donecache = new EhCacheWrapper<String, BaseRequest>("requestDoneCache", ehcachemanager.getManager());
		}
		else throw new NoCacheException("manager不能为空，ThreadCache线程缓存池创建失败！");
	}
	
	/**
	 * 获取已处理缓存
	 * @return
	 */
	public EhCacheWrapper<String, BaseRequest> doneCache() {
		if (null == donecache) {
			synchronized(EhCacheWrapper.class) {
				if (null == donecache) {
					donecache = new EhCacheWrapper<String, BaseRequest>("requestDoneCache", ehcachemanager.getManager());
				}
			}
		}

		return donecache;
	}
	
	/**
	 * 获取已处理keys
	 * @return
	 */
	public List<?> getDoneKeys() {
		return donecache.getCache().getKeys();
	}
	
	/**
	 * 添加连接到连接池
	 * @param reqest
	 */
	public void addDone(BaseRequest request) {
		donecache.put(request.getID(), request);
	}
	
	/**
	 * 更新连接池
	 * @param reqest
	 */
	public void replaceDone(BaseRequest request) {
		donecache.replace(request.getID(), request);
	}
	
	/**
	 * 删除元素，并触发remove事件
	 * @param key
	 */
	public void removeDone(String key) {
		donecache.remove(key);
	}
	
	/**
	 * 删除元素，不触发remove事件
	 * @param key
	 */
	public void removeQuiteDone(String key) {
		donecache.removeQuiet(key);
	}
	
	/**
	 * 查询超时未应答连接
	 * @param timeouts
	 * @return
	 */
	public Results queryTimeOut(int timeouts) {
		Ehcache cache = donecache.getCache(); 	//获取缓存
		Query query = cache.createQuery();		//创建查询器
		//获取查询信息
		Attribute<Integer> timeoutsAttr = cache.getSearchAttribute("times"); //超时时间
		Attribute<Integer> statusAttr = cache.getSearchAttribute("status");	//状态
		//设置查询条件
		query.includeValues(); //设置只从值中搜索
		query.addCriteria(timeoutsAttr.ge(timeouts)); 	//超时时间
		query.addCriteria(statusAttr.le(1));	//设置等待处理或等待应答状态
		query.maxResults(50);	//设置最大获取查询数
		//查询并处理结果集
		Results results = query.execute();
		return results;
	}
	
}
