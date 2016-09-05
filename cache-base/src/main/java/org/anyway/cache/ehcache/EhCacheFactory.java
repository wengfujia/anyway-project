package org.anyway.cache.ehcache;

import net.sf.ehcache.CacheManager;

/*
 * 名称: EhCacheFactory
 * 描述: 初始化ehcache缓存
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2014年6月12日
 * 修改日期:
 */
public class EhCacheFactory {
	private CacheManager manager = null;
	
	public EhCacheFactory() {
		if (manager == null) {
			manager = CacheManager.newInstance("./ehcache/ehcache.xml");
		}
	}
	
	public EhCacheFactory(CacheManager Obj) {
		if (manager != null) {
			manager.clearAll();
			manager = null;
		}
		manager = Obj;
	}
	
	public CacheManager getManager() {
		return this.manager;
	}
}
