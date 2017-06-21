/*
 * 名称: CMEvent
 * 描述: CahceManager监听事件
 * 不作处理
 * 版本：  1.0.0
 * 作者： 翁富家
 * 修改:
 * 日期：2014年6月16日
 * 修改日期:
 */

package org.anyway.cache.ehcache.event;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Status;
import net.sf.ehcache.event.CacheManagerEventListener;

public class CMEvent implements CacheManagerEventListener {
	
	@Override
	public void dispose() throws CacheException {}
	
	@Override
    public Status getStatus() {return null;}
    
    @Override
    public void init() throws CacheException {
    }
    
    @Override
    public void notifyCacheAdded(String cacheName) {
    }
    
    @Override
    public void notifyCacheRemoved(String cacheName) {

    }
}
