/*
 * 名称: HttpCacheEvent
 * 描述: http连接线程池事件
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2015年6月4日
 * 修改日期:
 */

package org.anyway.server.web.ehcache.event;

import org.anyway.server.data.packages.HTTPREQUEST;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

public class HttpCacheEvent implements CacheEventListener {
	
	@Override
	public void dispose() {
	}
	
	@Override
    public void notifyElementEvicted(Ehcache cache, Element element) {
    	@SuppressWarnings("unchecked")
		HTTPREQUEST<String> request = (HTTPREQUEST<String>)element.getObjectValue();
    	close(request);
	}
    
    /**
     * 缓存过期
     * 释放连接
     */
    @Override
    public void notifyElementExpired(Ehcache cache, Element element) {
    	//获取连接，关闭
    	@SuppressWarnings("unchecked")
		HTTPREQUEST<String> request = (HTTPREQUEST<String>)element.getObjectValue();
    	close(request);
    }
    
    /**
     * 缓存清除
     * @param cache
     */
    @Override
    public void notifyRemoveAll(Ehcache cache) {
    	//关闭连接池中的所有连接
    	for (Object key : cache.getKeys()) {
    		@SuppressWarnings("unchecked")
			HTTPREQUEST<String> request = (HTTPREQUEST<String>)cache.get(key).getObjectValue();
    		close(request);
    	}
    }

    /**
     * 缓存删除
     * 释放连接
     */
    @Override
    public void notifyElementRemoved(Ehcache cache, Element element)
             throws CacheException {
    	@SuppressWarnings("unchecked")
		HTTPREQUEST<String> request = (HTTPREQUEST<String>) element.getObjectValue();
    	close(request);
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
    
    /**
     * 关闭连接
     * @param request
     */
    private void close(HTTPREQUEST<String> request) {
    	if (null != request) {
    		if (null != request.getIpTable()) {
    			request.getIpTable().decCurthreads();
    		}
        	request.Close();
    	}
    }
}