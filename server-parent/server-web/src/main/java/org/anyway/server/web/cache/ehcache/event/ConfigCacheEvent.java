/*
 * 名称: ConfigCacheEvent
 * 描述: config连接线程池事件
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年6月4日
 * 修改日期:
 */

package org.anyway.server.web.cache.ehcache.event;

import org.anyway.exceptions.NoCacheException;
import org.anyway.server.web.cache.CacheManager;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

public class ConfigCacheEvent implements CacheEventListener {
	
	@Override
	public void dispose() {
	}
	
	@Override
    public void notifyElementEvicted(Ehcache cache, Element element) {
	}
    
    /**
     * 缓存过期
     * 释放连接
     */
    @Override
    public void notifyElementExpired(Ehcache cache, Element element) {
    	//如果是微信则进行重获
    	if (cache.getName().equals("WeixinConfigCache")) {
    		try {
    			Element ele = new Element(element.getObjectKey(), element.getObjectValue());
				cache.put(ele);
				//获取accesstoken
				CacheManager.getInstance().getConfigCache().getTokenFormWeixin(ele.getObjectKey());
			} catch (NoCacheException e) {
				
			}
    	}
    }
    
    /**
     * 缓存清除
     * @param cache
     */
    @Override
    public void notifyRemoveAll(Ehcache cache) {
    }

    /**
     * 缓存删除
     * 释放连接
     */
    @Override
    public void notifyElementRemoved(Ehcache cache, Element element)
             throws CacheException {
    }
    
    /**
     * 缓存增加
     * 判断是否启用触发发送
     */
    @Override
    public void notifyElementPut(Ehcache cache, Element element)
            throws CacheException {
    }
    
    @Override
    public void notifyElementUpdated(Ehcache cache, Element element)
             throws CacheException {
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}